// port-lint: source ratatui-widgets/src/canvas.rs
package ratatui.widgets.canvas

/**
 * A [Canvas] and a collection of [Shape]s.
 *
 * The [Canvas] is a blank space on which you can draw anything manually or use one of the
 * predefined [Shape]s.
 *
 * The available shapes are:
 * - [Circle]: A basic circle
 * - [Line]: A line between two points
 * - [Map]: A world map
 * - [Points]: A scatter of points
 * - [Rectangle]: A basic rectangle
 *
 * You can also implement your own custom [Shape]s.
 */
@Suppress("unused")
internal object CanvasModule {
    // Mirrors `pub use` exports and module-local types from Rust.
    val canvas = Canvas::class
    val shape = Shape::class

    val circle = Circle::class
    val line = Line::class
    val map = Map::class
    val mapResolution = MapResolution::class
    val points = Points::class
    val rectangle = Rectangle::class

    val label = Label::class
    val painter = Painter::class
    val context = Context::class

    val layer = Layer::class
    val layerCell = LayerCell::class
    val grid = Grid::class
    val patternGrid = PatternGrid::class
}

