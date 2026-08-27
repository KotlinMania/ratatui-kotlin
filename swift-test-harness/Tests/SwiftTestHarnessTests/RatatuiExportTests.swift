#if canImport(Testing)
import Testing
import Ratatui

@Suite("Ratatui Swift Export Suite")
struct RatatuiExportTests {
    @Test("Swift module loads cleanly")
    func swiftModuleLoads() {
        #expect(Bool(true), "Ratatui swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import Ratatui

final class RatatuiExportTests: XCTestCase {
    func testSwiftModuleLoads() {
        XCTAssertTrue(true, "Ratatui swift module imported cleanly")
    }
}
#endif
