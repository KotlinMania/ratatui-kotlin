// port-lint: source ratatui/tests/widgetsParagraph.rs
package ratatui.widgets.paragraph

import kotlin.test.Test
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.layout.HorizontalAlignment
import ratatui.text.Line
import ratatui.text.Span
import ratatui.text.Text
import ratatui.terminal.Terminal
import ratatui.widgets.block.Block
import ratatui.widgets.block.Padding

private fun testCase(paragraph: Paragraph, expected: Buffer) {
    val backend = TestBackend.new(expected.area.width, expected.area.height)
    val terminal = Terminal(backend)
    terminal.draw { f ->
        f.renderWidget(paragraph, f.area())
    }
    terminal.backend().assertBuffer(expected)
}

private const val SAMPLE_STRING: String =
    "The library is based on the principle of immediate rendering with intermediate buffers. " +
        "This means that at each new frame you should build all widgets that are supposed to be part of " +
        "the UI. While providing a great flexibility for rich and interactive UI, this may introduce " +
        "overhead for highly dynamic content."

class ParagraphTest {
    @Test
    fun widgetsParagraphRendersDoubleWidthGraphemes() {
        val s = "コンピュータ上で文字を扱う場合、典型的には文字による通信を行う場合にその両端点では、"

        val text = listOf(Line.from(s))
        val paragraph = Paragraph.new(text)
            .block(Block.bordered())
            .wrap(Wrap(trim = true))

        testCase(
            paragraph,
            Buffer.withLines(
                "┌────────┐",
                "│コンピュ│",
                "│ータ上で│",
                "│文字を扱│",
                "│う場合、│",
                "│典型的に│",
                "│は文字に│",
                "│よる通信│",
                "│を行う場│",
                "└────────┘",
            )
        )
    }

    @Test
    fun widgetsParagraphRendersMixedWidthGraphemes() {
        val backend = TestBackend.new(10, 7)
        val terminal = Terminal(backend)

        val s = "aコンピュータ上で文字を扱う場合、"
        terminal.draw { f ->
            val text = listOf(Line.from(s))
            val paragraph = Paragraph.new(text)
                .block(Block.bordered())
                .wrap(Wrap(trim = true))
            f.renderWidget(paragraph, f.area())
        }

        terminal.backend().assertBufferLines(
            // The internal width is 8 so only 4 slots for double-width characters.
            "┌────────┐",
            "│aコンピ │", // Here we have 1 latin character so only 3 double-width ones can fit.
            "│ュータ上│",
            "│で文字を│",
            "│扱う場合│",
            "│、      │",
            "└────────┘",
        )
    }

    @Test
    fun widgetsParagraphCanWrapWithATrailingNbsp() {
        val nbsp = "\u00a0"
        val line = Line.from(listOf(Span.raw("NBSP"), Span.raw(nbsp)))
        val paragraph = Paragraph.new(line).block(Block.bordered())

        testCase(
            paragraph,
            Buffer.withLines(
                "┌──────────────────┐",
                "│NBSP\u00a0             │",
                "└──────────────────┘",
            )
        )
    }

    @Test
    fun widgetsParagraphCanScrollHorizontally() {
        val text = Text.from("段落现在可以水平滚动了！\nParagraph can scroll horizontally!\nLittle line")
        val paragraph = Paragraph.new(text).block(Block.bordered())

        testCase(
            paragraph.alignment(HorizontalAlignment.Left).scroll(0 to 7),
            Buffer.withLines(
                "┌──────────────────┐",
                "│在可以水平滚动了！│",
                "│ph can scroll hori│",
                "│line              │",
                "│                  │",
                "│                  │",
                "│                  │",
                "│                  │",
                "│                  │",
                "└──────────────────┘",
            )
        )

        // only support HorizontalAlignment.Left
        testCase(
            paragraph.alignment(HorizontalAlignment.Right).scroll(0 to 7),
            Buffer.withLines(
                "┌──────────────────┐",
                "│段落现在可以水平滚│",
                "│Paragraph can scro│",
                "│       Little line│",
                "│                  │",
                "│                  │",
                "│                  │",
                "│                  │",
                "│                  │",
                "└──────────────────┘",
            )
        )
    }

    @Test
    fun widgetsParagraphCanWrapItsContent() {
        val text = listOf(Line.from(SAMPLE_STRING))
        val paragraph = Paragraph.new(text)
            .block(Block.bordered())
            .wrap(Wrap(trim = true))

        testCase(
            paragraph.alignment(HorizontalAlignment.Left),
            Buffer.withLines(
                "┌──────────────────┐",
                "│The library is    │",
                "│based on the      │",
                "│principle of      │",
                "│immediate         │",
                "│rendering with    │",
                "│intermediate      │",
                "│buffers. This     │",
                "│means that at each│",
                "└──────────────────┘",
            )
        )

        testCase(
            paragraph.alignment(HorizontalAlignment.Center),
            Buffer.withLines(
                "┌──────────────────┐",
                "│  The library is  │",
                "│   based on the   │",
                "│   principle of   │",
                "│     immediate    │",
                "│  rendering with  │",
                "│   intermediate   │",
                "│   buffers. This  │",
                "│means that at each│",
                "└──────────────────┘",
            )
        )

        testCase(
            paragraph.alignment(HorizontalAlignment.Right),
            Buffer.withLines(
                "┌──────────────────┐",
                "│    The library is│",
                "│      based on the│",
                "│      principle of│",
                "│         immediate│",
                "│    rendering with│",
                "│      intermediate│",
                "│     buffers. This│",
                "│means that at each│",
                "└──────────────────┘",
            )
        )
    }

