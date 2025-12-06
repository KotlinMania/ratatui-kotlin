package ratatui.symbols.merge

/**
 * A strategy for merging two symbols into one.
 *
 * This enum defines how two symbols should be merged together, allowing for different behaviors
 * when combining symbols, such as replacing the previous symbol, merging them if an exact match
 * exists, or using a fuzzy match to find the closest representation.
 *
 * This is useful for collapsing borders in layouts, where multiple symbols may need to be
 * combined to create a single, coherent border representation.
 */
enum class MergeStrategy {
    /**
     * Replaces the previous symbol with the next one.
     *
     * This strategy simply replaces the previous symbol with the next one, without attempting to
     * merge them. This is useful when you want to ensure that the last rendered symbol takes
     * precedence over the previous one, regardless of their compatibility.
     */
    Replace,

    /**
     * Merges symbols only if an exact composite unicode character exists.
     *
     * This strategy attempts to merge two symbols into a single composite unicode character if the
     * exact representation exists. If the required unicode symbol does not exist, it falls back to
     * [Replace], replacing the previous symbol with the next one.
     */
    Exact,

    /**
     * Merges symbols even if an exact composite unicode character doesn't exist, using the closest
     * match.
     *
     * If required unicode symbol exists, acts exactly like [Exact], if not, the following rules
     * are applied:
     *
     * 1. Dashed segments are replaced with plain or thick equivalents.
     * 2. Rounded segments are replaced with plain.
     * 3. Double and thick segments are merged based on the second symbol.
     * 4. Some combinations of double and plain that don't exist are merged based on the second symbol.
     */
    Fuzzy;

    /**
     * Merges two symbols using this merge strategy.
     *
     * This method takes two strings representing the previous and next symbols, and
     * returns a string representing the merged symbol based on the merge strategy.
     *
     * If either of the symbols are not in the Box Drawing Unicode block, the `next` symbol is
     * returned as is. If both symbols are valid, they are merged according to the rules defined
     * in the [MergeStrategy].
     */
    fun merge(prev: String, next: String): String {
        if (this == Replace) {
            return next
        }

        val prevSymbol = BorderSymbol.fromString(prev)
        val nextSymbol = BorderSymbol.fromString(next)

        return when {
            prevSymbol != null && nextSymbol != null -> {
                val merged = prevSymbol.merge(nextSymbol, this)
                merged.toStringOrNull() ?: next
            }
            prevSymbol == null && nextSymbol != null -> prev
            else -> next
        }
    }

    companion object {
        fun default(): MergeStrategy = Replace
    }
}

/**
 * A visual style defining the appearance of a single line making up a block border.
 */
internal enum class LineStyle {
    Nothing,
    Plain,
    Rounded,
    Double,
    Thick,
    DoubleDash,
    DoubleDashThick,
    TripleDash,
    TripleDashThick,
    QuadrupleDash,
    QuadrupleDashThick;

    fun merge(other: LineStyle): LineStyle {
        return if (other == Nothing) this else other
    }
}

/**
 * Represents a composite border symbol using individual line components.
 */
internal data class BorderSymbol(
    val right: LineStyle,
    val up: LineStyle,
    val left: LineStyle,
    val down: LineStyle
) {
    fun merge(other: BorderSymbol, strategy: MergeStrategy): BorderSymbol {
        val exactResult = BorderSymbol(
            right.merge(other.right),
            up.merge(other.up),
            left.merge(other.left),
            down.merge(other.down)
        )
        return when (strategy) {
            MergeStrategy.Replace -> other
            MergeStrategy.Fuzzy -> exactResult.fuzzy(other)
            MergeStrategy.Exact -> exactResult
        }
    }

    private fun fuzzy(other: BorderSymbol): BorderSymbol {
        var result = this

        // Dashes only include vertical and horizontal lines.
        if (!result.isStraight()) {
            result = result
                .replace(LineStyle.DoubleDash, LineStyle.Plain)
                .replace(LineStyle.TripleDash, LineStyle.Plain)
                .replace(LineStyle.QuadrupleDash, LineStyle.Plain)
                .replace(LineStyle.DoubleDashThick, LineStyle.Thick)
                .replace(LineStyle.TripleDashThick, LineStyle.Thick)
                .replace(LineStyle.QuadrupleDashThick, LineStyle.Thick)
        }

        // Rounded has only corner variants.
        if (!result.isCorner()) {
            result = result.replace(LineStyle.Rounded, LineStyle.Plain)
        }

        // There are no Double + Thick variants.
        if (result.contains(LineStyle.Double) && result.contains(LineStyle.Thick)) {
            result = if (other.contains(LineStyle.Double)) {
                result.replace(LineStyle.Thick, LineStyle.Double)
            } else {
                result.replace(LineStyle.Double, LineStyle.Thick)
            }
        }

        // Some Plain + Double variants don't exist.
        if (result.toStringOrNull() == null) {
            result = if (other.contains(LineStyle.Double)) {
                result.replace(LineStyle.Plain, LineStyle.Double)
            } else {
                result.replace(LineStyle.Double, LineStyle.Plain)
            }
        }

        return result
    }

    private fun isStraight(): Boolean {
        return (up == down && left == right) &&
                (up == LineStyle.Nothing || left == LineStyle.Nothing)
    }

    private fun isCorner(): Boolean {
        return when {
            up != LineStyle.Nothing && right != LineStyle.Nothing &&
                    down == LineStyle.Nothing && left == LineStyle.Nothing -> up == right
            right != LineStyle.Nothing && down != LineStyle.Nothing &&
                    up == LineStyle.Nothing && left == LineStyle.Nothing -> right == down
            down != LineStyle.Nothing && left != LineStyle.Nothing &&
                    up == LineStyle.Nothing && right == LineStyle.Nothing -> down == left
            up != LineStyle.Nothing && left != LineStyle.Nothing &&
                    right == LineStyle.Nothing && down == LineStyle.Nothing -> up == left
            else -> false
        }
    }

    private fun contains(style: LineStyle): Boolean {
        return up == style || right == style || down == style || left == style
    }

    private fun replace(from: LineStyle, to: LineStyle): BorderSymbol {
        return BorderSymbol(
            if (right == from) to else right,
            if (up == from) to else up,
            if (left == from) to else left,
            if (down == from) to else down
        )
    }

    fun toStringOrNull(): String? = SYMBOL_MAP.entries
        .find { it.value == this }
        ?.key

    companion object {
        fun fromString(s: String): BorderSymbol? = SYMBOL_MAP[s]
    }
}

