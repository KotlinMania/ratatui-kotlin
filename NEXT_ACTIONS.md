# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 2/6 (33.3%)
- **Function parity:** 2/28 matched (target 8) — 7.1%
- **Class/type parity:** 2/9 matched (target 2) — 22.2%
- **Combined symbol parity:** 4/37 matched (target 10) — 10.8%
- **Average inline-code cosine:** 0.13 (function body across 2 matched files)
- **Average documentation cosine:** 0.81 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. widgets.widget_ref

- **Target:** `widgets.WidgetRef [PROVENANCE-FALLBACK]`
- **Similarity:** 0.16
- **Dependents:** 0
- **Priority Score:** 121408.4
- **Functions:** 1/11 matched (target 6)
- **Missing functions:** `buf`, `render`, `render_ref_box`, `render_ref_box_vec`, `render_ref_some`, `render_ref_none`, `render_ref_str`, `render_ref_option_str`, `render_ref_string`, `render_ref_option_string`
- **Types:** 1/3 matched (target 1)
- **Missing types:** `Greeting`, `Farewell`
- **Tests:** 0/10 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui/src/widgets/widgetRef.rs` vs expected `widgets/widget_ref.rs`
- **Proposed provenance header:** `// port-lint: source widgets/widget_ref.rs` (current: `// port-lint: source ratatui/src/widgets/widgetRef.rs`)
- **Lint issues:** 1

### 2. widgets.stateful_widget_ref

- **Target:** `widgets.StatefulWidgetRef [PROVENANCE-FALLBACK]`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 91109.1
- **Functions:** 1/7 matched (target 2)
- **Missing functions:** `buf`, `state`, `render`, `box_render_ref`, `render_stateful_widget_ref_with_unsized_state`, `render_stateful_widget_with_unsized_state`
- **Types:** 1/4 matched (target 1)
- **Missing types:** `State`, `PersonalGreeting`, `Bytes`
- **Tests:** 0/6 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui/src/widgets/statefulWidgetRef.rs` vs expected `widgets/stateful_widget_ref.rs`
- **Proposed provenance header:** `// port-lint: source widgets/stateful_widget_ref.rs` (current: `// port-lint: source ratatui/src/widgets/statefulWidgetRef.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

