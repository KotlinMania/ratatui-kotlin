# Code Port - Progress Report

**Generated:** 2026-08-27
**Source:** ratatui/src
**Target:** src/commonMain/kotlin

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Function parity | 2/28 matched (target 8) | 7.1% |
| Class/type parity | 2/9 matched (target 2) | 22.2% |
| Combined symbol parity | 4/37 matched (target 10) | 10.8% |
| Average function body similarity | 0.13 | inline-code cosine |
| Average documentation similarity | 0.81 | doc text cosine |
| Missing source functions | 10 | 0% parity until ported |
| Missing source classes/types | 2 | 0% parity until ported |
| Missing source symbol files | 2 | 12 symbols |
| Cheat/scoring failures | 0 | forced to 0% |
| Total source files | 6 | 100% |
| Target units (paired) | 141 | - |
| Target files (total) | 141 | - |
| Porting progress | 2 | 33.3% (matched) |
| Missing files | 3 | 50.0% |
| Reexport/wiring files | 1 | consult-only |

## Port Quality Analysis

**Average Function Similarity:** 0.13

Similarity in this report is the required function-by-function body/parameter score. Class/type parity and symbol deficits are reported beside it; whole-file shape is diagnostic only.

**Work Distribution:**
- Critical (<0.60): 2 files (100.0% of matched)
- Needs review (0.60-0.84): 0 files (0.0% of matched)

## Worst Function Scores First

Every matched file is listed from lowest function body/parameter similarity upward. Missing symbol names are not capped.

| Rank | Source | Target | Function similarity | Functions | Missing functions | Types | Missing types | Tests | Symbol deficit | Priority |
|------|--------|--------|---------------------|-----------|-------------------|-------|---------------|-------|----------------|----------|
| 1 | `widgets.stateful_widget_ref` | `widgets.StatefulWidgetRef [PROVENANCE-FALLBACK]` | 0.09 | 1/7 matched (target 2) | `buf`, `state`, `render`, `box_render_ref`, `render_stateful_widget_ref_with_unsized_state`, `render_stateful_widget_with_unsized_state` | 1/4 matched (target 1) | `State`, `PersonalGreeting`, `Bytes` | 0/6 | 9 | 91109.1 |
| 2 | `widgets.widget_ref` | `widgets.WidgetRef [PROVENANCE-FALLBACK]` | 0.16 | 1/11 matched (target 6) | `buf`, `render`, `render_ref_box`, `render_ref_box_vec`, `render_ref_some`, `render_ref_none`, `render_ref_str`, `render_ref_option_str`, `render_ref_string`, `render_ref_option_string` | 1/3 matched (target 1) | `Greeting`, `Farewell` | 0/10 | 12 | 121408.4 |

## Cheat Detection / Scoring Failures

_None detected._

### Critical Ports (Similarity < 0.60, Worst First)

These files need significant work:

- `widgets.stateful_widget_ref` -> `widgets.StatefulWidgetRef [PROVENANCE-FALLBACK]` (0.09)
- `widgets.widget_ref` -> `widgets.WidgetRef [PROVENANCE-FALLBACK]` (0.16)

## Incorrect Ports (Missing Types)

These files are matched (often via `// port-lint`) but appear to be missing one or more type declarations
present in the Rust source file.

| Source | Target | Missing types | Examples |
|--------|--------|---------------|----------|
| `widgets.widget_ref` | `widgets.WidgetRef [PROVENANCE-FALLBACK]` | 2/3 | `Greeting`, `Farewell` |
| `widgets.stateful_widget_ref` | `widgets.StatefulWidgetRef [PROVENANCE-FALLBACK]` | 3/4 | `State`, `PersonalGreeting`, `Bytes` |

## High Priority Missing Files

| Rank | Source file | Expected target | Deps | Functions | Classes/types | Symbols | Source path | Expected path |
|------|-------------|-----------------|------|-----------|---------------|---------|-------------|---------------|
| 1 | `init` | `Init` | 0 | 8 | 1 | 9 | `init.rs` | `Init.kt` |
| 2 | `widgets` | `widgets.Widgets` | 0 | 2 | 1 | 3 | `widgets.rs` | `widgets/Widgets.kt` |
| 3 | `prelude` | `Prelude` | 0 | 0 | 0 | 0 | `prelude.rs` | `Prelude.kt` |

## Documentation Gaps

There is missing documentation that is hurting overall scoring.

**Documentation coverage:** 148 / 398 lines (37%)

Documentation gaps (>20%), complete list:

- `widgets.widget_ref` - 65% gap (268 → 95 lines)
- `widgets.stateful_widget_ref` - 59% gap (130 → 53 lines)

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

