"""Synthetic guard rejection tests. These are not producer or consumer runtime proof."""
import hashlib
import io
import json
import tempfile
import unittest
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path

from check_publication_consumer import COMPONENTS, GROUP, MODULE, metadata_path, verify_components

VERSION = "0.1.12"


def zip_bytes(entries):
    output = io.BytesIO()
    with zipfile.ZipFile(output, "w") as archive:
        for name, data in entries.items():
            archive.writestr(name, data)
    return output.getvalue()


def fixture():
    entries = {}
    for module, (classes, edges) in COMPONENTS.items():
        root = metadata_path(module, VERSION) + ".module"
        component = {"group": GROUP, "module": module, "version": VERSION}
        root_variants = []
        for suffix, platform, element, environment in [
            ("-android", "androidJvm", "aar", "android"),
            ("-jvm", "jvm", "jar", "standard-jvm"),
        ]:
            base = metadata_path(module, VERSION, suffix)
            path = base + ".module"
            class_jar = zip_bytes({name: bytes.fromhex("cafebabe") for name in classes})
            payload = zip_bytes({"AndroidManifest.xml": b"<manifest/>", "classes.jar": class_jar}) if suffix == "-android" else class_jar
            entries[base + "." + element] = payload
            attrs = {"org.gradle.category": "library", "org.gradle.libraryelements": element,
                     "org.gradle.jvm.environment": environment, "org.jetbrains.kotlin.platform.type": platform}
            variants = []
            for usage in ["java-api", "java-runtime"]:
                attributes = dict(attrs, **{"org.gradle.usage": usage})
                root_variants.append({"name": platform + usage, "attributes": attributes,
                                      "available-at": {"group": GROUP, "module": module + suffix,
                                                       "version": VERSION, "url": "../../" + module + suffix + "/" + VERSION + "/" + Path(path).name}})
                variants.append({"name": platform + usage, "attributes": attributes,
                                 "dependencies": [{"group": group, "module": child, "version": {"requires": VERSION}}
                                                  for group, child, _ in edges],
                                 "files": [{"url": Path(base).name + "." + element, "size": len(payload),
                                            "sha256": hashlib.sha256(payload).hexdigest()}]})
            entries[path] = json.dumps({"formatVersion": "1.1", "component": dict(component, url="../../" + module + "/" + VERSION + "/" + Path(root).name), "variants": variants}).encode()
            pom = ET.Element("project", xmlns="http://maven.apache.org/POM/4.0.0")
            for key, value in [("groupId", GROUP), ("artifactId", module + suffix), ("version", VERSION), ("packaging", element)]:
                ET.SubElement(pom, key).text = value
            dependencies = ET.SubElement(pom, "dependencies")
            for group, child, android in edges:
                dependency = ET.SubElement(dependencies, "dependency")
                for key, value in [("groupId", group), ("artifactId", android if suffix == "-android" else child + "-jvm"), ("version", VERSION), ("scope", "compile")]:
                    ET.SubElement(dependency, key).text = value
            entries[base + ".pom"] = ET.tostring(pom)
        entries[root] = json.dumps({"formatVersion": "1.1", "component": component, "variants": root_variants}).encode()
    return entries


class FacadePublicationTests(unittest.TestCase):
    def setUp(self):
        root = Path(__file__).resolve().parent.parent / "build/verification-fixtures"
        root.mkdir(parents=True, exist_ok=True)
        self.directory = tempfile.TemporaryDirectory(dir=root)
        self.addCleanup(self.directory.cleanup)
        self.bundle = Path(self.directory.name) / "guard.zip"
        self.entries = fixture()

    def verify(self):
        self.bundle.write_bytes(zip_bytes(self.entries))
        return verify_components(self.bundle, VERSION)

    def change_metadata(self, suffix, edit):
        path = metadata_path(MODULE, VERSION, suffix) + ".module"
        data = json.loads(self.entries[path])
        edit(data)
        self.entries[path] = json.dumps(data).encode()

    def test_empty_facade_requires_all_real_owners(self):
        result = self.verify()
        self.assertTrue(result["api_facade"])
        self.assertEqual(set(result["components"]), set(COMPONENTS))
        self.assertTrue(all(component["required_classes"] for name, component in result["components"].items() if name != MODULE))

    def test_missing_owner_rejected(self):
        del self.entries[metadata_path("ratatui-core", VERSION, "-android") + ".aar"]
        with self.assertRaisesRegex(ValueError, "Missing Android publication"):
            self.verify()

    def test_runtime_only_edge_rejected(self):
        self.change_metadata("-android", lambda data: data["variants"][0].update(dependencies=[]))
        with self.assertRaisesRegex(ValueError, "Public signature dependencies"):
            self.verify()

    def test_wrong_child_version_rejected(self):
        self.change_metadata("-android", lambda data: data["variants"][0]["dependencies"][0]["version"].update(requires="0.0.0"))
        with self.assertRaisesRegex(ValueError, "current-version API edge"):
            self.verify()

    def test_wrong_child_coordinate_rejected(self):
        self.change_metadata("-jvm", lambda data: data["variants"][0]["dependencies"][0].update(module="wrong-child"))
        with self.assertRaisesRegex(ValueError, "JVM public API dependency"):
            self.verify()

    def test_wrong_variant_rejected(self):
        self.change_metadata("-jvm", lambda data: data["variants"][0]["attributes"].update({"org.jetbrains.kotlin.platform.type": "androidJvm"}))
        with self.assertRaisesRegex(ValueError, "Missing jvm API/runtime"):
            self.verify()

    def test_wrong_payload_hash_rejected(self):
        self.change_metadata("-jvm", lambda data: data["variants"][0]["files"][0].update(sha256="0" * 64))
        with self.assertRaisesRegex(ValueError, "payload hash/path/size"):
            self.verify()

    def test_wrong_pom_scope_rejected(self):
        path = metadata_path(MODULE, VERSION, "-jvm") + ".pom"
        self.entries[path] = self.entries[path].replace(b"<scope>compile</scope>", b"<scope>runtime</scope>")
        with self.assertRaisesRegex(ValueError, "compile-scope POM edge"):
            self.verify()

    def test_empty_mandatory_owner_payload_rejected(self):
        module = "ratatui-widgets"
        base = metadata_path(module, VERSION, "-jvm")
        payload = zip_bytes({})
        self.entries[base + ".jar"] = payload
        data = json.loads(self.entries[base + ".module"])
        for variant in data["variants"]:
            variant["files"][0].update(size=len(payload), sha256=hashlib.sha256(payload).hexdigest())
        self.entries[base + ".module"] = json.dumps(data).encode()
        with self.assertRaisesRegex(ValueError, "Missing real JVM public API bytecode"):
            self.verify()


if __name__ == "__main__":
    unittest.main()
