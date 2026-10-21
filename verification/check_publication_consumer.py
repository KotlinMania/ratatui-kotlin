"""Check real staged Android metadata and independent consumer execution."""
import argparse
import hashlib
import io
import json
import re
import struct
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path

from verify_android_publication import require, verify, related_path

GROUP = "io.github.kotlinmania"
MODULE = "ratatui-kotlin"
CLASSES = set()  # The facade exposes real crate classes through api(project(...)).
CORE_EDGES = [(GROUP, "bitflags-kotlin", "bitflags-kotlin-android"),
              (GROUP, "anstyle-kotlin", "anstyle-kotlin-android"),
              (GROUP, "unicode-width-kotlin", "unicode-width-kotlin-android"),
              (GROUP, "serde-core", "serde-core-android")]
EDGES = [(GROUP, module, module + "-android")
         for module in ["ratatui-core", "ratatui-widgets", "ratatui-macros"]]
COMPONENTS = {
    MODULE: (CLASSES, EDGES),
    "ratatui-core": ({"ratatui/text/Text.class", "ratatui/style/Modifier.class",
                      "ratatui/style/ParseColorError.class", "ratatui/style/Color$Companion.class",
                      "ratatui/style/TryFromColorError.class", "ratatui/style/ColorParseResult.class",
                      "ratatui/style/Ansi256ColorResult.class", "ratatui/style/AnsiColorResult.class",
                      "ratatui/style/RgbColorResult.class"}, CORE_EDGES),
    "ratatui-widgets": ({"ratatui/widgets/list/ListState.class", "ratatui/widgets/table/TableState.class",
                         "ratatui/widgets/scrollbar/ScrollbarState.class"},
                        [(GROUP, "ratatui-core", "ratatui-core-android")]),
    "ratatui-macros": ({"ratatui_macros/SpanKt.class", "ratatui_macros/RowKt.class"},
                       [(GROUP, "ratatui-core", "ratatui-core-android"),
                        (GROUP, "ratatui-widgets", "ratatui-widgets-android")]),
}

TESTS = {"publishedTextUsesUnicodeWidth", "publishedModifierExposesBitflags",
         "publishedErrorsRetainRustDiagnostics", "publishedColorRetainsItsDeserializeContract"}


def class_hierarchy(payload):
    require(payload[:4] == bytes.fromhex("cafebabe"), "Invalid class payload")
    cursor = 8
    def number():
        nonlocal cursor
        value = struct.unpack_from(">H", payload, cursor)[0]
        cursor += 2
        return value
    count = number()
    pool = {}
    index = 1
    while index < count:
        tag = payload[cursor]
        cursor += 1
        if tag == 1:
            size = number()
            pool[index] = payload[cursor:cursor + size].decode("utf-8", errors="replace")
            cursor += size
        elif tag == 7:
            pool[index] = number()
        elif tag in [8, 16, 19, 20]:
            cursor += 2
        elif tag in [3, 4, 9, 10, 11, 12, 17, 18]:
            cursor += 4
        elif tag in [5, 6]:
            cursor += 8
            index += 1
        elif tag == 15:
            cursor += 3
        else:
            raise ValueError(f"Unknown class constant tag: {tag}")
        index += 1
    number()  # access flags
    own = number()
    parent = number()
    interfaces = [number() for _ in range(number())]
    return {"class": pool[pool[own]], "superclass": pool[pool[parent]],
            "interfaces": [pool[pool[i]] for i in interfaces]}


def verify_diagnostic_payloads(archive, version):
    expected = {"ParseColorError": "java/lang/Object", "TryFromColorError": "java/lang/Enum"}
    result = {}
    android = archive.read(metadata_path("ratatui-core", version, "-android") + ".aar")
    with zipfile.ZipFile(io.BytesIO(android)) as aar:
        android_jar = aar.read("classes.jar")
    for platform, payload in [("android", android_jar),
                              ("jvm", archive.read(metadata_path("ratatui-core", version, "-jvm") + ".jar"))]:
        result[platform] = {}
        with zipfile.ZipFile(io.BytesIO(payload)) as jar:
            for name, parent in expected.items():
                shape = class_hierarchy(jar.read("ratatui/style/" + name + ".class"))
                require(shape["class"] == "ratatui/style/" + name and shape["superclass"] == parent
                        and not shape["interfaces"], f"Leaked diagnostic inheritance: {name}")
                result[platform][name] = shape
    return result


def metadata_path(module, version, suffix=""):
    child = module + suffix
    return f"{GROUP.replace('.', '/')}/{child}/{version}/{child}-{version}"


def selected_variants(metadata, platform):
    variants = [v for v in metadata["variants"]
                if v.get("attributes", {}).get("org.jetbrains.kotlin.platform.type") == platform
                and v["attributes"].get("org.gradle.category") == "library"]
    require(len(variants) == 2 and {v["attributes"].get("org.gradle.usage") for v in variants}
            == {"java-api", "java-runtime"}, f"Missing {platform} API/runtime variants")
    return variants


