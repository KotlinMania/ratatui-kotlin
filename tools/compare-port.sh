#!/usr/bin/env bash
# Compare Rust source with Kotlin port to measure similarity

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
AST_DISTANCE="$SCRIPT_DIR/ast_distance"

RUST_WIDGETS="$SCRIPT_DIR/../ratatui-widgets/src"
KOTLIN_WIDGETS="$SCRIPT_DIR/../src/commonMain/kotlin/ratatui/widgets"

echo "=== ratatui-kotlin Port Similarity Report ==="
echo ""

compare() {
    local rust_file="$1"
    local kotlin_file="$2"
    local rust_path="$RUST_WIDGETS/$rust_file"
    local kotlin_path="$KOTLIN_WIDGETS/$kotlin_file"

    if [[ -f "$rust_path" && -f "$kotlin_path" ]]; then
        echo "--- $rust_file <-> $kotlin_file ---"
        "$AST_DISTANCE" "$rust_path" "$kotlin_path" 2>/dev/null | grep -E "(Cosine|Structure|Combined|Tree [12])"
        echo ""
    else
        echo "--- $rust_file: MISSING ---"
        [[ ! -f "$rust_path" ]] && echo "  Rust: $rust_path not found"
        [[ ! -f "$kotlin_path" ]] && echo "  Kotlin: $kotlin_path not found"
        echo ""
    fi
}

compare "sparkline.rs" "sparkline/Sparkline.kt"
compare "chart.rs" "chart/Chart.kt"
compare "calendar.rs" "calendar/Monthly.kt"
compare "logo.rs" "logo/RatatuiLogo.kt"
compare "mascot.rs" "mascot/RatatuiMascot.kt"

echo "=== Function-level comparison available with: ==="
echo "$AST_DISTANCE --compare-functions <rust_file> <kotlin_file>"