    @Test
    fun widgetsParagraphWorksWithPadding() {
        val block = Block.bordered().padding(Padding(left = 2, right = 2, top = 1, bottom = 1))
        val paragraph = Paragraph.new(listOf(Line.from(SAMPLE_STRING)))
            .block(block)
            .wrap(Wrap(trim = true))

        testCase(
            paragraph.alignment(HorizontalAlignment.Left),
            Buffer.withLines(
                "┌────────────────────┐",
                "│                    │",
                "│  The library is    │",
                "│  based on the      │",
                "│  principle of      │",
                "│  immediate         │",
                "│  rendering with    │",
                "│  intermediate      │",
                "│  buffers. This     │",
                "│  means that at     │",
                "│                    │",
                "└────────────────────┘",
            )
        )

        testCase(
            paragraph.alignment(HorizontalAlignment.Right),
            Buffer.withLines(
                "┌────────────────────┐",
                "│                    │",
                "│    The library is  │",
                "│      based on the  │",
                "│      principle of  │",
                "│         immediate  │",
                "│    rendering with  │",
                "│      intermediate  │",
                "│     buffers. This  │",
                "│     means that at  │",
                "│                    │",
                "└────────────────────┘",
            )
        )

        val paragraphWithCenteredLine = Paragraph.new(listOf(
            Line.from("This is always centered.").alignment(HorizontalAlignment.Center),
            Line.from(SAMPLE_STRING),
        ))
            .block(block)
            .wrap(Wrap(trim = true))

        testCase(
            paragraphWithCenteredLine.alignment(HorizontalAlignment.Right),
            Buffer.withLines(
                "┌────────────────────┐",
                "│                    │",
                "│   This is always   │",
                "│      centered.     │",
                "│    The library is  │",
                "│      based on the  │",
                "│      principle of  │",
                "│         immediate  │",
                "│    rendering with  │",
                "│      intermediate  │",
                "│     buffers. This  │",
                "│     means that at  │",
                "│                    │",
                "└────────────────────┘",
            )
        )
    }

    @Test
    fun widgetsParagraphCanAlignSpans() {
        val rightS = "This string will override the paragraph alignment to be right aligned."
        val defaultS = "This string will be aligned based on the alignment of the paragraph."

        val text = listOf(
            Line.from(rightS).alignment(HorizontalAlignment.Right),
            Line.from(defaultS),
        )
        val paragraph = Paragraph.new(text)
            .block(Block.bordered())
            .wrap(Wrap(trim = true))

        testCase(
            paragraph.alignment(HorizontalAlignment.Left),
            Buffer.withLines(
                "┌──────────────────┐",
                "│  This string will│",
                "│      override the│",
                "│         paragraph│",
                "│   alignment to be│",
                "│    right aligned.│",
                "│This string will  │",
                "│be aligned based  │",
                "│on the alignment  │",
                "└──────────────────┘",
            )
        )

        testCase(
            paragraph.alignment(HorizontalAlignment.Center),
            Buffer.withLines(
                "┌──────────────────┐",
                "│  This string will│",
                "│      override the│",
                "│         paragraph│",
                "│   alignment to be│",
                "│    right aligned.│",
                "│ This string will │",
                "│ be aligned based │",
                "│ on the alignment │",
                "└──────────────────┘",
            )
        )

        val leftLines = listOf("This string", "will override the paragraph alignment")
            .map { Line.from(it).alignment(HorizontalAlignment.Left) }
        val lines = listOf(
            "This",
            "must be pretty long",
            "in order to effectively show",
            "truncation.",
        ).map { Line.from(it) }

        val combined = leftLines + lines
        val truncParagraph = Paragraph.new(combined).block(Block.bordered())

        testCase(
            truncParagraph.alignment(HorizontalAlignment.Right),
            Buffer.withLines(
                "┌──────────────────┐",
                "│This string       │",
                "│will override the │",
                "│              This│",
                "│must be pretty lon│",
                "│in order to effect│",
                "│       truncation.│",
                "│                  │",
                "│                  │",
                "└──────────────────┘",
            )
        )

        testCase(
            truncParagraph.alignment(HorizontalAlignment.Left),
            Buffer.withLines(
                "┌──────────────────┐",
                "│This string       │",
                "│will override the │",
                "│This              │",
                "│must be pretty lon│",
                "│in order to effect│",
                "│truncation.       │",
                "│                  │",
                "└──────────────────┘",
            )
        )
    }
}