def verify_project_edges(archive, module, version, edges):
    # Sibling project edges must select the exact same staged source version,
    # in API as well as runtime metadata and compile-scope POM dependencies.
    internal = {root for group, root, _ in edges if group == GROUP and root in COMPONENTS}
    ns = {"m": "http://maven.apache.org/POM/4.0.0"}
    for suffix, platform in [("-android", "androidJvm"), ("-jvm", "jvm")]:
        base = metadata_path(module, version, suffix)
        metadata = json.loads(archive.read(base + ".module"))
        for variant in selected_variants(metadata, platform):
            dependencies = {(d["group"], d["module"]): d for d in variant.get("dependencies", [])}
            if module == "ratatui-core" and variant["attributes"]["org.gradle.usage"] == "java-api":
                require((GROUP, "thiserror-kotlin") not in dependencies,
                        "Derive tooling must not be a direct public core dependency")
            for child in internal:
                edge = dependencies.get((GROUP, child))
                require(edge is not None and edge.get("version", {}).get("requires") == version,
                        f"Missing/wrong current-version API edge: {module} -> {child}")
        pom = ET.fromstring(archive.read(base + ".pom"))
        dependencies = {(d.findtext("m:groupId", namespaces=ns),
                         d.findtext("m:artifactId", namespaces=ns)): d
                        for d in pom.findall("m:dependencies/m:dependency", ns)}
        for child in internal:
            edge = dependencies.get((GROUP, child + suffix))
            require(edge is not None and edge.findtext("m:scope", namespaces=ns) == "compile"
                    and edge.findtext("m:version", namespaces=ns) == version,
                    f"Missing/wrong compile-scope POM edge: {module} -> {child}")


def verify_jvm(archive, module, version, classes, edges):
    base = metadata_path(module, version, "-jvm")
    root_path = metadata_path(module, version) + ".module"
    path = base + ".module"
    metadata = json.loads(archive.read(path))
    require(metadata.get("formatVersion") == "1.1", "Unsupported JVM metadata format")
    component = metadata["component"]
    require((component.get("group"), component.get("module"), component.get("version"))
            == (GROUP, module, version), "Incorrect JVM owner identity")
    require(related_path(path, component["url"]) == root_path, "Wrong JVM owning component URL")
    root = json.loads(archive.read(root_path))
    for variant in selected_variants(root, "jvm"):
        redirect = variant["available-at"]
        require((redirect.get("group"), redirect.get("module"), redirect.get("version"))
                == (GROUP, module + "-jvm", version)
                and related_path(root_path, redirect["url"]) == path, "Wrong JVM root redirect")
    payload = archive.read(base + ".jar")
    digest = hashlib.sha256(payload).hexdigest()
    for variant in selected_variants(metadata, "jvm"):
        attrs = variant["attributes"]
        require(attrs.get("org.gradle.libraryelements") == "jar"
                and attrs.get("org.gradle.jvm.environment") == "standard-jvm", "Wrong JVM variant")
        files = variant.get("files", [])
        require(len(files) == 1 and related_path(path, files[0]["url"]) == base + ".jar"
                and files[0].get("size") == len(payload) and files[0].get("sha256") == digest,
                "JVM payload hash/path/size mismatch")
        dependencies = {(d["group"], d["module"]) for d in variant.get("dependencies", [])}
        require({(g, m) for g, m, _ in edges} <= dependencies, "Missing JVM public API dependency")
    pom = ET.fromstring(archive.read(base + ".pom"))
    ns = {"m": "http://maven.apache.org/POM/4.0.0"}
    for name, expected in [("groupId", GROUP), ("artifactId", module + "-jvm"), ("version", version)]:
        require(pom.findtext("m:" + name, namespaces=ns) == expected, "Incorrect JVM POM identity")
    require(pom.findtext("m:packaging", default="jar", namespaces=ns) == "jar", "Incorrect JVM packaging")
    with zipfile.ZipFile(io.BytesIO(payload)) as jar:
        require(len(jar.namelist()) == len(set(jar.namelist())), "Duplicate JVM JAR entry")
        require(module != MODULE or not any(n.endswith(".class") for n in jar.namelist()),
                "API facade unexpectedly contains JVM bytecode")
        for name in classes:
            require(name in jar.namelist() and jar.read(name).startswith(bytes.fromhex("cafebabe")),
                    f"Missing real JVM public API bytecode: {name}")
    return {"jar_sha256": digest, "required_classes": sorted(classes)}


