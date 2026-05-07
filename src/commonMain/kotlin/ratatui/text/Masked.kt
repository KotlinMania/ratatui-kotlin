// port-lint: source ratatui-core/src/text/masked.rs
package ratatui.text

/**
 * A wrapper around a string that is masked when displayed.
 *
 * The masked string is displayed as a series of the same character. This might be used to display
 * a password field or similar secure data.
 *
 * # Examples
 *
 * ```kotlin
 * val password = Masked.new("12345", 'x')
 * val text = Text.from(password)
 * ```
 */
class Masked private constructor(
    private val inner: String,
    private val maskChar: Char
) : ToText {
    companion object {
        fun new(s: String, maskChar: Char): Masked = Masked(inner = s, maskChar = maskChar)
    }

    /**
     * The character to use for masking.
     */
    fun maskChar(): Char = maskChar

    /**
     * The underlying string, with all characters masked.
     */
    fun value(): String = buildString(inner.length) {
        for (ignored in inner) {
            append(maskChar)
        }
    }

    /**
     * Debug representation of a masked string is the underlying string.
     *
     * Rust formats `Masked` with `Debug` as the inner value (unmasked). Kotlin only has `toString`,
     * so we provide an explicit method for the debug form.
     */
    fun debugString(): String = inner

    /**
     * Display representation of a masked string is the masked string.
     */
    override fun toString(): String = value()

    override fun toText(): Text = Text.raw(value())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Masked) return false
        return inner == other.inner && maskChar == other.maskChar
    }

    override fun hashCode(): Int {
        var result = inner.hashCode()
        result = 31 * result + maskChar.hashCode()
        return result
    }
}

