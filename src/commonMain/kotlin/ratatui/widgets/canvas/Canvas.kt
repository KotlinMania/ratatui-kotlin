package ratatui.widgets.canvas

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.symbols.Marker
import ratatui.widgets.Widget
import ratatui.widgets.block.Block

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
