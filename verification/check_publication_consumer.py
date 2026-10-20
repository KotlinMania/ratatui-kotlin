"""Check real staged Android metadata and independent consumer execution."""
import argparse
import hashlib
import json
import re
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path

from verify_android_publication import require, verify

GROUP = "io.github.kotlinmania"
MODULE = "ratatui-kotlin"
CLASSES = set()  # The facade exposes real crate classes through api(project(...)).
CORE_EDGES = [(GROUP, "bitflags-kotlin", "bitflags-kotlin-android"),
              (GROUP, "thiserror-kotlin", "thiserror-kotlin-android"),
              (GROUP, "unicode-width-kotlin", "unicode-width-kotlin-android"),
              (GROUP, "serde-core", "serde-core-android")]
EDGES = [(GROUP, module, module + "-android")
         for module in ["ratatui-core", "ratatui-widgets", "ratatui-macros"]]
COMPONENTS = {
    MODULE: (CLASSES, EDGES),
    "ratatui-core": ({"ratatui/text/Text.class", "ratatui/style/Modifier.class",
                      "ratatui/style/ParseColorError.class", "ratatui/style/ColorSerializer.class"}, CORE_EDGES),
    "ratatui-widgets": ({"ratatui/widgets/list/ListState.class", "ratatui/widgets/table/TableState.class",
                         "ratatui/widgets/scrollbar/ScrollbarState.class"},
                        [(GROUP, "ratatui-core", "ratatui-core-android")]),
    "ratatui-macros": ({"ratatui_macros/SpanKt.class", "ratatui_macros/RowKt.class"},
                       [(GROUP, "ratatui-core", "ratatui-core-android"),
                        (GROUP, "ratatui-widgets", "ratatui-widgets-android")]),
}

TESTS = {"publishedTextUsesUnicodeWidth", "publishedModifierExposesBitflags",
         "publishedErrorRetainsItsStandardErrorType", "publishedSerializerRetainsItsPublicInterface"}


def stage(repository, version, bundle):
    require(re.fullmatch(r"[A-Za-z0-9_.+-]+", version), "Invalid version")
    bundle.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(bundle, "w", zipfile.ZIP_DEFLATED) as archive:
        for module in [name for owner in COMPONENTS for name in [owner, owner + "-android"]]:
            root = repository / GROUP.replace(".", "/") / module / version
            require(root.is_dir(), f"Missing current publication: {root}")
            for path in sorted(root.rglob("*")):
                if path.is_file():
                    archive.write(path, path.relative_to(repository))
    receipts = {module: verify(bundle, GROUP, module, version, classes, edges)
                for module, (classes, edges) in COMPONENTS.items()}
    result = dict(receipts[MODULE])
    result["components"] = receipts
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
    for scope in ["compile", "runtime"]:
        matches = [path for path in paths[scope] if Path(path).suffix == ".aar" and
                   hashlib.sha256(Path(path).read_bytes()).hexdigest() == publication["aar_sha256"]]
        require(len(matches) == 1, f"Selected {scope} AAR bytes differ from staged producer")
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
