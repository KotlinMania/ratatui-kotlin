package ratatui.widgets.canvas

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
