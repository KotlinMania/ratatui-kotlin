#!/usr/bin/env bash
# Compare Rust source with Kotlin port to measure similarity

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
AST_DISTANCE="$SCRIPT_DIR/ast_distance"

# Use upstream ratatui if available, otherwise local
if [[ -d "/tmp/ratatui-upstream/ratatui-widgets/src" ]]; then
    RUST_WIDGETS="/tmp/ratatui-upstream/ratatui-widgets/src"
    echo "Using upstream ratatui source"
else
    RUST_WIDGETS="$SCRIPT_DIR/../ratatui-widgets/src"
    echo "Using local ratatui source (may be incomplete)"
fi
KOTLIN_WIDGETS="$SCRIPT_DIR/../src/commonMain/kotlin/ratatui/widgets"

echo ""
echo "=== ratatui-kotlin Port Similarity Report ==="
echo ""
printf "%-25s %8s %10s %15s\n" "Widget" "Cosine" "Combined" "Size (Kt/Rs)"
printf "%-25s %8s %10s %15s\n" "------" "------" "--------" "------------"

compare() {
    local rust_file="$1"
    local kotlin_file="$2"
    local rust_path="$RUST_WIDGETS/$rust_file"
    local kotlin_path="$KOTLIN_WIDGETS/$kotlin_file"

    if [[ -f "$rust_path" && -f "$kotlin_path" ]]; then
        result=$("$AST_DISTANCE" "$rust_path" "$kotlin_path" 2>/dev/null)
        cosine=$(echo "$result" | grep "Cosine" | awk '{print $3}')
        combined=$(echo "$result" | grep "Combined" | awk '{print $3}')
        rust_size=$(echo "$result" | grep "Tree 1:" | sed 's/.*size=\([0-9]*\).*/\1/')
        kotlin_size=$(echo "$result" | grep "Tree 2:" | sed 's/.*size=\([0-9]*\).*/\1/')
        ratio=$(echo "scale=0; $kotlin_size * 100 / $rust_size" | bc 2>/dev/null || echo "?")
        printf "%-25s %8s %10s %7s/%-5s (%2s%%)\n" \
            "${rust_file%.rs}" "$cosine" "$combined" "$kotlin_size" "$rust_size" "$ratio"
    elif [[ ! -f "$rust_path" ]]; then
        printf "%-25s %8s %10s %15s\n" "${rust_file%.rs}" "-" "-" "NO RUST SRC"
    else
        printf "%-25s %8s %10s %15s\n" "${rust_file%.rs}" "-" "-" "NO KOTLIN"
    fi
}

# All widgets
compare "barchart.rs" "barchart/BarChart.kt"
compare "block.rs" "block/Block.kt"
compare "borders.rs" "Borders.kt"
compare "canvas.rs" "canvas/Canvas.kt"
compare "chart.rs" "chart/Chart.kt"
compare "clear.rs" "Clear.kt"
compare "gauge.rs" "gauge/Gauge.kt"
compare "list.rs" "list/List.kt"
compare "paragraph.rs" "paragraph/Paragraph.kt"
compare "scrollbar.rs" "scrollbar/Scrollbar.kt"
compare "sparkline.rs" "sparkline/Sparkline.kt"
compare "table.rs" "table/Table.kt"
compare "tabs.rs" "tabs/Tabs.kt"
compare "calendar.rs" "calendar/Monthly.kt"
compare "logo.rs" "logo/RatatuiLogo.kt"
compare "mascot.rs" "mascot/RatatuiMascot.kt"

echo ""
echo "Combined Score: >0.7 good | 0.5-0.7 partial | <0.5 stub"