def stage(repository, version, bundle):
    require(re.fullmatch(r"[A-Za-z0-9_.+-]+", version), "Invalid version")
    bundle.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(bundle, "w", zipfile.ZIP_DEFLATED) as archive:
        for module in [name for owner in COMPONENTS for name in [owner, owner + "-android", owner + "-jvm"]]:
            root = repository / GROUP.replace(".", "/") / module / version
            require(root.is_dir(), f"Missing current publication: {root}")
            for path in sorted(root.rglob("*")):
                if path.is_file():
                    archive.write(path, path.relative_to(repository))
    result = verify_components(bundle, version)
    source_root = Path(__file__).resolve().parent.parent
    with zipfile.ZipFile(bundle) as archive:
        for module in COMPONENTS:
            owner = source_root if module == MODULE else source_root / module
            jar_path = metadata_path(module, version, "-jvm") + "-sources.jar"
            payload = archive.read(jar_path)
            with zipfile.ZipFile(io.BytesIO(payload)) as sources:
                expected = {str(path.relative_to(owner / "src/commonMain/kotlin")): path
                            for path in (owner / "src/commonMain/kotlin").rglob("*.kt")}
                actual = {name.removeprefix("commonMain/"): name for name in sources.namelist()
                          if name.startswith("commonMain/") and name.endswith(".kt")}
                require(set(actual) == set(expected), f"Stale/missing owned sources: {module}")
                for name, path in expected.items():
                    require(sources.read(actual[name]) == path.read_bytes(), f"Source hash mismatch: {module}/{name}")
            result["components"][module]["jvm"]["sources_sha256"] = hashlib.sha256(payload).hexdigest()
            result["components"][module]["jvm"]["current_source_files"] = len(expected)
    return result


def verify_components(bundle, version):
    receipts = {module: verify(bundle, GROUP, module, version, classes, edges, api_facade=module == MODULE)
                for module, (classes, edges) in COMPONENTS.items()}
    with zipfile.ZipFile(bundle) as archive:
        for module, (classes, edges) in COMPONENTS.items():
            receipts[module]["jvm"] = verify_jvm(archive, module, version, classes, edges)
            verify_project_edges(archive, module, version, edges)
        diagnostics = verify_diagnostic_payloads(archive, version)
    result = dict(receipts[MODULE])
    result["components"] = receipts
    result["diagnostics"] = diagnostics
    return result


def executed_tests(results):
    receipt = {}
    for task in ["testAndroidHostTest", "jvmTest"]:
        files = sorted((results / task).glob("*.xml"))
        require(bool(files), f"No execution XML for {task}")
        cases = [case for path in files for case in ET.parse(path).getroot().iter("testcase")]
        require(len(cases) == len(TESTS), f"Expected four actual consumer tests in {task}")
        names = []
        for case in cases:
            require(case.get("classname", "").endswith("PublicApiConsumerTest"), "Unexpected test class")
            require(not any(case.find(tag) is not None for tag in ["failure", "error", "skipped"]),
                    f"Failed or skipped consumer test: {case.get('name')}")
            names.append(case.get("name", "").split("[", 1)[0])
        require(len(set(names)) == len(TESTS) and set(names) == TESTS, f"Missing or duplicate tests: {task}")
        receipt[task] = {"executed": len(cases), "names": names}
    return receipt


def consumer(build, publication):
    require(publication["status"] == "passed", "Publication proof failed")
    group, module, version = publication["coordinates"].split(":")
    reports = build / "reports/publication"
    variant = json.loads((reports / "selected-variant.json").read_text())
    require((variant["group"], variant["module"], variant["version"]) ==
            (group, module + "-android", version), "Wrong selected Android coordinate")
    duplicates = json.loads((reports / "duplicates.json").read_text())
    require(duplicates["status"] == "passed" and
            duplicates["duplicates_by_configuration"] == {"compile": {}, "runtime": {}},
            "Compile/runtime duplicate check did not pass independently")
    paths = json.loads((reports / "raw-classpaths.json").read_text())
    for owner, component in publication["components"].items():
        for scope in ["compile", "runtime"]:
            matches = [path for path in paths[scope] if Path(path).suffix == ".aar" and
                       hashlib.sha256(Path(path).read_bytes()).hexdigest() == component["aar_sha256"]]
            require(len(matches) == 1, f"Selected {scope} {owner} AAR differs from staged producer")
    jvm_paths = json.loads((reports / "jvm-classpaths.json").read_text())
    for owner, component in publication["components"].items():
        for scope in ["compile", "runtime"]:
            matches = [path for path in jvm_paths[scope] if Path(path).suffix == ".jar" and
                       hashlib.sha256(Path(path).read_bytes()).hexdigest() == component["jvm"]["jar_sha256"]]
            require(len(matches) == 1, f"Selected {scope} {owner} JVM JAR differs from staged producer")
    return {"status": "passed", "coordinates": publication["coordinates"],
            "aar_sha256": publication["aar_sha256"], "tests": executed_tests(build / "test-results"),
            "scope": "Android host and JVM consumer only; other platform runtime gates remain separate"}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest="mode", required=True)
    staged = sub.add_parser("stage")
    staged.add_argument("--repository", required=True, type=Path)
    staged.add_argument("--version", required=True)
    staged.add_argument("--bundle", required=True, type=Path)
    consumed = sub.add_parser("consumer")
    consumed.add_argument("--build", required=True, type=Path)
    consumed.add_argument("--publication", required=True, type=Path)
    parser.add_argument("--receipt", required=True, type=Path)
    args = parser.parse_args()
    result = stage(args.repository, args.version, args.bundle) if args.mode == "stage" else consumer(
        args.build, json.loads(args.publication.read_text()))
    args.receipt.parent.mkdir(parents=True, exist_ok=True)
    args.receipt.write_text(json.dumps(result, indent=2) + "\n")
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
