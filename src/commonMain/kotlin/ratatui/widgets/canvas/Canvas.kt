// port-lint: source ratatui-widgets/src/canvas.rs
package ratatui.widgets.canvas

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.symbols.Braille
import ratatui.symbols.HalfBlock
import ratatui.symbols.Marker
import ratatui.symbols.Pixel
import ratatui.text.Line
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import kotlin.math.round

/**
 * Something that can be drawn on a [Canvas].
 *
 * You may implement your own canvas custom widgets by implementing this trait.
 */
interface Shape {
    /**
     * Draws this [Shape] using the given [Painter].
     *
     * This is the only method required to implement a custom widget that can be drawn on a
     * [Canvas].
     */
    fun draw(painter: Painter)
}

/**
 * Label to draw some text on the canvas.
 */
data class Label(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val line: Line = Line()
)

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
        is Marker.Custom -> CharGrid(width, height, marker.char.toString())
    }
}

/**
 * Holds the state of the [Canvas] when painting to it.
 *
 * This is used by the [Canvas] widget to draw shapes on the grid. It can be useful to think of
 * this as similar to the Frame struct that is used to draw widgets on the terminal.
 */
class Context internal constructor(
    // Width of the canvas in cells (NOT the resolution in dots/pixels)
    private val width: Int,
    // Height of the canvas in cells (NOT the resolution in dots/pixels)
    private val height: Int,
    // Canvas coordinate system x bounds [left, right]
    internal val xBounds: DoubleArray,
    // Canvas coordinate system y bounds [bottom, top]
    internal val yBounds: DoubleArray,
    internal var grid: Grid,
    private var dirty: Boolean = false,
    internal val layers: MutableList<Layer> = mutableListOf(),
    internal val labels: MutableList<Label> = mutableListOf()
) {
    /**
     * Create a new Context with the given width and height measured in terminal columns and rows
     * respectively. The `x` and `y` bounds define the specific area of some coordinate system that
     * will be drawn on the canvas. The marker defines the type of points used to draw the shapes.
     *
     * Applications should not use this directly but rather use the [Canvas] widget. This will be
     * created by the [Canvas.paint] method and passed to the closure that is used to draw on
     * the canvas.
     *
     * The `x` and `y` bounds should be specified as left/right and bottom/top respectively.
     */
    constructor(
        width: Int,
        height: Int,
        xBounds: DoubleArray,
        yBounds: DoubleArray,
        marker: Marker
    ) : this(
        width = width,
        height = height,
        xBounds = xBounds,
        yBounds = yBounds,
        grid = createGrid(width, height, marker)
    )

    /**
     * Change the marker being used in this context.
     *
     * This will save the last layer if necessary and reset the grid to use the new marker.
     */
    fun marker(marker: Marker) {
        finish()
        grid = createGrid(width, height, marker)
    }

    /**
     * Draw the given [Shape] in this context.
     */
    fun draw(shape: Shape) {
        dirty = true
        val painter = Painter.from(this)
        shape.draw(painter)
    }

    /**
     * Save the existing state of the grid as a layer.
     *
     * Save the existing state as a layer to be rendered and reset the grid to its initial
     * state for the next layer.
     *
     * This allows the canvas to be drawn in multiple layers. This is useful if you want to
     * draw multiple shapes on the [Canvas] in specific order.
     */
    fun layer() {
        layers.add(grid.save())
        grid.reset()
        dirty = false
    }

    /**
     * Print a text on the [Canvas] at the given position.
     *
     * Note that the text is always printed on top of the canvas and is **not** affected by the
     * layers.
     */
    fun print(x: Double, y: Double, line: Line) {
        labels.add(Label(x, y, line))
    }

    /**
     * Print a string on the [Canvas] at the given position.
     */
    fun print(x: Double, y: Double, text: String) {
        labels.add(Label(x, y, Line.raw(text)))
    }

    /**
     * Save the last layer if necessary.
     */
    internal fun finish() {
        if (dirty) {
            layer()
        }
    }

    companion object {
        /**
         * Create a new Context with the given parameters.
         */
        fun new(
            width: Int,
            height: Int,
            xBounds: DoubleArray,
            yBounds: DoubleArray,
            marker: Marker
        ): Context = Context(width, height, xBounds, yBounds, marker)
    }
}

