# Shared publication guard from the KotlinMania template; verify real staged or published coordinate bytes.
"""Verify an Android KMP payload in the actual Central deployment ZIP."""

import argparse
import hashlib
import io
import json
import posixpath
import sys
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path




def require(condition, message):
    if not condition:
        raise ValueError(message)


def related_path(origin, relative):
    require(not relative.startswith("/"), f"Absolute metadata URL: {relative}")
    path = posixpath.normpath(posixpath.join(posixpath.dirname(origin), relative))
    require(not path.startswith("../"), f"Metadata URL escapes repository: {relative}")
    return path


def verify(bundle, group, module, version, required_classes, api_dependencies):
    require(bool(required_classes), "Declare publicClasses to verify real public bytecode")
    # Each reviewed edge supplies its actual Android POM module; external KMP
    # libraries may legitimately select a JVM artifact instead of an AAR.
    api_modules = {(g, root) for g, root, android in api_dependencies}
    pom_modules = {(g, android) for g, root, android in api_dependencies}
    prefix = group.replace(".", "/")
    root_dir = f"{prefix}/{module}/{version}"
    android_dir = f"{prefix}/{module}-android/{version}"
    root_path = f"{root_dir}/{module}-{version}.module"
    android_path = f"{android_dir}/{module}-android-{version}.module"
    aar_path = f"{android_dir}/{module}-android-{version}.aar"
    pom_path = f"{android_dir}/{module}-android-{version}.pom"
    expected_identity = {"group": group, "module": module, "version": version}

    with zipfile.ZipFile(bundle) as archive:
        names = archive.namelist()
        require(len(names) == len(set(names)), "Duplicate deployment ZIP entries")
        require(all(not n.startswith("/") and ".." not in n.split("/") for n in names),
                "Unsafe deployment ZIP entry")
        for path in [root_path, android_path, aar_path, pom_path]:
            require(path in names, f"Missing Android publication entry: {path}")
        root = json.loads(archive.read(root_path))
        android = json.loads(archive.read(android_path))
        for path, metadata in [(root_path, root), (android_path, android)]:
            require(metadata.get("formatVersion") == "1.1", f"Unsupported metadata format: {path}")
            component = metadata["component"]
            require(all(component.get(k) == v for k, v in expected_identity.items()),
                    f"Incorrect owning component: {path}")
        require(related_path(android_path, android["component"]["url"]) == root_path,
                "Android metadata must reference its owning root component")
        usages = {"java-api", "java-runtime"}

        def library_variants(metadata):
            return [v for v in metadata["variants"]
                    if v.get("attributes", {}).get("org.jetbrains.kotlin.platform.type") == "androidJvm"
                    and v["attributes"].get("org.gradle.category") == "library"]

        root_variants = library_variants(root)
        variants = library_variants(android)
        for selected in [root_variants, variants]:
            require(len(selected) == 2 and
                    {v["attributes"].get("org.gradle.usage") for v in selected} == usages,
                    "Android API and runtime variants are required")
            for variant in selected:
                attributes = variant["attributes"]
                require(attributes.get("org.gradle.libraryelements") == "aar" and
                        attributes.get("org.gradle.jvm.environment") == "android",
                        "Android variants must select Android AARs")
        for variant in root_variants:
            redirect = variant["available-at"]
            require(redirect.get("group") == group and redirect.get("module") == f"{module}-android"
                    and redirect.get("version") == version
                    and related_path(root_path, redirect["url"]) == android_path,
                    "Root Android variant redirects to the wrong publication")

        payload = archive.read(aar_path)
        payload_hash = hashlib.sha256(payload).hexdigest()
        for variant in variants:
            files = variant.get("files", [])
            require(len(files) == 1 and related_path(android_path, files[0]["url"]) == aar_path,
                    "Android variant must reference the packaged AAR")
            descriptor = files[0]
            require(descriptor.get("size") == len(payload) and descriptor.get("sha256") == payload_hash,
                    "AAR size or SHA-256 disagrees with module metadata")
            for algorithm in ["sha512", "sha1", "md5"]:
                if algorithm in descriptor:
                    require(hashlib.new(algorithm, payload).hexdigest() == descriptor[algorithm],
                            f"AAR {algorithm} disagrees with module metadata")
            dependencies = {(d["group"], d["module"]) for d in variant.get("dependencies", [])}
            require(api_modules <= dependencies,
                    f"Public signature dependencies missing from {variant['name']}: "
                    f"{sorted(api_modules - dependencies)}")

        ns = {"m": "http://maven.apache.org/POM/4.0.0"}
        pom = ET.fromstring(archive.read(pom_path))
        for key, value in {"groupId": group, "artifactId": f"{module}-android",
                           "version": version, "packaging": "aar"}.items():
            require(pom.findtext(f"m:{key}", namespaces=ns) == value, f"Incorrect Android POM {key}")
        compile_dependencies = {
            (d.findtext("m:groupId", namespaces=ns), d.findtext("m:artifactId", namespaces=ns))
            for d in pom.findall("m:dependencies/m:dependency", ns)
            if d.findtext("m:scope", default="compile", namespaces=ns) == "compile"
        }
        require(pom_modules <= compile_dependencies,
                "Android POM must expose public signature dependencies at compile scope")

        with zipfile.ZipFile(io.BytesIO(payload)) as aar:
            entries = aar.namelist()
            require(len(entries) == len(set(entries)), "Duplicate AAR entries")
            require("AndroidManifest.xml" in entries and aar.read("AndroidManifest.xml"),
                    "AAR has no Android manifest")
            require("classes.jar" in entries, "AAR has no classes.jar")
            with zipfile.ZipFile(io.BytesIO(aar.read("classes.jar"))) as classes:
                for name in required_classes:
                    require(name in classes.namelist(), f"AAR is missing public API class: {name}")
                    require(classes.read(name).startswith(bytes.fromhex("cafebabe")),
                            f"AAR entry is not JVM bytecode: {name}")
        return {
            "status": "passed",
            "bundle": str(Path(bundle).resolve()),
            "bundle_sha256": hashlib.sha256(Path(bundle).read_bytes()).hexdigest(),
            "coordinates": f"{group}:{module}:{version}",
            "aar": aar_path,
            "aar_sha256": payload_hash,
            "aar_bytes": len(payload),
            "required_classes": sorted(required_classes),
            "variants": [v["name"] for v in variants],
            "api_dependencies": sorted(":".join(edge) for edge in api_dependencies),
            "consumer_compilation": "Separate Gradle consumer must still resolve and compile this publication.",
        }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("bundle", type=Path)
    parser.add_argument("--group", required=True)
    parser.add_argument("--module", required=True)
    parser.add_argument("--version", required=True)
    parser.add_argument("--receipt", type=Path, required=True)
    parser.add_argument("--public-class", action="append", required=True)
    parser.add_argument("--api-dependency", action="append", default=[],
                        help="group:root-module:Android-POM-module (explicit reviewed API edge)")
    args = parser.parse_args()
    try:
        edges = [tuple(edge.split(":")) for edge in args.api_dependency]
        require(all(len(edge) == 3 and all(edge) for edge in edges), "Invalid API edge")
        receipt = verify(args.bundle, args.group, args.module, args.version,
                         set(args.public_class), edges)
    except (ValueError, KeyError, OSError, zipfile.BadZipFile, ET.ParseError) as error:
        receipt = {"status": "failed", "error": str(error)}
    args.receipt.parent.mkdir(parents=True, exist_ok=True)
    args.receipt.write_text(json.dumps(receipt, indent=2) + "\n")
    print(json.dumps(receipt, indent=2))
    return 0 if receipt["status"] == "passed" else 1


if __name__ == "__main__":
    sys.exit(main())
