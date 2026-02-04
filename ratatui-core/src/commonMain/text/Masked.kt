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
 * val password = Masked("12345", 'x')
 * println(password.value()) // prints "xxxxx"
 * ```
 */
data class Masked(
    /** The underlying string value (unmasked) */
    private val inner: String,
    /** The character to use for masking */
    val maskChar: Char
) {
    /**
     * Returns the underlying string with all characters replaced by the mask character.
     */
    fun value(): String {
        return inner.map { maskChar }.joinToString("")
    }

    /**
     * Debug representation shows the underlying string (for debugging purposes).
     */
    override fun toString(): String = value()

    companion object {
        /**
         * Creates a new [Masked] string with the given content and mask character.
         *
         * @param content The string to mask
         * @param maskChar The character to display instead of the actual content
         */
        fun new(content: String, maskChar: Char): Masked {
            return Masked(inner = content, maskChar = maskChar)
        }
    }
}

/**
 * Extension function to convert a [Masked] string to [Text].
 */
fun Masked.toText(): Text = Text.raw(this.value())
