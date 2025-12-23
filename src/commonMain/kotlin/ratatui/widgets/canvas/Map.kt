package ratatui.widgets.canvas

import ratatui.style.Color

/**
 * Defines how many points are going to be used to draw a [Map].
 *
 * You generally want a [High] resolution map.
 */
enum class MapResolution {
    /**
     * A lesser resolution for the [Map] [Shape].
     *
     * Contains about 1000 points.
     */
    Low,

    /**
     * A higher resolution for the [Map] [Shape].
     *
     * Contains about 5000 points, you likely want to use [ratatui.symbols.Marker.Braille] with this.
     */
    High;

    /**
     * Get the coordinate data for this resolution.
     */
    internal fun data(): List<Pair<Double, Double>> = when (this) {
        Low -> WorldData.LOW_RESOLUTION
        High -> WorldData.HIGH_RESOLUTION
    }

    companion object {
        fun default(): MapResolution = Low
    }
}

/**
 * A world map.
 *
 * A world map can be rendered with different [resolutions][MapResolution] and [colors][Color].
 *
 * @property resolution The resolution of the map (number of points used to draw the map)
 * @property color Map color (color of the points of the map)
 */
data class Map(
    val resolution: MapResolution = MapResolution.Low,
    val color: Color = Color.Reset
) : Shape {

    override fun draw(painter: Painter) {
        for ((x, y) in resolution.data()) {
            val point = painter.getPoint(x, y)
            if (point != null) {
                painter.paint(point.first, point.second, color)
            }
        }
    }
}
