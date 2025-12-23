package ratatui.widgets.canvas

import ratatui.style.Color
import ratatui.symbols.Braille
import ratatui.symbols.HalfBlock
import ratatui.symbols.Marker
import ratatui.symbols.Pixel

/**
 * A cell within a layer.
 *
 * If a [Context] contains multiple layers, then the symbol, foreground, and background colors
 * for a character will be determined by the top-most layer that provides a value for that
 * character.
 */
internal data class LayerCell(
    val symbol: String?,
    val fg: Color?,
    val bg: Color?
)

/**
 * A single layer of the canvas.
 *
 * This allows the canvas to be drawn in multiple layers. This is useful if you want to draw
 * multiple shapes on the canvas in specific order.
 */
internal data class Layer(
    val contents: List<LayerCell>
)

/**
 * A grid of cells that can be painted on.
 *
 * The grid represents a particular screen region measured in rows and columns. The underlying
 * resolution of the grid might exceed the number of rows and columns.
 */
internal interface Grid {
    /**
     * Get the resolution of the grid in number of dots.
     *
     * This doesn't have to be the same as the number of rows and columns of the grid.
     */
    fun resolution(): Pair<Double, Double>

    /**
     * Paint a point of the grid.
     *
     * The point is expressed in number of dots starting at the origin of the grid in the top left
     * corner.
     */
    fun paint(x: Int, y: Int, color: Color)

    /**
     * Save the current state of the Grid as a layer to be rendered.
     */
    fun save(): Layer

    /**
     * Reset the grid to its initial state.
     */
    fun reset()
}

/**
 * The pattern and color of a PatternGrid cell.
 */
private data class PatternCell(
    var pattern: Int = 0,
    var color: Color? = null
)

/**
 * The PatternGrid is a grid made up of cells each containing a WxH pattern character.
 *
 * This makes it possible to draw shapes with a resolution of e.g. 2x4 (Braille or unicode octant)
 * per cell.
 */
internal class PatternGrid(
    private val width: Int,
    private val height: Int,
    private val cellWidth: Int,
    private val cellHeight: Int,
    private val charTable: Array<String>
) : Grid {
    private val cells: MutableList<PatternCell> = MutableList(width * height) { PatternCell() }

    override fun resolution(): Pair<Double, Double> {
        return Pair(width.toDouble() * cellWidth, height.toDouble() * cellHeight)
    }

    override fun paint(x: Int, y: Int, color: Color) {
        val cellX = x / cellWidth
        val cellY = y / cellHeight
        val index = cellY * width + cellX

        if (index >= 0 && index < cells.size) {
            val localX = x % cellWidth
            val localY = y % cellHeight
            val bitIndex = localX + cellWidth * localY
            cells[index].pattern = cells[index].pattern or (1 shl bitIndex)
            cells[index].color = color
        }
    }

    override fun save(): Layer {
        val contents = cells.map { cell ->
            val symbol = if (cell.pattern == 0) {
                null
            } else {
                charTable.getOrNull(cell.pattern)
            }
            LayerCell(
                symbol = symbol,
                fg = cell.color,
                bg = null
            )
        }
        return Layer(contents)
    }

    override fun reset() {
        for (i in cells.indices) {
            cells[i] = PatternCell()
        }
    }
}

/**
 * The CharGrid is a grid made up of cells each containing a single character.
 *
 * This makes it possible to draw shapes with a resolution of 1x1 dots per cell.
 */
internal class CharGrid(
    private val width: Int,
    private val height: Int,
    private val cellChar: String,
    private val applyColorToBg: Boolean = false
) : Grid {
    private val cells: MutableList<Color?> = MutableList(width * height) { null }

    override fun resolution(): Pair<Double, Double> {
        return Pair(width.toDouble(), height.toDouble())
    }

    override fun paint(x: Int, y: Int, color: Color) {
        val index = y * width + x
        if (index >= 0 && index < cells.size) {
            cells[index] = color
        }
    }

    override fun save(): Layer {
        val contents = cells.map { color ->
            LayerCell(
                symbol = if (color != null) cellChar else null,
                fg = color,
                bg = if (applyColorToBg) color else null
            )
        }
        return Layer(contents)
    }

    override fun reset() {
        for (i in cells.indices) {
            cells[i] = null
        }
    }
}

/**
 * The HalfBlockGrid is a grid made up of cells each containing a half block character.
 *
 * In terminals, each character is usually twice as tall as it is wide. This allows us to draw
 * shapes with a resolution of 1x2 "pixels" per cell.
 */
internal class HalfBlockGrid(
    private val width: Int,
    private val height: Int
) : Grid {
    // Represents a single color for each "pixel" arranged in row, column order
    private val pixels: MutableList<MutableList<Color?>> = MutableList(height * 2) {
        MutableList(width) { null }
    }

    override fun resolution(): Pair<Double, Double> {
        return Pair(width.toDouble(), height.toDouble() * 2.0)
    }

    override fun paint(x: Int, y: Int, color: Color) {
        if (y in pixels.indices && x in 0 until width) {
            pixels[y][x] = color
        }
    }

    override fun save(): Layer {
        // Join each adjacent row together to get vertical pairs of pixels
        val contents = mutableListOf<LayerCell>()

        for (row in 0 until height) {
            val upperRow = pixels.getOrNull(row * 2) ?: continue
            val lowerRow = pixels.getOrNull(row * 2 + 1) ?: continue

            for (col in 0 until width) {
                val upper = upperRow.getOrNull(col)
                val lower = lowerRow.getOrNull(col)

                val (symbol, fg, bg) = when {
                    upper == null && lower == null -> Triple(null, null, null)
                    upper == null && lower != null -> Triple(HalfBlock.LOWER.toString(), lower, null)
                    upper != null && lower == null -> Triple(HalfBlock.UPPER.toString(), upper, null)
                    upper == lower -> Triple(HalfBlock.FULL.toString(), upper, lower)
                    else -> Triple(HalfBlock.UPPER.toString(), upper, lower)
                }
                contents.add(LayerCell(symbol, fg, bg))
            }
        }

        return Layer(contents)
    }

    override fun reset() {
        for (row in pixels) {
            for (i in row.indices) {
                row[i] = null
            }
        }
    }
}

/**
 * Create a Grid for the given marker type.
 */
internal fun createGrid(width: Int, height: Int, marker: Marker): Grid {
    return when (marker) {
        Marker.Block -> CharGrid(width, height, "\u2588", applyColorToBg = true)
        Marker.Bar -> CharGrid(width, height, "\u2584")
        Marker.Braille -> PatternGrid(width, height, 2, 4, Braille.PATTERNS.map { it.toString() }.toTypedArray())
        Marker.HalfBlock -> HalfBlockGrid(width, height)
        Marker.Quadrant -> PatternGrid(width, height, 2, 2, Pixel.QUADRANTS.map { it.toString() }.toTypedArray())
        Marker.Sextant -> PatternGrid(width, height, 2, 3, Pixel.SEXTANTS)
        Marker.Octant -> PatternGrid(width, height, 2, 4, Pixel.OCTANTS)
        Marker.Dot -> CharGrid(width, height, "•")
    }
}
