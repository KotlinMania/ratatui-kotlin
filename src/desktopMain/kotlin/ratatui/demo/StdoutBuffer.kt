package ratatui.demo

/**
 * A small buffering writer for demo backends that output ANSI commands.
 *
 * The Rust demos write to `stdout` directly. In Kotlin/Native we model this as an [Appendable]
 * that buffers until an explicit flush, keeping the call sites close to the Rust structure.
 */
internal class StdoutBuffer : Appendable {
    private val buffer = StringBuilder()

    override fun append(value: CharSequence?): StdoutBuffer {
        buffer.append(value)
        return this
    }

    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): StdoutBuffer {
        buffer.append(value, startIndex, endIndex)
        return this
    }

    override fun append(value: Char): StdoutBuffer {
        buffer.append(value)
        return this
    }

    fun flush() {
        if (buffer.isNotEmpty()) {
            print(buffer.toString())
            buffer.setLength(0)
        }
    }
}

