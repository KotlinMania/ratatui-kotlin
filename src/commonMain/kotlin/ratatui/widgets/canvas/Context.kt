package ratatui.widgets.canvas

import ratatui.symbols.Marker
import ratatui.text.Line

/**
 * Label to draw some text on the canvas.
 */
data class Label(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val line: Line = Line()
)

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
