package ratatui.layout

import kasuari.*

/**
 * Represents the spacing between segments in a layout.
 *
 * The [Spacing] sealed class is used to define the spacing between segments in a layout. It can
 * represent either positive spacing (space between segments) or negative spacing (overlap between
 * segments).
 *
 * # Variants
 *
 * - [Space]: Represents positive spacing between segments. The value indicates the number of cells.
 * - [Overlap]: Represents negative spacing, causing overlap between segments. The value indicates
 *   the number of overlapping cells.
 *
 * # Default
 *
 * The default value for [Spacing] is `Space(0)`, which means no spacing or no overlap between
 * segments.
 */
sealed class Spacing {
    /** Positive spacing between segments */
    data class Space(val value: Int) : Spacing()
    /** Negative spacing (overlap) between segments */
    data class Overlap(val value: Int) : Spacing()

    companion object {
        fun default(): Spacing = Space(0)

        fun from(value: Int): Spacing = if (value < 0) {
            Overlap(-value)
        } else {
            Space(value)
        }
    }
}

/**
 * The primary layout engine for dividing terminal space using constraints and direction.
 *
 * A layout is a set of constraints that can be applied to a given area to split it into smaller
 * rectangular areas. This is the core building block for creating structured user interfaces in
 * terminal applications.
 *
 * A layout is composed of:
 * - a direction (horizontal or vertical)
 * - a set of constraints (length, ratio, percentage, fill, min, max)
 * - a margin (horizontal and vertical), the space between the edge of the main area and the split
 *   areas
 * - a flex option that controls space distribution
 * - a spacing option that controls gaps between segments
 *
 * The algorithm used to compute the layout is based on the kasuari Cassowary constraint solver.
 *
 * # Example
 *
 * ```kotlin
 * val area = Rect(0, 0, 80, 24)
 * val layout = Layout.vertical(listOf(Constraint.Length(5), Constraint.Fill(1)))
 * val (top, bottom) = layout.split(area)
 * ```
 */
