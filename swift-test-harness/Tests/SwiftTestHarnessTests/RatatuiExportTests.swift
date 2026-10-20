import Testing
import Ratatui
import RatatuiCore

@Suite("Ratatui Swift Export Suite")
struct RatatuiExportTests {
    @Test("Swift module loads cleanly")
    func swiftModuleLoads() {
        #expect(ratatui.text.Text.Companion.shared.from(content: "green").width() == 5)
        #expect(ratatui.text.Text.Companion.shared.from(content: "界").width() == 2)
    }
}
