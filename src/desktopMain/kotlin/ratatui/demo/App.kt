// port-lint: source examples/apps/demo/src/app.rs
package ratatui.demo

import kotlin.math.sin
import kotlin.random.Random
import ratatui.widgets.list.ListState

private val TASKS: List<String> = listOf(
    "Item1", "Item2", "Item3", "Item4", "Item5", "Item6", "Item7", "Item8", "Item9", "Item10",
    "Item11", "Item12", "Item13", "Item14", "Item15", "Item16", "Item17", "Item18", "Item19",
    "Item20", "Item21", "Item22", "Item23", "Item24"
)

private val LOGS: List<Pair<String, String>> = listOf(
    Pair("Event1", "INFO"),
    Pair("Event2", "INFO"),
    Pair("Event3", "CRITICAL"),
    Pair("Event4", "ERROR"),
    Pair("Event5", "INFO"),
    Pair("Event6", "INFO"),
    Pair("Event7", "WARNING"),
    Pair("Event8", "INFO"),
    Pair("Event9", "INFO"),
    Pair("Event10", "INFO"),
    Pair("Event11", "CRITICAL"),
    Pair("Event12", "INFO"),
    Pair("Event13", "INFO"),
    Pair("Event14", "INFO"),
    Pair("Event15", "INFO"),
    Pair("Event16", "INFO"),
    Pair("Event17", "ERROR"),
    Pair("Event18", "ERROR"),
    Pair("Event19", "INFO"),
    Pair("Event20", "INFO"),
    Pair("Event21", "WARNING"),
    Pair("Event22", "INFO"),
    Pair("Event23", "INFO"),
    Pair("Event24", "WARNING"),
    Pair("Event25", "INFO"),
    Pair("Event26", "INFO")
)

private val EVENTS: List<Pair<String, Long>> = listOf(
    Pair("B1", 9),
    Pair("B2", 12),
    Pair("B3", 5),
    Pair("B4", 8),
    Pair("B5", 2),
    Pair("B6", 4),
    Pair("B7", 5),
    Pair("B8", 9),
    Pair("B9", 14),
    Pair("B10", 15),
    Pair("B11", 1),
    Pair("B12", 0),
    Pair("B13", 4),
    Pair("B14", 6),
    Pair("B15", 4),
    Pair("B16", 6),
    Pair("B17", 4),
    Pair("B18", 7),
    Pair("B19", 13),
    Pair("B20", 8),
    Pair("B21", 11),
    Pair("B22", 9),
    Pair("B23", 3),
    Pair("B24", 5)
)

class RandomSignal private constructor(
    private val distributionLower: ULong,
    private val distributionUpper: ULong,
    private val rng: Random
) : Iterator<ULong> {
    companion object {
        fun new(lower: ULong, upper: ULong): RandomSignal {
            require(lower < upper) { "invalid range" }
            return RandomSignal(
                distributionLower = lower,
                distributionUpper = upper,
                rng = Random.Default
            )
        }
    }

    override fun hasNext(): Boolean = true

    override fun next(): ULong {
        val lower = distributionLower.toLong()
        val upper = distributionUpper.toLong()
        return rng.nextLong(lower, upper).toULong()
    }
}

class SinSignal private constructor(
    private var x: Double,
    private val interval: Double,
    private val period: Double,
    private val scale: Double
) : Iterator<Pair<Double, Double>> {
    companion object {
        fun new(interval: Double, period: Double, scale: Double): SinSignal = SinSignal(
            x = 0.0,
            interval = interval,
            period = period,
            scale = scale
        )
    }

    override fun hasNext(): Boolean = true

    override fun next(): Pair<Double, Double> {
        val point = Pair(x, sin(x * 1.0 / period) * scale)
        x += interval
        return point
    }
}

class TabsState(
    val titles: List<String>,
    var index: Int = 0
) {
    companion object {
        fun new(titles: List<String>): TabsState = TabsState(titles = titles, index = 0)
    }

    fun next() {
        index = (index + 1) % titles.size
    }

    fun previous() {
        if (index > 0) {
            index -= 1
        } else {
            index = titles.size - 1
        }
    }
}