data class Layout(
    private val direction: Direction = Direction.Vertical,
    private val constraints: List<Constraint> = emptyList(),
    private val margin: Margin = Margin.default(),
    private val flex: Flex = Flex.Start,
    private val spacing: Spacing = Spacing.default()
) {
    companion object {
        // Multiplier that decides floating point precision when rounding.
        private const val FLOAT_PRECISION_MULTIPLIER: Double = 100.0

        /**
         * Creates a new layout with default values.
         */
        fun default(): Layout = Layout()

        /**
         * Creates a new layout with the given direction and constraints.
         */
        fun new(direction: Direction, constraints: List<Constraint>): Layout =
            Layout(direction = direction, constraints = constraints)

        /**
         * Creates a new vertical layout with the given constraints.
         */
        fun vertical(constraints: List<Constraint>): Layout =
            Layout(direction = Direction.Vertical, constraints = constraints)

        /**
         * Creates a new horizontal layout with the given constraints.
         */
        fun horizontal(constraints: List<Constraint>): Layout =
            Layout(direction = Direction.Horizontal, constraints = constraints)
    }

    /**
     * Set the direction of the layout.
     */
    fun direction(direction: Direction): Layout = copy(direction = direction)

    /**
     * Sets the constraints of the layout.
     */
    fun constraints(constraints: List<Constraint>): Layout = copy(constraints = constraints)

    /**
     * Set the margin of the layout (uniform on all sides).
     */
    fun margin(margin: Int): Layout = copy(margin = Margin(horizontal = margin, vertical = margin))

    /**
     * Set the horizontal margin of the layout.
     */
    fun horizontalMargin(horizontal: Int): Layout =
        copy(margin = this.margin.copy(horizontal = horizontal))

    /**
     * Set the vertical margin of the layout.
     */
    fun verticalMargin(vertical: Int): Layout =
        copy(margin = this.margin.copy(vertical = vertical))

    /**
     * Set the flex behavior of the layout.
     */
    fun flex(flex: Flex): Layout = copy(flex = flex)

    /**
     * Sets the spacing between items in the layout.
     */
    fun spacing(spacing: Int): Layout = copy(spacing = Spacing.from(spacing))

    /**
     * Sets the spacing between items in the layout.
     */
    fun spacing(spacing: Spacing): Layout = copy(spacing = spacing)

    /**
     * Split the rect into sub-rects according to the layout.
     *
     * This method returns a list of [Rect]s that represent the areas computed by the layout.
     */
    fun split(area: Rect): List<Rect> = splitWithSpacers(area).first

    /**
     * Split the rect into sub-rects and also return spacers between them.
     *
     * This method returns a pair of lists: the first list contains the segment areas,
     * and the second list contains the spacer areas between segments.
     */
    fun splitWithSpacers(area: Rect): Pair<List<Rect>, List<Rect>> = trySplit(area)

    private fun trySplit(area: Rect): Pair<List<Rect>, List<Rect>> {
        val solver = Solver.new()

        val innerArea = area.inner(margin)
        val (areaStart, areaEnd) = when (direction) {
            Direction.Horizontal -> Pair(
                innerArea.x.toDouble() * FLOAT_PRECISION_MULTIPLIER,
                innerArea.right().toDouble() * FLOAT_PRECISION_MULTIPLIER
            )
            Direction.Vertical -> Pair(
                innerArea.y.toDouble() * FLOAT_PRECISION_MULTIPLIER,
                innerArea.bottom().toDouble() * FLOAT_PRECISION_MULTIPLIER
            )
        }

        val variableCount = constraints.size * 2 + 2
        val variables = (0 until variableCount).map { Variable.new() }
        val spacers = variables.windowed(2, 2).map { (a, b) -> Element(a, b) }
        val segments = variables.drop(1).windowed(2, 2).map { (a, b) -> Element(a, b) }

        val spacingValue = when (spacing) {
            is Spacing.Space -> spacing.value
            is Spacing.Overlap -> -spacing.value
        }

        val areaElement = Element(variables.first(), variables.last())
        configureArea(solver, areaElement, areaStart, areaEnd)
        configureVariableInAreaConstraints(solver, variables, areaElement)
        configureVariableConstraints(solver, variables)
        configureFlexConstraints(solver, areaElement, spacers, flex, spacingValue)
        configureConstraints(solver, areaElement, segments, constraints, flex)
        configureFillConstraints(solver, segments, constraints, flex)

        if (!flex.isLegacy()) {
            for (i in 0 until segments.size - 1) {
                val left = segments[i]
                val right = segments[i + 1]
                solver.addConstraint(left.hasSize(right, Strengths.ALL_SEGMENT_GROW))
            }
        }

        val changes: Map<Variable, Double> = solver.fetchChanges().toMap()

        val segmentRects = changesToRects(changes, segments, innerArea, direction)
        val spacerRects = changesToRects(changes, spacers, innerArea, direction)

        return Pair(segmentRects, spacerRects)
    }

    private fun configureArea(
        solver: Solver,
        area: Element,
        areaStart: Double,
        areaEnd: Double
    ) {
        solver.addConstraint(
            area.start with WeightedRelation.EQ(Strength.REQUIRED) to areaStart
        )
        solver.addConstraint(
            area.end with WeightedRelation.EQ(Strength.REQUIRED) to areaEnd
        )
    }

    private fun configureVariableInAreaConstraints(
        solver: Solver,
        variables: List<Variable>,
        area: Element
    ) {
        // All variables are in the range [area.start, area.end]
        for (variable in variables) {
            solver.addConstraint(
                variable with WeightedRelation.GE(Strength.REQUIRED) to area.start
            )
            solver.addConstraint(
                variable with WeightedRelation.LE(Strength.REQUIRED) to area.end
            )
        }
    }

    private fun configureVariableConstraints(
        solver: Solver,
        variables: List<Variable>
    ) {
        // Ensure variables are in order (left <= right for each pair)
        for (i in 1 until variables.size step 2) {
            if (i + 1 < variables.size) {
                solver.addConstraint(
                    variables[i] with WeightedRelation.LE(Strength.REQUIRED) to variables[i + 1]
                )
            }
        }
    }

    private fun configureConstraints(
        solver: Solver,
        area: Element,
        segments: List<Element>,
        constraints: List<Constraint>,
        flex: Flex
    ) {
        for ((constraint, segment) in constraints.zip(segments)) {
            when (constraint) {
                is Constraint.Max -> {
                    solver.addConstraint(segment.hasMaxSize(constraint.value.toInt(), Strengths.MAX_SIZE_LE))
                    solver.addConstraint(segment.hasIntSize(constraint.value.toInt(), Strengths.MAX_SIZE_EQ))
                }
                is Constraint.Min -> {
                    solver.addConstraint(segment.hasMinSize(constraint.value.toInt(), Strengths.MIN_SIZE_GE))
                    if (flex.isLegacy()) {
                        solver.addConstraint(segment.hasIntSize(constraint.value.toInt(), Strengths.MIN_SIZE_EQ))
                    } else {
                        solver.addConstraint(segment.hasSize(area, Strengths.FILL_GROW))
                    }
                }
                is Constraint.Length -> {
                    solver.addConstraint(segment.hasIntSize(constraint.value.toInt(), Strengths.LENGTH_SIZE_EQ))
                }
                is Constraint.Percentage -> {
                    val size = area.size() * constraint.value.toDouble() / 100.0
                    solver.addConstraint(segment.hasSize(size, Strengths.PERCENTAGE_SIZE_EQ))
                }
                is Constraint.Ratio -> {
                    // Avoid division by zero by using 1 when denominator is 0
                    val den = if (constraint.denominator == 0u) 1u else constraint.denominator
                    val size = area.size() * constraint.numerator.toDouble() / den.toDouble()
                    solver.addConstraint(segment.hasSize(size, Strengths.RATIO_SIZE_EQ))
                }
                is Constraint.Fill -> {
                    // Given no other constraints, this segment will grow as much as possible
                    solver.addConstraint(segment.hasSize(area, Strengths.FILL_GROW))
                }
            }
        }
    }

    private fun configureFlexConstraints(
        solver: Solver,
        area: Element,
        spacers: List<Element>,
        flex: Flex,
        spacing: Int
    ) {
        val spacersExceptFirstAndLast = if (spacers.size > 2) {
            spacers.subList(1, spacers.size - 1)
        } else {
            emptyList()
        }
        val spacingF64 = spacing.toDouble() * FLOAT_PRECISION_MULTIPLIER

        when (flex) {
            Flex.Legacy -> {
                for (spacer in spacersExceptFirstAndLast) {
                    solver.addConstraint(spacer.hasSize(spacingF64, Strengths.SPACER_SIZE_EQ))
                }
                spacers.firstOrNull()?.let { first ->
                    solver.addConstraint(first.isEmpty())
                }
                spacers.lastOrNull()?.let { last ->
                    solver.addConstraint(last.isEmpty())
                }
            }

            Flex.SpaceAround -> {
                if (spacers.size <= 2) {
                    // Fallback to SpaceEvenly
                    for (i in spacers.indices) {
                        for (j in i + 1 until spacers.size) {
                            solver.addConstraint(spacers[i].hasSize(spacers[j], Strengths.SPACER_SIZE_EQ))
                        }
                    }
                    for (spacer in spacers) {
                        solver.addConstraint(spacer.hasMinSize(spacing, Strengths.SPACER_SIZE_EQ))
                        solver.addConstraint(spacer.hasSize(area, Strengths.SPACE_GROW))
                    }
                } else {
                    val first = spacers.first()
                    val last = spacers.last()
                    val middle = spacers.subList(1, spacers.size - 1)

                    // All middle spacers should be equal in size
                    for (i in middle.indices) {
                        for (j in i + 1 until middle.size) {
                            solver.addConstraint(middle[i].hasSize(middle[j], Strengths.SPACER_SIZE_EQ))
                        }
                    }

                    // First and last spacers should be half the size of any middle spacer
                    middle.firstOrNull()?.let { firstMiddle ->
                        solver.addConstraint(firstMiddle.hasDoubleSize(first, Strengths.SPACER_SIZE_EQ))
                        solver.addConstraint(firstMiddle.hasDoubleSize(last, Strengths.SPACER_SIZE_EQ))
                    }

                    for (spacer in spacers) {
                        solver.addConstraint(spacer.hasMinSize(spacing, Strengths.SPACER_SIZE_EQ))
                        solver.addConstraint(spacer.hasSize(area, Strengths.SPACE_GROW))
                    }
                }
            }

            Flex.SpaceEvenly -> {
                for (i in spacers.indices) {
                    for (j in i + 1 until spacers.size) {
                        solver.addConstraint(spacers[i].hasSize(spacers[j], Strengths.SPACER_SIZE_EQ))
                    }
                }
                for (spacer in spacers) {
                    solver.addConstraint(spacer.hasMinSize(spacing, Strengths.SPACER_SIZE_EQ))
                    solver.addConstraint(spacer.hasSize(area, Strengths.SPACE_GROW))
                }
            }

            Flex.SpaceBetween -> {
                for (i in spacersExceptFirstAndLast.indices) {
                    for (j in i + 1 until spacersExceptFirstAndLast.size) {
                        solver.addConstraint(
                            spacersExceptFirstAndLast[i].hasSize(
                                spacersExceptFirstAndLast[j].size(),
                                Strengths.SPACER_SIZE_EQ
                            )
                        )
                    }
                }
                for (spacer in spacersExceptFirstAndLast) {
                    solver.addConstraint(spacer.hasMinSize(spacing, Strengths.SPACER_SIZE_EQ))
                    solver.addConstraint(spacer.hasSize(area, Strengths.SPACE_GROW))
                }
                spacers.firstOrNull()?.let { first ->
                    solver.addConstraint(first.isEmpty())
                }
                spacers.lastOrNull()?.let { last ->
                    solver.addConstraint(last.isEmpty())
                }
            }

            Flex.Start -> {
                for (spacer in spacersExceptFirstAndLast) {
                    solver.addConstraint(spacer.hasSize(spacingF64, Strengths.SPACER_SIZE_EQ))
                }
                spacers.firstOrNull()?.let { first ->
                    solver.addConstraint(first.isEmpty())
                }
                spacers.lastOrNull()?.let { last ->
                    solver.addConstraint(last.hasSize(area, Strengths.GROW))
                }
            }

            Flex.Center -> {
                for (spacer in spacersExceptFirstAndLast) {
                    solver.addConstraint(spacer.hasSize(spacingF64, Strengths.SPACER_SIZE_EQ))
                }
                val first = spacers.firstOrNull()
                val last = spacers.lastOrNull()
                if (first != null && last != null) {
                    solver.addConstraint(first.hasSize(area, Strengths.GROW))
                    solver.addConstraint(last.hasSize(area, Strengths.GROW))
                    solver.addConstraint(first.hasSize(last, Strengths.SPACER_SIZE_EQ))
                }
            }

            Flex.End -> {
                for (spacer in spacersExceptFirstAndLast) {
                    solver.addConstraint(spacer.hasSize(spacingF64, Strengths.SPACER_SIZE_EQ))
                }
                spacers.lastOrNull()?.let { last ->
                    solver.addConstraint(last.isEmpty())
                }
                spacers.firstOrNull()?.let { first ->
                    solver.addConstraint(first.hasSize(area, Strengths.GROW))
                }
            }
        }
    }

    private fun configureFillConstraints(
        solver: Solver,
        segments: List<Element>,
        constraints: List<Constraint>,
        flex: Flex
    ) {
        val fillSegments = constraints.zip(segments).filter { (c, _) ->
            c is Constraint.Fill || (!flex.isLegacy() && c is Constraint.Min)
        }

        for (i in fillSegments.indices) {
            for (j in i + 1 until fillSegments.size) {
                val (leftConstraint, leftSegment) = fillSegments[i]
                val (rightConstraint, rightSegment) = fillSegments[j]

                val leftScalingFactor = when (leftConstraint) {
                    is Constraint.Fill -> leftConstraint.value.toDouble().coerceAtLeast(1e-6)
                    is Constraint.Min -> 1.0
                    else -> 1.0
                }
                val rightScalingFactor = when (rightConstraint) {
                    is Constraint.Fill -> rightConstraint.value.toDouble().coerceAtLeast(1e-6)
                    is Constraint.Min -> 1.0
                    else -> 1.0
                }

                solver.addConstraint(
                    (rightScalingFactor * leftSegment.size()) with WeightedRelation.EQ(Strengths.GROW) to
                        (leftScalingFactor * rightSegment.size())
                )
            }
        }
    }

    private fun changesToRects(
        changes: Map<Variable, Double>,
        elements: List<Element>,
        area: Rect,
        direction: Direction
    ): List<Rect> {
        return elements.map { element ->
            val start = changes[element.start] ?: 0.0
            val end = changes[element.end] ?: 0.0
            val startInt = kotlin.math.round(kotlin.math.round(start) / FLOAT_PRECISION_MULTIPLIER).toInt()
            val endInt = kotlin.math.round(kotlin.math.round(end) / FLOAT_PRECISION_MULTIPLIER).toInt()
            val size = (endInt - startInt).coerceAtLeast(0)

            when (direction) {
                Direction.Horizontal -> Rect(
                    x = startInt,
                    y = area.y,
                    width = size,
                    height = area.height
                )
                Direction.Vertical -> Rect(
                    x = area.x,
                    y = startInt,
                    width = area.width,
                    height = size
                )
            }
        }
    }

    /**
     * A container used by the solver inside split
     */
    private data class Element(
        val start: Variable,
        val end: Variable
    ) {
        fun size(): Expression = end - start

        fun hasMaxSize(size: Int, strength: Strength): kasuari.Constraint =
            this.size() with WeightedRelation.LE(strength) to (size.toDouble() * FLOAT_PRECISION_MULTIPLIER)

        fun hasMinSize(size: Int, strength: Strength): kasuari.Constraint =
            this.size() with WeightedRelation.GE(strength) to (size.toDouble() * FLOAT_PRECISION_MULTIPLIER)

        fun hasIntSize(size: Int, strength: Strength): kasuari.Constraint =
            this.size() with WeightedRelation.EQ(strength) to (size.toDouble() * FLOAT_PRECISION_MULTIPLIER)

        fun hasSize(size: Expression, strength: Strength): kasuari.Constraint =
            this.size() with WeightedRelation.EQ(strength) to size

        fun hasSize(size: Double, strength: Strength): kasuari.Constraint =
            this.size() with WeightedRelation.EQ(strength) to size

        fun hasSize(other: Element, strength: Strength): kasuari.Constraint =
            this.size() with WeightedRelation.EQ(strength) to other.size()

        fun hasDoubleSize(other: Element, strength: Strength): kasuari.Constraint =
            this.size() with WeightedRelation.EQ(strength) to (other.size() * 2.0)

        fun isEmpty(): kasuari.Constraint =
            this.size() with WeightedRelation.EQ(Strength.REQUIRED - Strength.WEAK) to 0.0
    }

    /**
     * Strength constants for layout constraints
     */
    private object Strengths {
        /** The strength to apply to Spacers to ensure that their sizes are equal. */
        val SPACER_SIZE_EQ: Strength = Strength.REQUIRED / 10.0

        /** The strength to apply to Min inequality constraints. */
        val MIN_SIZE_GE: Strength = Strength.STRONG * 100.0

        /** The strength to apply to Max inequality constraints. */
        val MAX_SIZE_LE: Strength = Strength.STRONG * 100.0

        /** The strength to apply to Length constraints. */
        val LENGTH_SIZE_EQ: Strength = Strength.STRONG * 10.0

        /** The strength to apply to Percentage constraints. */
        val PERCENTAGE_SIZE_EQ: Strength = Strength.STRONG

        /** The strength to apply to Ratio constraints. */
        val RATIO_SIZE_EQ: Strength = Strength.STRONG / 10.0

        /** The strength to apply to Min equality constraints. */
        val MIN_SIZE_EQ: Strength = Strength.MEDIUM * 10.0

        /** The strength to apply to Max equality constraints. */
        val MAX_SIZE_EQ: Strength = Strength.MEDIUM * 10.0

        /** The strength to apply to Fill growing constraints. */
        val FILL_GROW: Strength = Strength.MEDIUM

        /** The strength to apply to growing constraints. */
        val GROW: Strength = Strength.MEDIUM / 10.0

        /** The strength to apply to Spacer growing constraints. */
        val SPACE_GROW: Strength = Strength.WEAK * 10.0

        /** The strength to apply to growing the size of all segments equally. */
        val ALL_SEGMENT_GROW: Strength = Strength.WEAK
    }
}
