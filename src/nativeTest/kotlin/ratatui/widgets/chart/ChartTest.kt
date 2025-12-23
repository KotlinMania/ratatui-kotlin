package ratatui.widgets.chart

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.symbols.Marker
import ratatui.widgets.block.Block
import ratatui.widgets.red
import ratatui.widgets.cyan
import ratatui.widgets.magenta
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for the Chart widget.
 */
class ChartTest {

    @Test
    fun chartDefault() {
        val chart = Chart.default()
        assertNotNull(chart)
    }

    @Test
    fun chartWithDatasets() {
        val datasets = listOf(
            Dataset.default()
                .name("data1")
                .data(0.0 to 0.0, 1.0 to 1.0, 2.0 to 2.0)
        )
        val chart = Chart.new(datasets)
        assertNotNull(chart)
    }

    @Test
    fun chartWithMultipleDatasets() {
        val datasets = listOf(
            Dataset.default()
                .name("scatter")
                .graphType(GraphType.Scatter)
                .data(0.0 to 0.0, 1.0 to 1.0),
            Dataset.default()
                .name("line")
                .graphType(GraphType.Line)
                .data(0.0 to 2.0, 2.0 to 0.0)
        )
        val chart = Chart.new(datasets)
        assertNotNull(chart)
    }

    @Test
    fun chartWithAxes() {
        val xAxis = Axis.default()
            .title("X Axis")
            .bounds(0.0, 10.0)
            .labels("0", "5", "10")

        val yAxis = Axis.default()
            .title("Y Axis")
            .bounds(0.0, 100.0)
            .labels("0", "50", "100")

        val chart = Chart.new(listOf(Dataset.default().data(5.0 to 50.0)))
            .xAxis(xAxis)
            .yAxis(yAxis)
        assertNotNull(chart)
    }

    @Test
    fun chartWithBlock() {
        val chart = Chart.new(listOf(Dataset.default()))
            .block(Block.bordered().title("Chart"))
        assertNotNull(chart)
    }

    @Test
    fun chartWithLegendPosition() {
        val chart = Chart.new(listOf(Dataset.default().name("data")))
            .legendPosition(LegendPosition.TopLeft)
        assertNotNull(chart)
    }

    @Test
    fun chartWithHiddenLegend() {
        val chart = Chart.new(listOf(Dataset.default().name("data")))
            .legendPosition(null)
        assertNotNull(chart)
    }

    @Test
    fun chartRenderInMinimalBuffer() {
        val buffer = Buffer.empty(Rect.new(0, 0, 1, 1))
        val chart = Chart.new(listOf(Dataset.default().data(1.0 to 1.0)))
        // This should not panic
        chart.render(buffer.area, buffer)
    }

    @Test
    fun chartRenderInZeroSizeBuffer() {
        val buffer = Buffer.empty(Rect.ZERO)
        val chart = Chart.new(listOf(Dataset.default()))
        // This should not panic
        chart.render(buffer.area, buffer)
    }

    @Test
    fun chartRenderWithAllFeatures() {
        val buffer = Buffer.empty(Rect.new(0, 0, 60, 20))
        val datasets = listOf(
            Dataset.default()
                .name("data1")
                .marker(Marker.Dot)
                .graphType(GraphType.Scatter)
                .cyan()
                .data(1.0 to 1.0, 2.0 to 2.0, 3.0 to 3.0),
            Dataset.default()
                .name("data2")
                .marker(Marker.Braille)
                .graphType(GraphType.Line)
                .magenta()
                .data(1.0 to 3.0, 2.0 to 2.0, 3.0 to 1.0)
        )
        val chart = Chart.new(datasets)
            .block(Block.bordered().title("Chart"))
            .xAxis(Axis.default().title("X").bounds(0.0, 4.0).labels("0", "2", "4"))
            .yAxis(Axis.default().title("Y").bounds(0.0, 4.0).labels("0", "2", "4"))
            .legendPosition(LegendPosition.TopRight)
        chart.render(buffer.area, buffer)
        // Just verify it doesn't panic
    }
}

