# High Priority Ports - Action Plan

## Files by Impact

Priority = deps * 1,000,000 + SymDeficit * 10,000 + SrcSymbols * 100 + (1 - function similarity) * 10

Dependency fanout is ranked first so the ladder favors ports that clear downstream compilation failures fastest.

This list is complete and includes function/type detail for every matched file. Function similarity is the required body/parameter comparison; file-level shape does not rescue a port.

| Rank | Source | Target | Function similarity | Deps | Functions | Missing functions | Types | Missing types | SymDeficit | SrcSymbols | Priority |
|------|--------|--------|------------|------|-----------|-------------------|-------|---------------|-----------|------------|----------|
| 1 | `widgets.widget_ref` | `widgets.WidgetRef [PROVENANCE-FALLBACK]` | 0.16 | 0 | 1/11 matched (target 6) | `buf`, `render`, `render_ref_box`, `render_ref_box_vec`, `render_ref_some`, `render_ref_none`, `render_ref_str`, `render_ref_option_str`, `render_ref_string`, `render_ref_option_string` | 1/3 matched (target 1) | `Greeting`, `Farewell` | 12 | 14 | 121408.4 |
| 2 | `widgets.stateful_widget_ref` | `widgets.StatefulWidgetRef [PROVENANCE-FALLBACK]` | 0.09 | 0 | 1/7 matched (target 2) | `buf`, `state`, `render`, `box_render_ref`, `render_stateful_widget_ref_with_unsized_state`, `render_stateful_widget_with_unsized_state` | 1/4 matched (target 1) | `State`, `PersonalGreeting`, `Bytes` | 9 | 11 | 91109.1 |

## Cheat Detection / Scoring Failures

_None detected._

## Critical Issues (Function Similarity < 0.60 with Dependencies)

No critical issues with dependencies.

## Missing Files (by Dependents)

| Rank | Source file | Expected target | Deps | Functions | Classes/types | Symbols | Source path | Expected path |
|------|-------------|-----------------|------|-----------|---------------|---------|-------------|---------------|
| 1 | `init` | `Init` | 0 | 8 | 1 | 9 | `init.rs` | `Init.kt` |
| 2 | `prelude` | `Prelude` | 0 | 0 | 0 | 0 | `prelude.rs` | `Prelude.kt` |
| 3 | `widgets` | `widgets.Widgets` | 0 | 2 | 1 | 3 | `widgets.rs` | `widgets/Widgets.kt` |

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