class StatefulList<T>(
    val state: ListState,
    val items: MutableList<T>
) {
    companion object {
        fun <T> withItems(items: List<T>): StatefulList<T> = StatefulList(
            state = ListState.default(),
            items = items.toMutableList()
        )
    }

    fun next() {
        val selected = state.selected
        val i = if (selected != null) {
            if (selected >= items.size - 1) 0 else selected + 1
        } else {
            0
        }
        state.select(i)
    }

    fun previous() {
        val selected = state.selected
        val i = if (selected != null) {
            if (selected == 0) items.size - 1 else selected - 1
        } else {
            0
        }
        state.select(i)
    }
}

class Signal<T>(
    private val source: Iterator<T>,
    val points: MutableList<T>,
    private val tickRate: Int
) {
    fun onTick() {
        val drain = minOf(tickRate, points.size).coerceAtLeast(0)
        if (drain > 0) {
            points.subList(0, drain).clear()
        }
        repeat(tickRate.coerceAtLeast(0)) {
            points.add(source.next())
        }
    }
}

class Signals(
    val sin1: Signal<Pair<Double, Double>>,
    val sin2: Signal<Pair<Double, Double>>,
    val window: DoubleArray
) {
    fun onTick() {
        sin1.onTick()
        sin2.onTick()
        window[0] += 1.0
        window[1] += 1.0
    }
}

data class Server(
    val name: String,
    val location: String,
    val coords: Pair<Double, Double>,
    val status: String
)

class App(
    val title: String,
    var shouldQuit: Boolean,
    val tabs: TabsState,
    var showChart: Boolean,
    var progress: Double,
    val sparkline: Signal<ULong>,
    val tasks: StatefulList<String>,
    val logs: StatefulList<Pair<String, String>>,
    val signals: Signals,
    val barchart: MutableList<Pair<String, Long>>,
    val servers: List<Server>,
    val enhancedGraphics: Boolean
) {
    companion object {
        fun new(title: String, enhancedGraphics: Boolean): App {
            val randSignal = RandomSignal.new(0uL, 100uL)
            val sparklinePoints = MutableList(300) { randSignal.next() }

            val sinSignal = SinSignal.new(0.2, 3.0, 18.0)
            val sin1Points = MutableList(100) { sinSignal.next() }

            val sinSignal2 = SinSignal.new(0.1, 2.0, 10.0)
            val sin2Points = MutableList(200) { sinSignal2.next() }

            return App(
                title = title,
                shouldQuit = false,
                tabs = TabsState.new(listOf("Tab0", "Tab1", "Tab2")),
                showChart = true,
                progress = 0.0,
                sparkline = Signal(
                    source = randSignal,
                    points = sparklinePoints,
                    tickRate = 1
                ),
                tasks = StatefulList.withItems(TASKS),
                logs = StatefulList.withItems(LOGS),
                signals = Signals(
                    sin1 = Signal(
                        source = sinSignal,
                        points = sin1Points,
                        tickRate = 5
                    ),
                    sin2 = Signal(
                        source = sinSignal2,
                        points = sin2Points,
                        tickRate = 10
                    ),
                    window = doubleArrayOf(0.0, 20.0)
                ),
                barchart = EVENTS.toMutableList(),
                servers = listOf(
                    Server(
                        name = "NorthAmerica-1",
                        location = "New York City",
                        coords = Pair(40.71, -74.00),
                        status = "Up"
                    ),
                    Server(
                        name = "Europe-1",
                        location = "Paris",
                        coords = Pair(48.85, 2.35),
                        status = "Failure"
                    ),
                    Server(
                        name = "SouthAmerica-1",
                        location = "São Paulo",
                        coords = Pair(-23.54, -46.62),
                        status = "Up"
                    ),
                    Server(
                        name = "Asia-1",
                        location = "Singapore",
                        coords = Pair(1.35, 103.86),
                        status = "Up"
                    )
                ),
                enhancedGraphics = enhancedGraphics
            )
        }
    }

    fun onUp() {
        tasks.previous()
    }

    fun onDown() {
        tasks.next()
    }

    fun onRight() {
        tabs.next()
    }

    fun onLeft() {
        tabs.previous()
    }

    fun onKey(c: Char) {
        when (c) {
            'q' -> shouldQuit = true
            't' -> showChart = !showChart
            else -> {}
        }
    }

    fun onTick() {
        // Update progress
        progress += 0.001
        if (progress > 1.0) {
            progress = 0.0
        }

        sparkline.onTick()
        signals.onTick()

        val log = logs.items.removeAt(logs.items.size - 1)
        logs.items.add(0, log)

        val event = barchart.removeAt(barchart.size - 1)
        barchart.add(0, event)
    }
}