/**
 * Painter is an abstraction over the [Context] that allows to draw shapes on the grid.
 *
 * It is used by the [Shape] interface to draw shapes on the grid. It can be useful to think of this
 * as similar to the Buffer struct that is used to draw widgets on the terminal.
 */
class Painter internal constructor(
    private val context: Context,
    private val resolution: Pair<Double, Double>
) {
    /**
     * Convert the `(x, y)` coordinates to location of a point on the grid.
     *
     * `(x, y)` coordinates are expressed in the coordinate system of the canvas. The origin is in
     * the lower left corner of the canvas (unlike most other coordinates in Ratatui where the
     * origin is the upper left corner). The `x` and `y` bounds of the canvas define the specific
     * area of some coordinate system that will be drawn on the canvas. The resolution of the grid
     * is used to convert the `(x, y)` coordinates to the location of a point on the grid.
     *
     * The grid coordinates are expressed in the coordinate system of the grid. The origin is in
     * the top left corner of the grid. The x and y bounds of the grid are always `[0, width - 1]`
     * and `[0, height - 1]` respectively. The resolution of the grid is used to convert the
     * `(x, y)` coordinates to the location of a point on the grid.
     *
     * Points are rounded to the nearest grid cell (with points exactly in the center of a cell
     * rounding up).
     *
     * @return The grid coordinates as (x, y) pair, or null if the point is outside the canvas bounds
     */
    fun getPoint(x: Double, y: Double): Pair<Int, Int>? {
        val left = context.xBounds[0]
        val right = context.xBounds[1]
        val bottom = context.yBounds[0]
        val top = context.yBounds[1]

        if (x < left || x > right || y < bottom || y > top) {
            return null
        }

        val width = right - left
        val height = top - bottom

        if (width <= 0.0 || height <= 0.0) {
            return null
        }

        val gridX = round((x - left) * (resolution.first - 1.0) / width).toInt()
        val gridY = round((top - y) * (resolution.second - 1.0) / height).toInt()

        return Pair(gridX, gridY)
    }

    /**
     * Paint a point of the grid.
     */
    fun paint(x: Int, y: Int, color: Color) {
        context.grid.paint(x, y, color)
    }

    /**
     * Canvas context bounds by axis.
     *
     * @return Pair of (xBounds, yBounds) arrays
     */
    fun bounds(): Pair<DoubleArray, DoubleArray> {
        return Pair(context.xBounds, context.yBounds)
    }

    companion object {
        internal fun from(context: Context): Painter {
            val resolution = context.grid.resolution()
            return Painter(context, resolution)
        }
    }
}

/**
 * The Canvas widget provides a means to draw shapes (Lines, Rectangles, Circles, etc.) on a grid.
 *
 * By default the grid is made of Braille patterns but you may change the marker to use a different
 * set of symbols. If your terminal or font does not support this unicode block, you will see
 * unicode replacement characters (�) instead of braille dots. The Braille patterns (as well the
 * octant character patterns) provide a more fine grained result with a 2x4 resolution per
 * character, but you might want to use a simple dot, block, or bar instead by calling the
 * [marker] method if your target environment does not support those symbols.
 *
 * The Canvas widget is used by calling the [paint] method and passing a closure that
 * will be used to draw on the canvas. The closure will be passed a [Context] object that can be
 * used to draw shapes on the canvas.
 *
 * # Examples
 *
 * ```kotlin
 * Canvas()
 *     .block(Block.bordered().title("Canvas"))
 *     .xBounds(doubleArrayOf(-180.0, 180.0))
 *     .yBounds(doubleArrayOf(-90.0, 90.0))
 *     .paint { ctx ->
 *         ctx.draw(Map(resolution = MapResolution.High, color = Color.White))
 *         ctx.layer()
 *         ctx.draw(Line(0.0, 10.0, 10.0, 10.0, Color.White))
 *         ctx.draw(Rectangle(10.0, 20.0, 10.0, 10.0, Color.Red))
 *     }
 * ```
 */
