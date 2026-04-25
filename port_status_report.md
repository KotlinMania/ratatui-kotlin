# Code Port - Progress Report

**Generated:** 2026-04-25
**Source:** tmp/ratatui/ratatui/src
**Target:** src/commonMain/kotlin/ratatui

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Total source files | 6 | 100% |
| Target units (paired) | 95 | - |
| Target files (total) | 95 | - |
| Porting progress | 4 | 66.7% (matched) |
| Missing files | 2 | 33.3% |

## Port Quality Analysis

**Average Similarity:** 0.12

**Quality Distribution:**
- Excellent (≥0.85): 0 files (0.0% of matched)
- Good (0.60-0.84): 0 files (0.0% of matched)
- Critical (<0.60): 4 files (100.0% of matched)

### Excellent Ports (Similarity ≥ 0.85)

These files are well-ported and likely complete:


### Critical Ports (Similarity < 0.60)

These files need significant work:

- `widgets.widget_ref` → `widgets.WidgetRef` (0.20)
- `widgets` → `widgets.WidgetsModule` (0.00)
- `widgets.stateful_widget_ref` → `widgets.StatefulWidgetRef` (0.15)
- `lib` → `widgets.RatatuiWidgetsModule` (0.13)

## Incorrect Ports (Missing Types)

These files are matched (often via `// port-lint`) but appear to be missing one or more type declarations
present in the Rust source file.

| Source | Target | Missing types | Examples |
|--------|--------|---------------|----------|
| `widgets.widget_ref` | `widgets.WidgetRef` | 2/3 | `Greeting`, `Farewell` |
| `widgets` | `widgets.WidgetsModule` | 1/1 | `FrameExt` |
| `widgets.stateful_widget_ref` | `widgets.StatefulWidgetRef` | 3/4 | `State`, `PersonalGreeting`, `Bytes` |

## High Priority Missing Files

| Rank | Source file | Deps | Path |
|------|------------|------|------|
| 1 | `init` | 0 | `init.rs` |
| 2 | `prelude` | 0 | `prelude.rs` |

## Documentation Gaps

There is missing documentation that is hurting overall scoring.

**Documentation coverage:** 196 / 2654 lines (7%)

Top documentation gaps (>20%):

- `widgets` - 100% gap (1430 → 4 lines)
- `lib` - 95% gap (826 → 44 lines)
- `widgets.widget_ref` - 65% gap (268 → 95 lines)
- `widgets.stateful_widget_ref` - 59% gap (130 → 53 lines)

