# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 1/1 (100.0%)
- **Function parity:** 22/63 matched (target 38) — 34.9%
- **Class/type parity:** 1/2 matched (target 3) — 50.0%
- **Combined symbol parity:** 23/65 matched (target 41) — 35.4%
- **Average inline-code cosine:** 0.00 (function body across 1 matched files)
- **Average documentation cosine:** 0.72 (doc text across 1 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 1 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. buffer

- **Target:** `Buffer [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 426510.0
- **Functions:** 22/63 matched (target 38)
- **Missing functions:** `content`, `area`, `fmt`, `debug_empty_buffer`, `debug_grapheme_override`, `debug_some_example`, `it_translates_to_and_from_coordinates`, `pos_of_panics_on_out_of_bounds`, `index_of_panics_on_out_of_bounds`, `test_cell`, `test_cell_mut`, `index_out_of_bounds_panics`, `index_mut_out_of_bounds_panics`, `set_string_multi_width_overwrite`, `set_string_zero_width`, `set_string_double_width`, `small_one_line_buffer`, `set_line_raw`, `set_line_styled`, `set_style_does_not_panic_when_out_of_area`, `diff_empty_empty`, `diff_empty_filled`, `diff_filled_filled`, `diff_single_width`, `diff_multi_width`, `diff_multi_width_offset`, `merge_diff_idempotent`, `merge_diff_forcedwidth`, `merge_diff_link`, `merge_diff_split_link`, `merge_diff_image_sequences`, `diff_skip`, `merge_with_offset`, `merge_skip`, `with_lines_accepts_into_lines`, `control_sequence_rendered_full`, `control_sequence_rendered_partially`, `renders_emoji`, `index_pos_of_u16_max`, `diff_clears_trailing_cell_for_wide_grapheme`, `diff_ignores_style_only_changes_in_trailing_cells`
- **Types:** 1/2 matched (target 3)
- **Missing types:** `Output`
- **Tests:** 0/38 matched
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui-core/src/buffer/buffer.rs` vs expected `buffer.rs`
- **Proposed provenance header:** `// port-lint: source buffer.rs` (current: `// port-lint: source ratatui-core/src/buffer/buffer.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

