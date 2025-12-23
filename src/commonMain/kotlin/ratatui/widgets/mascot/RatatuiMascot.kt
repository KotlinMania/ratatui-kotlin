package ratatui.widgets.mascot

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.widgets.Widget

/**
 * State for the mascot's eye
 */
enum class MascotEyeColor {
    /** The default eye color */
    Default,
    /** The red eye color */
    Red
}

/**
 * A widget that renders the Ratatui mascot
 *
 * The mascot takes 32x16 cells and is rendered using half block characters.
 */
data class RatatuiMascot(
    private val eyeState: MascotEyeColor = MascotEyeColor.Default,
    /** The color of the rat */
    private val ratColor: Color = Color.Indexed(252u),        // light_gray #d0d0d0
    /** The color of the rat's eye */
    private val ratEyeColor: Color = Color.Indexed(236u),     // dark_charcoal #303030
    /** The color of the rat's eye when blinking */
    private val ratEyeBlink: Color = Color.Indexed(196u),     // red #ff0000
    /** The color of the rat's hat */
    private val hatColor: Color = Color.Indexed(231u),        // white #ffffff
    /** The color of the terminal */
    private val termColor: Color = Color.Indexed(232u),       // vampire_black #080808
    /** The color of the terminal border */
    private val termBorderColor: Color = Color.Indexed(237u), // gray #808080
    /** The color of the terminal cursor */
    private val termCursorColor: Color = Color.Indexed(248u)  // dark_gray #a8a8a8
) : Widget {

    /**
     * Set the eye state (open / blinking)
     *
     * @param ratEye The eye state
     * @return A new RatatuiMascot with the eye state set
     */
    fun setEye(ratEye: MascotEyeColor): RatatuiMascot = copy(eyeState = ratEye)

    private fun colorFor(c: Char): Color? = when (c) {
        RAT -> ratColor
        HAT -> hatColor
        EYE -> when (eyeState) {
            MascotEyeColor.Default -> ratEyeColor
            MascotEyeColor.Red -> ratEyeBlink
        }
        TERM -> termColor
        TERM_CURSOR -> termCursorColor
        TERM_BORDER -> termBorderColor
        else -> null
    }

    /**
     * Use half block characters to render a logo based on the `RATATUI_MASCOT` const.
     *
     * The logo colors are hardcoded in the widget.
     * The eye color depends on whether it's open / blinking
     */
    override fun render(area: Rect, buf: Buffer) {
        val clippedArea = area.intersection(buf.area)
        if (clippedArea.isEmpty()) {
            return
        }

        val lines = RATATUI_MASCOT.lines()
        val pairs = lines.chunked(2)

        for ((y, pair) in pairs.withIndex()) {
            if (pair.size < 2) continue
            val line1 = pair[0]
            val line2 = pair[1]

            for ((x, chars) in line1.zip(line2).withIndex()) {
                val (ch1, ch2) = chars

                val xPos = clippedArea.left() + x
                val yPos = clippedArea.top() + y

                // Check if coordinates are within the buffer area
                if (xPos >= clippedArea.right() || yPos >= clippedArea.bottom()) {
                    continue
                }

                val cell = buf[xPos, yPos]

                // Given two cells which make up the top and bottom of the character,
                // Foreground color should be the non-space, non-terminal
                val (fg, bg) = when {
                    ch1 == EMPTY && ch2 == EMPTY -> Pair(null, null)
                    ch1 == EMPTY || ch2 == EMPTY -> {
                        val c = if (ch1 != EMPTY) ch1 else ch2
                        Pair(colorFor(c), null)
                    }
                    ch1 == TERM && ch2 == TERM_BORDER -> Pair(colorFor(TERM_BORDER), colorFor(TERM))
                    ch1 == TERM || ch2 == TERM -> {
                        val c = if (ch1 != TERM) ch1 else ch2
                        Pair(colorFor(c), colorFor(TERM))
                    }
                    else -> Pair(colorFor(ch1), colorFor(ch2))
                }

                // Symbol should make the empty space or terminal bg as the empty part of the block
                val symbol = when {
                    ch1 == EMPTY && ch2 == EMPTY -> null
                    ch1 == TERM && ch2 == TERM -> EMPTY
                    ch2 == EMPTY || ch2 == TERM -> '▀'
                    ch1 == EMPTY || ch1 == TERM -> '▄'
                    ch1 == ch2 -> '█'
                    else -> '▀'
                }

                fg?.let { cell.fg = it }
                bg?.let { cell.bg = it }
                symbol?.let { cell.setSymbol(it.toString()) }
            }
        }
    }

    companion object {
        private const val EMPTY = ' '
        private const val RAT = '█'
        private const val HAT = 'h'
        private const val EYE = 'e'
        private const val TERM = '░'
        private const val TERM_BORDER = '▒'
        private const val TERM_CURSOR = '▓'

        private val RATATUI_MASCOT = """
                   hhh
                 hhhhhh
                hhhhhhh
               hhhhhhhh
              hhhhhhhhh
             hhhhhhhhhh
            hhhhhhhhhhhh
            hhhhhhhhhhhhh
            hhhhhhhhhhhhh     ██████
             hhhhhhhhhhh    ████████
                  hhhhh ███████████
                   hhh ██ee████████
                    h █████████████
                ████ █████████████
               █████████████████
               ████████████████
               ████████████████
                ███ ██████████
              ▒▒    █████████
             ▒░░▒   █████████
            ▒░░░░▒ ██████████
           ▒░░▓░░░▒ █████████
          ▒░░▓▓░░░░▒ ████████
         ▒░░░░░░░░░░▒ ██████████
        ▒░░░░░░░░░░░░▒ ██████████
       ▒░░░░░░░▓▓░░░░░▒ █████████
      ▒░░░░░░░░░▓▓░░░░░▒ ████  ███
     ▒░░░░░░░░░░░░░░░░░░▒ ██   ███
    ▒░░░░░░░░░░░░░░░░░░░░▒ █   ███
    ▒░░░░░░░░░░░░░░░░░░░░░▒   ███
     ▒░░░░░░░░░░░░░░░░░░░░░▒ ███
      ▒░░░░░░░░░░░░░░░░░░░░░▒ █
        """.trimIndent()

        /**
         * Create a new Ratatui mascot widget
         *
         * @return A new RatatuiMascot
         */
        fun new(): RatatuiMascot = RatatuiMascot()

        /**
         * Create a default Ratatui mascot widget
         *
         * @return A new RatatuiMascot
         */
        fun default(): RatatuiMascot = RatatuiMascot()
    }
}