class Canvas<F : (Context) -> Unit>(
    private var block: Block? = null,
    private var xBounds: DoubleArray = doubleArrayOf(0.0, 0.0),
    private var yBounds: DoubleArray = doubleArrayOf(0.0, 0.0),
    private var paintFunc: F? = null,
    private var backgroundColor: Color = Color.Reset,
    private var marker: Marker = Marker.Braille
) : Widget {

    /**
     * Wraps the canvas with a custom [Block] widget.
     */
    fun block(block: Block): Canvas<F> {
        this.block = block
        return this
    }

    /**
     * Define the viewport of the canvas.
     *
     * If you were to "zoom" to a certain part of the world you may want to choose different
     * bounds.
     */
    fun xBounds(bounds: DoubleArray): Canvas<F> {
        this.xBounds = bounds
        return this
    }

    /**
     * Define the viewport of the canvas.
     *
     * If you were to "zoom" to a certain part of the world you may want to choose different
     * bounds.
     */
    fun yBounds(bounds: DoubleArray): Canvas<F> {
        this.yBounds = bounds
        return this
    }

    /**
     * Store the closure that will be used to draw to the [Canvas].
     */
    fun paint(f: F): Canvas<F> {
        this.paintFunc = f
        return this
    }

    /**
     * Change the background [Color] of the entire canvas.
     */
    fun backgroundColor(color: Color): Canvas<F> {
        this.backgroundColor = color
        return this
    }

    /**
     * Change the type of points used to draw the shapes.
     *
     * By default the [Marker.Braille] patterns are used as they provide a more fine grained result,
     * but you might want to use the simple [Marker.Dot] or [Marker.Block] instead if the targeted
     * terminal does not support those symbols.
     */
    fun marker(marker: Marker): Canvas<F> {
        this.marker = marker
        return this
    }

    override fun render(area: Rect, buf: Buffer) {
        // Render the block if present
        block?.render(area, buf)

        // Get the inner area (inside the block if present)
        val canvasArea = block?.inner(area) ?: area

        if (canvasArea.isEmpty()) {
            return
        }

        // Set the background color
        buf.setStyle(canvasArea, Style().bg(backgroundColor))

        val width = canvasArea.width

        val painter = paintFunc ?: return

        // Create a blank context that matches the size of the canvas
        val ctx = Context(
            width = canvasArea.width,
            height = canvasArea.height,
            xBounds = xBounds,
            yBounds = yBounds,
            marker = marker
        )

        // Paint to this context
        painter(ctx)
        ctx.finish()

        // Retrieve painted points for each layer
        for (layer in ctx.layers) {
            for ((index, layerCell) in layer.contents.withIndex()) {
                val x = (index % width) + canvasArea.left()
                val y = (index / width) + canvasArea.top()

                if (x < canvasArea.right() && y < canvasArea.bottom()) {
                    val cell = buf[x, y]

                    layerCell.symbol?.let { symbol ->
                        cell.setSymbol(symbol)
                    }
                    layerCell.fg?.let { fg ->
                        cell.setFg(fg)
                    }
                    layerCell.bg?.let { bg ->
                        cell.setBg(bg)
                    }
                }
            }
        }

        // Finally draw the labels
        val left = xBounds[0]
        val right = xBounds[1]
        val top = yBounds[1]
        val bottom = yBounds[0]
        val boundsWidth = kotlin.math.abs(xBounds[1] - xBounds[0])
        val boundsHeight = kotlin.math.abs(yBounds[1] - yBounds[0])

        val resolutionWidth = (canvasArea.width - 1).toDouble()
        val resolutionHeight = (canvasArea.height - 1).toDouble()

        for (label in ctx.labels) {
            if (label.x >= left && label.x <= right && label.y <= top && label.y >= bottom) {
                val x = ((label.x - left) * resolutionWidth / boundsWidth).toInt() + canvasArea.left()
                val y = ((top - label.y) * resolutionHeight / boundsHeight).toInt() + canvasArea.top()
                buf.setLine(x, y, label.line, canvasArea.right() - x)
            }
        }
    }

    companion object {
        /**
         * Create a new Canvas with default settings.
         */
        operator fun invoke(): Canvas<(Context) -> Unit> = Canvas(
            block = null,
            xBounds = doubleArrayOf(0.0, 0.0),
            yBounds = doubleArrayOf(0.0, 0.0),
            paintFunc = null,
            backgroundColor = Color.Reset,
            marker = Marker.Braille
        )

        /**
         * Create a default Canvas.
         */
        fun default(): Canvas<(Context) -> Unit> = invoke()
    }
}