// Symbol lookup table
private val SYMBOL_MAP: Map<String, BorderSymbol> = buildMap {
    val N = LineStyle.Nothing
    val P = LineStyle.Plain
    val R = LineStyle.Rounded
    val D = LineStyle.Double
    val T = LineStyle.Thick
    val DD = LineStyle.DoubleDash
    val DDT = LineStyle.DoubleDashThick
    val TD = LineStyle.TripleDash
    val TDT = LineStyle.TripleDashThick
    val QD = LineStyle.QuadrupleDash
    val QDT = LineStyle.QuadrupleDashThick

    // Horizontal and vertical lines
    put("─", BorderSymbol(P, N, P, N))
    put("━", BorderSymbol(T, N, T, N))
    put("│", BorderSymbol(N, P, N, P))
    put("┃", BorderSymbol(N, T, N, T))

    // Dashed lines
    put("┄", BorderSymbol(TD, N, TD, N))
    put("┅", BorderSymbol(TDT, N, TDT, N))
    put("┆", BorderSymbol(N, TD, N, TD))
    put("┇", BorderSymbol(N, TDT, N, TDT))
    put("┈", BorderSymbol(QD, N, QD, N))
    put("┉", BorderSymbol(QDT, N, QDT, N))
    put("┊", BorderSymbol(N, QD, N, QD))
    put("┋", BorderSymbol(N, QDT, N, QDT))
    put("╌", BorderSymbol(DD, N, DD, N))
    put("╍", BorderSymbol(DDT, N, DDT, N))
    put("╎", BorderSymbol(N, DD, N, DD))
    put("╏", BorderSymbol(N, DDT, N, DDT))

    // Corners - plain
    put("┌", BorderSymbol(P, N, N, P))
    put("┐", BorderSymbol(N, N, P, P))
    put("└", BorderSymbol(P, P, N, N))
    put("┘", BorderSymbol(N, P, P, N))

    // Corners - thick
    put("┏", BorderSymbol(T, N, N, T))
    put("┓", BorderSymbol(N, N, T, T))
    put("┗", BorderSymbol(T, T, N, N))
    put("┛", BorderSymbol(N, T, T, N))

    // Corners - mixed plain/thick
    put("┍", BorderSymbol(T, N, N, P))
    put("┎", BorderSymbol(P, N, N, T))
    put("┑", BorderSymbol(N, N, T, P))
    put("┒", BorderSymbol(N, N, P, T))
    put("┕", BorderSymbol(T, P, N, N))
    put("┖", BorderSymbol(P, T, N, N))
    put("┙", BorderSymbol(N, P, T, N))
    put("┚", BorderSymbol(N, T, P, N))

    // T-junctions - plain
    put("├", BorderSymbol(P, P, N, P))
    put("┤", BorderSymbol(N, P, P, P))
    put("┬", BorderSymbol(P, N, P, P))
    put("┴", BorderSymbol(P, P, P, N))

    // T-junctions - thick
    put("┣", BorderSymbol(T, T, N, T))
    put("┫", BorderSymbol(N, T, T, T))
    put("┳", BorderSymbol(T, N, T, T))
    put("┻", BorderSymbol(T, T, T, N))

    // T-junctions - mixed
    put("┝", BorderSymbol(T, P, N, P))
    put("┞", BorderSymbol(P, T, N, P))
    put("┟", BorderSymbol(P, P, N, T))
    put("┠", BorderSymbol(P, T, N, T))
    put("┡", BorderSymbol(T, T, N, P))
    put("┢", BorderSymbol(T, P, N, T))
    put("┥", BorderSymbol(N, P, T, P))
    put("┦", BorderSymbol(N, T, P, P))
    put("┧", BorderSymbol(N, P, P, T))
    put("┨", BorderSymbol(N, T, P, T))
    put("┩", BorderSymbol(N, T, T, P))
    put("┪", BorderSymbol(N, P, T, T))
    put("┭", BorderSymbol(P, N, T, P))
    put("┮", BorderSymbol(T, N, P, P))
    put("┯", BorderSymbol(T, N, T, P))
    put("┰", BorderSymbol(P, N, P, T))
    put("┱", BorderSymbol(P, N, T, T))
    put("┲", BorderSymbol(T, N, P, T))
    put("┵", BorderSymbol(P, P, T, N))
    put("┶", BorderSymbol(T, P, P, N))
    put("┷", BorderSymbol(T, P, T, N))
    put("┸", BorderSymbol(P, T, P, N))
    put("┹", BorderSymbol(P, T, T, N))
    put("┺", BorderSymbol(T, T, P, N))

    // Cross - plain
    put("┼", BorderSymbol(P, P, P, P))

    // Cross - thick
    put("╋", BorderSymbol(T, T, T, T))

    // Cross - mixed
    put("┽", BorderSymbol(P, P, T, P))
    put("┾", BorderSymbol(T, P, P, P))
    put("┿", BorderSymbol(T, P, T, P))
    put("╀", BorderSymbol(P, T, P, P))
    put("╁", BorderSymbol(P, P, P, T))
    put("╂", BorderSymbol(P, T, P, T))
    put("╃", BorderSymbol(P, T, T, P))
    put("╄", BorderSymbol(T, T, P, P))
    put("╅", BorderSymbol(P, P, T, T))
    put("╆", BorderSymbol(T, P, P, T))
    put("╇", BorderSymbol(T, T, T, P))
    put("╈", BorderSymbol(T, P, T, T))
    put("╉", BorderSymbol(P, T, T, T))
    put("╊", BorderSymbol(T, T, P, T))

    // Double lines
    put("═", BorderSymbol(D, N, D, N))
    put("║", BorderSymbol(N, D, N, D))

    // Double corners
    put("╔", BorderSymbol(D, N, N, D))
    put("╗", BorderSymbol(N, N, D, D))
    put("╚", BorderSymbol(D, D, N, N))
    put("╝", BorderSymbol(N, D, D, N))

    // Double/plain mixed corners
    put("╒", BorderSymbol(D, N, N, P))
    put("╓", BorderSymbol(P, N, N, D))
    put("╕", BorderSymbol(N, N, D, P))
    put("╖", BorderSymbol(N, N, P, D))
    put("╘", BorderSymbol(D, P, N, N))
    put("╙", BorderSymbol(P, D, N, N))
    put("╛", BorderSymbol(N, P, D, N))
    put("╜", BorderSymbol(N, D, P, N))

    // Double T-junctions
    put("╠", BorderSymbol(D, D, N, D))
    put("╣", BorderSymbol(N, D, D, D))
    put("╦", BorderSymbol(D, N, D, D))
    put("╩", BorderSymbol(D, D, D, N))

    // Double/plain mixed T-junctions
    put("╞", BorderSymbol(D, P, N, P))
    put("╟", BorderSymbol(P, D, N, D))
    put("╡", BorderSymbol(N, P, D, P))
    put("╢", BorderSymbol(N, D, P, D))
    put("╤", BorderSymbol(D, N, D, P))
    put("╥", BorderSymbol(P, N, P, D))
    put("╧", BorderSymbol(D, P, D, N))
    put("╨", BorderSymbol(P, D, P, N))

    // Double cross
    put("╬", BorderSymbol(D, D, D, D))

    // Double/plain mixed cross
    put("╪", BorderSymbol(D, P, D, P))
    put("╫", BorderSymbol(P, D, P, D))

    // Rounded corners
    put("╭", BorderSymbol(R, N, N, R))
    put("╮", BorderSymbol(N, N, R, R))
    put("╯", BorderSymbol(N, R, R, N))
    put("╰", BorderSymbol(R, R, N, N))

    // Half lines
    put("╴", BorderSymbol(N, N, P, N))
    put("╵", BorderSymbol(N, P, N, N))
    put("╶", BorderSymbol(P, N, N, N))
    put("╷", BorderSymbol(N, N, N, P))
    put("╸", BorderSymbol(N, N, T, N))
    put("╹", BorderSymbol(N, T, N, N))
    put("╺", BorderSymbol(T, N, N, N))
    put("╻", BorderSymbol(N, N, N, T))
    put("╼", BorderSymbol(T, N, P, N))
    put("╽", BorderSymbol(N, P, N, T))
    put("╾", BorderSymbol(P, N, T, N))
    put("╿", BorderSymbol(N, T, N, P))
}
