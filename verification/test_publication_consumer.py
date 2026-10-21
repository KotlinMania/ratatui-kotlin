"""Negative controls for the consumer execution gate, not Kotlin runtime tests."""
import tempfile
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path

from check_publication_consumer import TESTS, executed_tests


class ExecutionGateTests(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.root = Path(self.directory.name)
        for task in ["testAndroidHostTest", "jvmTest"]:
            suite = ET.Element("testsuite")
            for name in sorted(TESTS):
                ET.SubElement(suite, "testcase", classname="consumer.PublicApiConsumerTest",
                              name=name + ("[jvm]" if task == "jvmTest" else ""))
            folder = self.root / task
            folder.mkdir()
            ET.ElementTree(suite).write(folder / "TEST-consumer.xml")

    def edit_android(self, change):
        path = self.root / "testAndroidHostTest/TEST-consumer.xml"
        document = ET.parse(path)
        change(document.getroot())
        document.write(path)

    def test_requires_all_twelve_actual_cases(self):
        self.assertEqual(sum(v["executed"] for v in executed_tests(self.root).values()), 12)

    def test_zero_cases_rejected(self):
        self.edit_android(lambda suite: suite.clear())
        with self.assertRaisesRegex(ValueError, "6 actual"):
            executed_tests(self.root)

    def test_duplicate_name_cannot_replace_missing_behavior(self):
        self.edit_android(lambda suite: suite[0].set("name", suite[1].get("name")))
        with self.assertRaisesRegex(ValueError, "Missing or duplicate"):
            executed_tests(self.root)

    def test_skipped_behavior_rejected(self):
        self.edit_android(lambda suite: ET.SubElement(suite[0], "skipped"))
        with self.assertRaisesRegex(ValueError, "Failed or skipped"):
            executed_tests(self.root)


if __name__ == "__main__":
    unittest.main()