/**
 * Tests for GraphType enum.
 */
class GraphTypeTest {

    @Test
    fun graphTypeToString() {
        assertEquals("Scatter", GraphType.Scatter.toString())
        assertEquals("Line", GraphType.Line.toString())
        assertEquals("Bar", GraphType.Bar.toString())
    }

    @Test
    fun graphTypeFromString() {
        assertEquals(GraphType.Scatter, GraphType.fromString("Scatter"))
        assertEquals(GraphType.Scatter, GraphType.fromString("scatter"))
        assertEquals(GraphType.Scatter, GraphType.fromString("SCATTER"))

        assertEquals(GraphType.Line, GraphType.fromString("Line"))
        assertEquals(GraphType.Line, GraphType.fromString("line"))

        assertEquals(GraphType.Bar, GraphType.fromString("Bar"))
        assertEquals(GraphType.Bar, GraphType.fromString("bar"))
    }

    @Test
    fun graphTypeFromStringInvalid() {
        assertNull(GraphType.fromString("invalid"))
        assertNull(GraphType.fromString(""))
    }

    @Test
    fun graphTypeParse() {
        assertEquals(GraphType.Scatter, GraphType.parse("scatter"))
        assertEquals(GraphType.Line, GraphType.parse("line"))
        assertEquals(GraphType.Bar, GraphType.parse("bar"))
    }
}

/**
 * Tests for Dataset.
 */
class DatasetTest {

    @Test
    fun datasetDefault() {
        val dataset = Dataset.default()
        assertNotNull(dataset)
    }

    @Test
    fun datasetWithName() {
        val dataset = Dataset.default().name("test")
        assertNotNull(dataset)
    }

    @Test
    fun datasetWithData() {
        val dataset = Dataset.default()
            .data(1.0 to 1.0, 2.0 to 2.0)
        assertNotNull(dataset)
    }

    @Test
    fun datasetWithMarker() {
        val dataset = Dataset.default().marker(Marker.Braille)
        assertNotNull(dataset)
    }

    @Test
    fun datasetWithGraphType() {
        val dataset = Dataset.default().graphType(GraphType.Line)
        assertNotNull(dataset)
    }

    @Test
    fun datasetWithStyle() {
        val dataset = Dataset.default()
            .style(Style.default().fg(Color.Cyan))
        assertNotNull(dataset)
    }

    @Test
    fun datasetWithStylize() {
        val dataset = Dataset.default()
            .red()
        assertNotNull(dataset)
    }
}

/**
 * Tests for Axis.
 */
class AxisTest {

    @Test
    fun axisDefault() {
        val axis = Axis.default()
        assertNotNull(axis)
    }

    @Test
    fun axisWithTitle() {
        val axis = Axis.default().title("Title")
        assertNotNull(axis)
    }

    @Test
    fun axisWithBounds() {
        val axis = Axis.default().bounds(0.0, 100.0)
        assertNotNull(axis)
    }

    @Test
    fun axisWithLabels() {
        val axis = Axis.default().labels("0", "50", "100")
        assertNotNull(axis)
    }

    @Test
    fun axisWithStyle() {
        val axis = Axis.default().style(Style.default().fg(Color.White))
        assertNotNull(axis)
    }

    @Test
    fun axisWithStylize() {
        val axis = Axis.default().red()
        assertNotNull(axis)
    }
}

/**
 * Tests for LegendPosition.
 */
class LegendPositionTest {

    @Test
    fun legendPositionDefault() {
        assertEquals(LegendPosition.TopRight, LegendPosition.default())
    }

    @Test
    fun legendPositionAllValues() {
        val positions = listOf(
            LegendPosition.Top,
            LegendPosition.TopRight,
            LegendPosition.TopLeft,
            LegendPosition.Left,
            LegendPosition.Right,
            LegendPosition.Bottom,
            LegendPosition.BottomRight,
            LegendPosition.BottomLeft
        )
        assertEquals(8, positions.size)
    }
}
