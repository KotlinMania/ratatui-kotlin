"""Manifest policy tests only; these do not compile or execute Swift."""
import unittest

from enforce_swift6 import enforce


class StrictSwiftTests(unittest.TestCase):
    def test_generated_dependencies_share_swift6_mode(self):
        text = '// swift-tools-version: 5.9\nimport PackageDescription\nlet package = Package(name: "Demo", targets: [.target(name: "KotlinCoroutineSupport"), .target(name: "Ratatui")])\n'
        result = enforce(text)
        self.assertTrue(result.startswith('// swift-tools-version: 6.0\n'))
        self.assertIn('swiftLanguageModes: [.v6]', result)
        self.assertIn('platforms: [.macOS("15.0")]', result)
        self.assertEqual(result.count('.target('), 2)

    def test_platforms_and_c_standard_preserved(self):
        text = '// swift-tools-version: 6.0\nimport PackageDescription\nlet package = Package(\n    name: "Demo",\n    platforms: [.macOS(.v15), .iOS(.v17)],\n    targets: [],\n    cLanguageStandard: .c11\n)\n'
        result = enforce(text)
        self.assertIn('platforms: [.macOS(.v15), .iOS(.v17)]', result)
        self.assertLess(result.index('swiftLanguageModes:'), result.index('cLanguageStandard:'))

    def test_weaker_package_mode_rejected(self):
        text = '// swift-tools-version: 6.0\nimport PackageDescription\nlet package = Package(name: "Demo", targets: [], swiftLanguageModes: [.v5])\n'
        with self.assertRaisesRegex(ValueError, 'v6 only'):
            enforce(text)

    def test_weaker_target_mode_rejected(self):
        text = '// swift-tools-version: 6.0\nimport PackageDescription\nlet package = Package(name: "Demo", targets: [.target(name: "Ratatui", swiftSettings: [.swiftLanguageMode(.v5)])])\n'
        with self.assertRaisesRegex(ValueError, 'must be v6'):
            enforce(text)

    def test_newer_swift6_tools_version_preserved(self):
        text = '// swift-tools-version: 6.2\nimport PackageDescription\nlet package = Package(name: "Demo", targets: [])\n'
        self.assertTrue(enforce(text).startswith('// swift-tools-version: 6.2\n'))

    def test_explicit_mode_is_idempotent(self):
        text = '// swift-tools-version: 6.0\nimport PackageDescription\nlet package = Package(name: "Demo", platforms: [.macOS(.v15)], targets: [], swiftLanguageModes: [.v6])\n'
        self.assertEqual(enforce(text), text)


if __name__ == '__main__':
    unittest.main(verbosity=2)
