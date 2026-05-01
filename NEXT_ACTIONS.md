# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 49/61 (80.3%)
- **Function parity:** 346/829 matched (target 1250) — 41.7%
- **Class/type parity:** 68/95 matched (target 166) — 71.6%
- **Combined symbol parity:** 414/924 matched (target 1416) — 44.8%
- **Cheat-zeroed Files:** 14
- **Critical Issues:** 36 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. buffer
- **Similarity:** 0.00 (needs 85% improvement)
- **Dependencies:** 12
- **Priority Score:** 12000010.0
- **Functions:** 0/0 matched (target 44)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Action:** Deep review - likely missing major functionality

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. buffer

- **Target:** `buffer.BufferTest [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 12
- **Priority Score:** 12000010.0
- **Functions:** 0/0 matched (target 44)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui-core/src/buffer/buffer.rs` vs expected `buffer.rs`
- **Proposed provenance header:** `// port-lint: source buffer.rs` (current: `// port-lint: source ratatui-core/src/buffer/buffer.rs`)
- **Lint issues:** 1

### 2. layout.rect

- **Target:** `layout.Rect`
- **Similarity:** 0.29
- **Dependents:** 9
- **Priority Score:** 9244807.0
- **Functions:** 23/47 matched (target 33)
- **Missing functions:** `fmt`, `centered_horizontally`, `centered_vertically`, `centered`, `layout`, `layout_vec`, `try_layout`, `to_string`, `negative_offset`, `negative_offset_saturate`, `offset_saturate_max`, `intersection_underflow`, `mutual_intersect`, `size_truncation`, `size_preservation`, `resize_updates_size`, `resize_clamps_at_bounds`, `can_be_const`, `split`, `split_invalid_number_of_recs`, `from_position_and_size`, `from_size`, `layout_invalid_number_of_rects`, `try_layout_invalid_number_of_rects`
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 0/17 matched

### 3. backend

- **Target:** `backend.Backend [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 6
- **Priority Score:** 6020810.0
- **Functions:** 3/5 matched (target 4)
- **Missing functions:** `clear_type_tostring`, `clear_type_from_str`
- **Types:** 3/3 matched
- **Missing types:** _none_
- **Tests:** 0/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/backend.rs` vs expected `backend.rs`
- **Proposed provenance header:** `// port-lint: source backend.rs` (current: `// port-lint: source ratatui-core/src/backend.rs`)
- **Lint issues:** 1

### 4. widgets.widget

- **Target:** `widgets.Widget [PROVENANCE-FALLBACK]`
- **Similarity:** 0.72
- **Dependents:** 6
- **Priority Score:** 6011003.0
- **Functions:** 7/8 matched (target 11)
- **Missing functions:** `buf`
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 6/7 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/widgets/widget.rs` vs expected `widgets/widget.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/widgets/widget.rs` vs expected `widgets/widget.rs`
- **Proposed provenance header:** `// port-lint: source widgets/widget.rs` (current: `// port-lint: source ratatui-core/src/widgets/widget.rs`)
- **Proposed provenance header:** `// port-lint: source widgets/widget.rs` (current: `// port-lint: source ratatui-core/src/widgets/widget.rs`)
- **Lint issues:** 2

### 5. rect.iter

- **Target:** `rect.Iter`
- **Similarity:** 0.74
- **Dependents:** 5
- **Priority Score:** 5032002.5
- **Functions:** 14/16 matched (target 21)
- **Missing functions:** `columns_max`, `columns_min`
- **Types:** 3/4 matched
- **Missing types:** `Item`
- **Tests:** 10/12 matched

### 6. layout.position

- **Target:** `layout.Position [PROVENANCE-FALLBACK]`
- **Similarity:** 0.51
- **Dependents:** 4
- **Priority Score:** 4071805.0
- **Functions:** 10/16 matched (target 21)
- **Missing functions:** `fmt`, `add`, `sub`, `add_assign`, `sub_assign`, `to_string`
- **Types:** 1/2 matched
- **Missing types:** `Output`
- **Tests:** 7/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/position.rs` vs expected `layout/position.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/position.rs` vs expected `layout/position.rs`
- **Proposed provenance header:** `// port-lint: source layout/position.rs` (current: `// port-lint: source ratatui-core/src/layout/position.rs`)
- **Proposed provenance header:** `// port-lint: source layout/position.rs` (current: `// port-lint: source ratatui-core/src/layout/position.rs`)
- **Lint issues:** 2

### 7. style.color

- **Target:** `style.Color [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 4
- **Priority Score:** 4062610.0
- **Functions:** 16/21 matched (target 40)
- **Missing functions:** `fmt`, `test_hsl_to_rgb`, `test_hsluv_to_rgb`, `deserialize_error`, `test_from_array_and_tuple_conversions`
- **Types:** 4/5 matched (target 30)
- **Missing types:** `Err`
- **Tests:** 7/11 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/style/color.rs` vs expected `style/color.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/style/color.rs` vs expected `style/color.rs`
- **Proposed provenance header:** `// port-lint: source style/color.rs` (current: `// port-lint: source ratatui-core/src/style/color.rs`)
- **Proposed provenance header:** `// port-lint: source style/color.rs` (current: `// port-lint: source ratatui-core/src/style/color.rs`)
- **Lint issues:** 2

### 8. text.line

- **Target:** `text.Line [PROVENANCE-FALLBACK]`
- **Similarity:** 0.17
- **Dependents:** 3
- **Priority Score:** 3638508.2
- **Functions:** 20/80 matched (target 32)
- **Missing functions:** `fmt`, `cow_to_spans`, `iter`, `iter_mut`, `width_cjk`, `into_iter`, `add`, `add_assign`, `extend`, `spans_after_width`, `small_buf`, `raw_str`, `styled_str`, `styled_string`, `styled_cow`, `spans_vec`, `spans_iter`, `stylize`, `from_string`, `from_str`, `from_vec`, `from_slice_of_spans`, `from_slice_of_strs`, `from_slice_of_strings`, `collect`, `from_span`, `add_span`, `add_line`, `add_assign_span`, `into_string`, `display_line_from_vec`, `display_styled_line`, `display_line_from_styled_span`, `hello_world`, `render_out_of_bounds`, `render_only_styles_line_area`, `render_only_styles_first_line`, `render_truncates`, `render_centered`, `render_right_aligned`, `render_truncates_left`, `render_truncates_right`, `render_truncates_center`, `regression_1032`, `crab_emoji_width`, `render_truncates_emoji`, `render_truncates_emoji_center`, `render_truncates_away_from_0x0`, `render_right_aligned_multi_span`, `flag_emoji`, `render_truncates_flag`, `render_truncates_very_long_line_of_many_spans`, `render_truncates_very_long_single_span_line`, `render_with_newlines`, `into_iter_ref`, `into_iter_mut_ref`, `for_loop_ref`, `for_loop_mut_ref`, `for_loop_into`, `debug`
- **Types:** 2/5 matched (target 2)
- **Missing types:** `Item`, `IntoIter`, `Output`
- **Tests:** 0/50 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/text/line.rs` vs expected `text/line.rs`
- **Proposed provenance header:** `// port-lint: source text/line.rs` (current: `// port-lint: source ratatui-core/src/text/line.rs`)
- **Lint issues:** 1

### 9. style.stylize

- **Target:** `style.Stylize [PROVENANCE-FALLBACK]`
- **Similarity:** 0.31
- **Dependents:** 3
- **Priority Score:** 3142507.0
- **Functions:** 7/20 matched (target 391)
- **Missing functions:** `fmt`, `str_styled`, `string_styled`, `cow_string_styled`, `temporary_string_styled`, `other_primitives_styled`, `color_modifier`, `fg_bg`, `repeated_attributes`, `all_chained`, `stylize_debug_foreground`, `stylize_debug_background`, `stylize_debug_underline`
- **Types:** 4/5 matched (target 7)
- **Missing types:** `Item`
- **Tests:** 0/12 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/style/stylize.rs` vs expected `style/stylize.rs`
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui/tests/stylize.rs` vs expected `style/stylize.rs`
- **Proposed provenance header:** `// port-lint: source style/stylize.rs` (current: `// port-lint: source ratatui-core/src/style/stylize.rs`)
- **Proposed provenance header:** `// port-lint: source style/stylize.rs` (current: `// port-lint: source ratatui/tests/stylize.rs`)
- **Lint issues:** 2

### 10. style

- **Target:** `style.Style [PROVENANCE-FALLBACK]`
- **Similarity:** 0.63
- **Dependents:** 3
- **Priority Score:** 3083403.8
- **Functions:** 25/33 matched (target 102)
- **Missing functions:** `fmt`, `deserialize_modifier`, `fmt_stylize`, `combine_individual_modifiers`, `style_can_be_const`, `serialize_then_deserialize`, `deserialize_defaults`, `deserialize_null_modifiers`
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 15/20 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/style.rs` vs expected `style.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/style.rs` vs expected `style.rs`
- **Proposed provenance header:** `// port-lint: source style.rs` (current: `// port-lint: source ratatui-core/src/style.rs`)
- **Proposed provenance header:** `// port-lint: source style.rs` (current: `// port-lint: source ratatui-core/src/style.rs`)
- **Lint issues:** 2

### 11. buffer.cell

- **Target:** `buffer.Cell [PROVENANCE-FALLBACK]`
- **Similarity:** 0.79
- **Dependents:** 3
- **Priority Score:** 3022302.0
- **Functions:** 19/21 matched (target 50)
- **Missing functions:** `eq`, `hash`
- **Types:** 2/2 matched (target 7)
- **Missing types:** _none_
- **Tests:** 4/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/buffer/cell.rs` vs expected `buffer/cell.rs`
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui-widgets/src/table/cell.rs` vs expected `buffer/cell.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/buffer/cell.rs` vs expected `buffer/cell.rs`
- **Proposed provenance header:** `// port-lint: source buffer/cell.rs` (current: `// port-lint: source ratatui-core/src/buffer/cell.rs`)
- **Proposed provenance header:** `// port-lint: source buffer/cell.rs` (current: `// port-lint: source ratatui-widgets/src/table/cell.rs`)
- **Proposed provenance header:** `// port-lint: source buffer/cell.rs` (current: `// port-lint: source ratatui-core/src/buffer/cell.rs`)
- **Lint issues:** 3

### 12. layout.size

- **Target:** `layout.Size [PROVENANCE-FALLBACK]`
- **Similarity:** 0.24
- **Dependents:** 2
- **Priority Score:** 2050907.6
- **Functions:** 3/8 matched (target 6)
- **Missing functions:** `fmt`, `from_tuple`, `to_tuple`, `from_rect`, `display`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Tests:** 0/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/size.rs` vs expected `layout/size.rs`
- **Proposed provenance header:** `// port-lint: source layout/size.rs` (current: `// port-lint: source ratatui-core/src/layout/size.rs`)
- **Lint issues:** 1

### 13. layout.constraint

- **Target:** `layout.Constraint [PROVENANCE-FALLBACK]`
- **Similarity:** 0.73
- **Dependents:** 2
- **Priority Score:** 2021302.8
- **Functions:** 10/12 matched (target 32)
- **Missing functions:** `fmt`, `to_string`
- **Types:** 1/1 matched (target 8)
- **Missing types:** _none_
- **Tests:** 0/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/constraint.rs` vs expected `layout/constraint.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/constraint.rs` vs expected `layout/constraint.rs`
- **Proposed provenance header:** `// port-lint: source layout/constraint.rs` (current: `// port-lint: source ratatui-core/src/layout/constraint.rs`)
- **Proposed provenance header:** `// port-lint: source layout/constraint.rs` (current: `// port-lint: source ratatui-core/src/layout/constraint.rs`)
- **Lint issues:** 2

### 14. terminal

- **Target:** `terminal.Terminal [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2010310.0
- **Functions:** 0/1 matched (target 38)
- **Missing functions:** `drop`
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/terminal.rs` vs expected `terminal.rs`
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui/tests/terminal.rs` vs expected `terminal.rs`
- **Proposed provenance header:** `// port-lint: source terminal.rs` (current: `// port-lint: source ratatui-core/src/terminal.rs`)
- **Proposed provenance header:** `// port-lint: source terminal.rs` (current: `// port-lint: source ratatui/tests/terminal.rs`)
- **Lint issues:** 2

### 15. buffer.cell_width

- **Target:** `buffer.CellWidth [PROVENANCE-FALLBACK]`
- **Similarity:** 0.53
- **Dependents:** 2
- **Priority Score:** 2000504.8
- **Functions:** 4/4 matched (target 8)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/buffer/cellWidth.rs` vs expected `buffer/cell_width.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/buffer/cellWidth.rs` vs expected `buffer/cell_width.rs`
- **Proposed provenance header:** `// port-lint: source buffer/cell_width.rs` (current: `// port-lint: source ratatui-core/src/buffer/cellWidth.rs`)
- **Proposed provenance header:** `// port-lint: source buffer/cell_width.rs` (current: `// port-lint: source ratatui-core/src/buffer/cellWidth.rs`)
- **Lint issues:** 2

### 16. text.span

- **Target:** `text.Span [PROVENANCE-FALLBACK]`
- **Similarity:** 0.24
- **Dependents:** 1
- **Priority Score:** 1395607.6
- **Functions:** 15/52 matched (target 27)
- **Missing functions:** `fmt`, `to_left_aligned_line`, `to_centered_line`, `to_right_aligned_line`, `width_cjk`, `add`, `small_buf`, `default`, `raw_str`, `raw_string`, `styled_str`, `styled_string`, `set_content`, `from_ref_str_borrowed_cow`, `from_string_ref_str_borrowed_cow`, `from_string_owned_cow`, `from_ref_string_borrowed_cow`, `stylize`, `display_span`, `display_newline_span`, `display_styled_span`, `left_aligned`, `centered`, `right_aligned`, `render_out_of_bounds`, `render_truncates_too_long_content`, `render_patches_existing_style`, `render_multi_width_symbol`, `render_multi_width_symbol_truncates_entire_symbol`, `render_overflowing_area_truncates`, `render_first_zero_width`, `render_second_zero_width`, `render_middle_zero_width`, `render_last_zero_width`, `render_with_newlines`, `issue_1160`, `debug`
- **Types:** 2/4 matched (target 3)
- **Missing types:** `Output`, `Item`
- **Tests:** 0/31 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/text/span.rs` vs expected `text/span.rs`
- **Proposed provenance header:** `// port-lint: source text/span.rs` (current: `// port-lint: source ratatui-core/src/text/span.rs`)
- **Lint issues:** 1

### 17. text.masked

- **Target:** `text.Masked [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1051010.0
- **Functions:** 4/9 matched (target 12)
- **Missing functions:** `fmt`, `from`, `debug`, `display`, `into_cow`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 1/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/text/masked.rs` vs expected `text/masked.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/text/masked.rs` vs expected `text/masked.rs`
- **Proposed provenance header:** `// port-lint: source text/masked.rs` (current: `// port-lint: source ratatui-core/src/text/masked.rs`)
- **Proposed provenance header:** `// port-lint: source text/masked.rs` (current: `// port-lint: source ratatui-core/src/text/masked.rs`)
- **Lint issues:** 2

### 18. layout.margin

- **Target:** `layout.Margin [PROVENANCE-FALLBACK]`
- **Similarity:** 0.15
- **Dependents:** 1
- **Priority Score:** 1050708.5
- **Functions:** 1/6 matched (target 3)
- **Missing functions:** `from`, `fmt`, `margin_to_string`, `margin_new`, `from_u16`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Tests:** 0/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/margin.rs` vs expected `layout/margin.rs`
- **Proposed provenance header:** `// port-lint: source layout/margin.rs` (current: `// port-lint: source ratatui-core/src/layout/margin.rs`)
- **Lint issues:** 1

### 19. widgets.stateful_widget

- **Target:** `widgets.StatefulWidget [PROVENANCE-FALLBACK]`
- **Similarity:** 0.75
- **Dependents:** 1
- **Priority Score:** 1030802.5
- **Functions:** 2/4 matched (target 6)
- **Missing functions:** `buf`, `state`
- **Types:** 3/4 matched
- **Missing types:** `State`
- **Tests:** 2/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/widgets/statefulWidget.rs` vs expected `widgets/stateful_widget.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/widgets/statefulWidget.rs` vs expected `widgets/stateful_widget.rs`
- **Proposed provenance header:** `// port-lint: source widgets/stateful_widget.rs` (current: `// port-lint: source ratatui-core/src/widgets/statefulWidget.rs`)
- **Proposed provenance header:** `// port-lint: source widgets/stateful_widget.rs` (current: `// port-lint: source ratatui-core/src/widgets/statefulWidget.rs`)
- **Lint issues:** 2

### 20. layout.direction

- **Target:** `layout.Direction [PROVENANCE-FALLBACK]`
- **Similarity:** 0.17
- **Dependents:** 1
- **Priority Score:** 1030508.3
- **Functions:** 1/4 matched (target 2)
- **Missing functions:** `direction_to_string`, `direction_from_str`, `other`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Tests:** 0/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/direction.rs` vs expected `layout/direction.rs`
- **Proposed provenance header:** `// port-lint: source layout/direction.rs` (current: `// port-lint: source ratatui-core/src/layout/direction.rs`)
- **Lint issues:** 1

### 21. layout.alignment

- **Target:** `layout.Alignment [PROVENANCE-FALLBACK]`
- **Similarity:** 0.37
- **Dependents:** 1
- **Priority Score:** 1020706.2
- **Functions:** 2/4 matched (target 7)
- **Missing functions:** `alignment_from_str`, `vertical_alignment_from_str`
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_
- **Tests:** 2/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/alignment.rs` vs expected `layout/alignment.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/alignment.rs` vs expected `layout/alignment.rs`
- **Proposed provenance header:** `// port-lint: source layout/alignment.rs` (current: `// port-lint: source ratatui-core/src/layout/alignment.rs`)
- **Proposed provenance header:** `// port-lint: source layout/alignment.rs` (current: `// port-lint: source ratatui-core/src/layout/alignment.rs`)
- **Lint issues:** 2

### 22. terminal.viewport

- **Target:** `terminal.Viewport [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1020310.0
- **Functions:** 0/2 matched (target 7)
- **Missing functions:** `fmt`, `viewport_to_string`
- **Types:** 1/1 matched (target 4)
- **Missing types:** _none_
- **Tests:** 0/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/terminal/viewport.rs` vs expected `terminal/viewport.rs`
- **Proposed provenance header:** `// port-lint: source terminal/viewport.rs` (current: `// port-lint: source ratatui-core/src/terminal/viewport.rs`)
- **Lint issues:** 1

### 23. layout.offset

- **Target:** `layout.Offset [PROVENANCE-FALLBACK]`
- **Similarity:** 0.73
- **Dependents:** 1
- **Priority Score:** 1000602.8
- **Functions:** 5/5 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/offset.rs` vs expected `layout/offset.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/offset.rs` vs expected `layout/offset.rs`
- **Proposed provenance header:** `// port-lint: source layout/offset.rs` (current: `// port-lint: source ratatui-core/src/layout/offset.rs`)
- **Proposed provenance header:** `// port-lint: source layout/offset.rs` (current: `// port-lint: source ratatui-core/src/layout/offset.rs`)
- **Lint issues:** 2

### 24. layout.flex

- **Target:** `layout.Flex [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1000110.0
- **Functions:** 0/0 matched (target 7)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/flex.rs` vs expected `layout/flex.rs`
- **Proposed provenance header:** `// port-lint: source layout/flex.rs` (current: `// port-lint: source ratatui-core/src/layout/flex.rs`)
- **Lint issues:** 1

### 25. text.text

- **Target:** `text.Text [PROVENANCE-FALLBACK]`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 547508.1
- **Functions:** 19/70 matched (target 33)
- **Missing functions:** `fmt`, `iter`, `iter_mut`, `width_cjk`, `into_iter`, `add`, `add_assign`, `small_buf`, `from_string`, `from_str`, `from_cow`, `from_span`, `from_line`, `from_slice_of_spans`, `from_slice_of_lines`, `from_slice_of_strs`, `from_slice_of_strings`, `from_vec_line`, `from_iterator`, `collect`, `add_line`, `add_text`, `add_assign_text`, `add_assign_line`, `extend_from_iter`, `extend_from_iter_str`, `display_raw_text`, `display_styled_text`, `display_text_from_vec`, `display_extended_text`, `stylize`, `push_line_empty`, `push_span_empty`, `render_out_of_bounds`, `render_right_aligned`, `render_centered_odd`, `render_centered_even`, `render_right_aligned_with_truncation`, `render_centered_odd_with_truncation`, `render_centered_even_with_truncation`, `render_one_line_right`, `render_only_styles_line_area`, `render_truncates`, `hello_world`, `into_iter_ref`, `into_iter_mut_ref`, `for_loop_ref`, `for_loop_mut_ref`, `for_loop_into`, `debug`, `debug_alternate`
- **Types:** 2/5 matched (target 2)
- **Missing types:** `Item`, `IntoIter`, `Output`
- **Tests:** 0/44 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/text/text.rs` vs expected `text/text.rs`
- **Proposed provenance header:** `// port-lint: source text/text.rs` (current: `// port-lint: source ratatui-core/src/text/text.rs`)
- **Lint issues:** 1

### 26. layout.layout

- **Target:** `layout.Layout [PROVENANCE-FALLBACK]`
- **Similarity:** 0.26
- **Dependents:** 0
- **Priority Score:** 508207.4
- **Functions:** 29/75 matched (target 41)
- **Missing functions:** `init_cache`, `resize_cache`, `areas`, `try_areas`, `spacers`, `cached_split`, `split_layout`, `round`, `debug_elements`, `strength_is_valid`, `cache_size`, `margins`, `letters`, `length`, `max`, `min`, `percentage`, `percentage_start`, `percentage_spacebetween`, `ratio`, `ratio_start`, `ratio_spacebetween`, `vertical_split_by_height`, `edge_cases`, `constraint_length`, `table_length`, `length_is_higher_priority`, `length_is_higher_priority_in_flex`, `fixed_with_50_width`, `fill`, `percentage_parameterized`, `min_max`, `flex_constraint`, `flex_overlap`, `flex_spacing`, `constraint_specification_tests_for_priority`, `constraint_specification_tests_for_priority_with_spacing`, `fill_vs_flex`, `fill_spacing`, `fill_overlap`, `flex_spacing_lower_priority_than_user_spacing`, `split_with_spacers_no_spacing`, `split_with_spacers_and_spacing`, `split_with_spacers_and_overlap`, `split_with_spacers_and_too_much_spacing`, `legacy_vs_default`
- **Types:** 3/7 matched (target 6)
- **Missing types:** `Rects`, `Segments`, `Spacers`, `Cache`
- **Tests:** 0/37 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/layout.rs` vs expected `layout/layout.rs`
- **Proposed provenance header:** `// port-lint: source layout/layout.rs` (current: `// port-lint: source ratatui-core/src/layout/layout.rs`)
- **Lint issues:** 1

### 27. buffer.buffer

- **Target:** `buffer.Buffer [PROVENANCE-FALLBACK]`
- **Similarity:** 0.25
- **Dependents:** 0
- **Priority Score:** 426507.4
- **Functions:** 22/63 matched (target 35)
- **Missing functions:** `index`, `index_mut`, `fmt`, `debug_empty_buffer`, `debug_grapheme_override`, `debug_some_example`, `it_translates_to_and_from_coordinates`, `pos_of_panics_on_out_of_bounds`, `index_of_panics_on_out_of_bounds`, `test_cell`, `test_cell_mut`, `index_out_of_bounds_panics`, `index_mut_out_of_bounds_panics`, `set_string_multi_width_overwrite`, `set_string_zero_width`, `set_string_double_width`, `small_one_line_buffer`, `set_line_raw`, `set_line_styled`, `set_style_does_not_panic_when_out_of_area`, `diff_empty_empty`, `diff_empty_filled`, `diff_filled_filled`, `diff_single_width`, `diff_multi_width`, `diff_multi_width_offset`, `merge_diff_idempotent`, `merge_diff_forcedwidth`, `merge_diff_link`, `merge_diff_split_link`, `merge_diff_image_sequences`, `diff_skip`, `merge_with_offset`, `merge_skip`, `with_lines_accepts_into_lines`, `control_sequence_rendered_full`, `control_sequence_rendered_partially`, `renders_emoji`, `index_pos_of_u16_max`, `diff_clears_trailing_cell_for_wide_grapheme`, `diff_ignores_style_only_changes_in_trailing_cells`
- **Types:** 1/2 matched (target 3)
- **Missing types:** `Output`
- **Tests:** 0/38 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/buffer/buffer.rs` vs expected `buffer/buffer.rs`
- **Proposed provenance header:** `// port-lint: source buffer/buffer.rs` (current: `// port-lint: source ratatui-core/src/buffer/buffer.rs`)
- **Lint issues:** 1

### 28. backend.test

- **Target:** `backend.TestBackend [PROVENANCE-FALLBACK]`
- **Similarity:** 0.32
- **Dependents:** 0
- **Priority Score:** 245006.8
- **Functions:** 25/47 matched (target 29)
- **Missing functions:** `assert_cursor_position`, `fmt`, `scroll_region_up`, `scroll_region_down`, `test_buffer_view`, `buffer_view_with_overwrites`, `assert_buffer_panics`, `assert_scrollback_panics`, `display`, `clear_region_all`, `clear_region_after_cursor`, `clear_region_before_cursor`, `clear_region_current_line`, `clear_region_until_new_line`, `append_lines_not_at_last_line`, `append_lines_at_last_line`, `append_multiple_lines_not_at_last_line`, `append_multiple_lines_past_last_line`, `append_multiple_lines_where_cursor_at_end_appends_height_lines`, `append_multiple_lines_where_cursor_appends_height_lines`, `append_multiple_lines_where_cursor_at_end_appends_more_than_height_lines`, `append_lines_truncates_beyond_u16_max`
- **Types:** 1/3 matched (target 1)
- **Missing types:** `Result`, `Error`
- **Tests:** 0/18 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/backend/test.rs` vs expected `backend/test.rs`
- **Proposed provenance header:** `// port-lint: source backend/test.rs` (current: `// port-lint: source ratatui-core/src/backend/test.rs`)
- **Lint issues:** 1

### 29. symbols.border

- **Target:** `border.Border [PROVENANCE-FALLBACK]`
- **Similarity:** 0.08
- **Dependents:** 0
- **Priority Score:** 202309.2
- **Functions:** 2/22 matched (target 2)
- **Missing functions:** `render`, `border_set_from_line_set`, `plain`, `rounded`, `double`, `thick`, `light_double_dashed`, `heavy_double_dashed`, `light_triple_dashed`, `heavy_triple_dashed`, `light_quadruple_dashed`, `heavy_quadruple_dashed`, `quadrant_outside`, `quadrant_inside`, `one_eighth_wide`, `one_eighth_tall`, `proportional_wide`, `proportional_tall`, `full`, `empty`
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 0/20 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/border.rs` vs expected `symbols/border.rs`
- **Proposed provenance header:** `// port-lint: source symbols/border.rs` (current: `// port-lint: source ratatui-core/src/symbols/border.rs`)
- **Lint issues:** 1

### 30. rect.ops

- **Target:** `rect.Ops [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 101010.0
- **Functions:** 0/9 matched (target 10)
- **Missing functions:** `neg`, `add`, `sub`, `add_assign`, `sub_assign`, `add_offset`, `sub_offset`, `add_assign_offset`, `sub_assign_offset`
- **Types:** 0/1 matched (target 0)
- **Missing types:** `Output`
- **Tests:** 0/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/layout/rect/ops.rs` vs expected `layout/rect/ops.rs`
- **Proposed provenance header:** `// port-lint: source layout/rect/ops.rs` (current: `// port-lint: source ratatui-core/src/layout/rect/ops.rs`)
- **Lint issues:** 1

### 31. terminal.cursor

- **Target:** `terminal.CursorTest [PROVENANCE-FALLBACK]`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 61106.1
- **Functions:** 5/11 matched (target 5)
- **Missing functions:** `hide_cursor`, `show_cursor`, `get_cursor`, `set_cursor`, `get_cursor_position`, `set_cursor_position`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 5/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/terminal/cursor.rs` vs expected `terminal/cursor.rs`
- **Proposed provenance header:** `// port-lint: source terminal/cursor.rs` (current: `// port-lint: source ratatui-core/src/terminal/cursor.rs`)
- **Lint issues:** 1

### 32. terminal.backend

- **Target:** `backend.ClearTypeTest [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 60610.0
- **Functions:** 0/6 matched (target 2)
- **Missing functions:** `backend`, `backend_mut`, `size`, `backend_returns_shared_reference`, `backend_mut_allows_mutating_backend_state`, `size_queries_underlying_backend_size`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 0/3 matched
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui-core/src/backend.rs` vs expected `terminal/backend.rs`
- **Proposed provenance header:** `// port-lint: source terminal/backend.rs` (current: `// port-lint: source ratatui-core/src/backend.rs`)
- **Lint issues:** 1

### 33. symbols.merge

- **Target:** `merge.Merge [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 51410.0
- **Functions:** 6/10 matched (target 11)
- **Missing functions:** `new`, `replace_merge_strategy`, `exact_merge_strategy`, `fuzzy_merge_strategy`
- **Types:** 3/4 matched (target 3)
- **Missing types:** `BorderSymbolError`
- **Tests:** 0/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/merge.rs` vs expected `symbols/merge.rs`
- **Proposed provenance header:** `// port-lint: source symbols/merge.rs` (current: `// port-lint: source ratatui-core/src/symbols/merge.rs`)
- **Lint issues:** 1

### 34. symbols.line

- **Target:** `line.Line [PROVENANCE-FALLBACK]`
- **Similarity:** 0.14
- **Dependents:** 0
- **Priority Score:** 50708.6
- **Functions:** 1/6 matched (target 3)
- **Missing functions:** `render`, `normal`, `rounded`, `double`, `thick`
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 0/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/line.rs` vs expected `symbols/line.rs`
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui-widgets/src/canvas/line.rs` vs expected `symbols/line.rs`
- **Proposed provenance header:** `// port-lint: source symbols/line.rs` (current: `// port-lint: source ratatui-core/src/symbols/line.rs`)
- **Proposed provenance header:** `// port-lint: source symbols/line.rs` (current: `// port-lint: source ratatui-widgets/src/canvas/line.rs`)
- **Lint issues:** 2

### 35. style.anstyle

- **Target:** `style.Anstyle [PROVENANCE-FALLBACK]`
- **Similarity:** 0.43
- **Dependents:** 0
- **Priority Score:** 31805.7
- **Functions:** 14/16 matched (target 26)
- **Missing functions:** `from`, `try_from`
- **Types:** 1/2 matched (target 3)
- **Missing types:** `Error`
- **Tests:** 14/14 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/style/anstyle.rs` vs expected `style/anstyle.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/style/anstyle.rs` vs expected `style/anstyle.rs`
- **Proposed provenance header:** `// port-lint: source style/anstyle.rs` (current: `// port-lint: source ratatui-core/src/style/anstyle.rs`)
- **Proposed provenance header:** `// port-lint: source style/anstyle.rs` (current: `// port-lint: source ratatui-core/src/style/anstyle.rs`)
- **Lint issues:** 2

### 36. buffer.assert

- **Target:** `buffer.Assert [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 30310.0
- **Functions:** 0/3 matched (target 4)
- **Missing functions:** `assert_buffer_eq_does_not_panic_on_equal_buffers`, `assert_buffer_eq_panics_on_unequal_area`, `assert_buffer_eq_panics_on_unequal_style`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 0/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/buffer/assert.rs` vs expected `buffer/assert.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/buffer/assert.rs` vs expected `buffer/assert.rs`
- **Proposed provenance header:** `// port-lint: source buffer/assert.rs` (current: `// port-lint: source ratatui-core/src/buffer/assert.rs`)
- **Proposed provenance header:** `// port-lint: source buffer/assert.rs` (current: `// port-lint: source ratatui-core/src/buffer/assert.rs`)
- **Lint issues:** 2

### 37. text.grapheme

- **Target:** `text.Grapheme [PROVENANCE-FALLBACK]`
- **Similarity:** 0.93
- **Dependents:** 0
- **Priority Score:** 10700.7
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 1/2 matched
- **Missing types:** `Item`
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/text/grapheme.rs` vs expected `text/grapheme.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/text/grapheme.rs` vs expected `text/grapheme.rs`
- **Proposed provenance header:** `// port-lint: source text/grapheme.rs` (current: `// port-lint: source ratatui-core/src/text/grapheme.rs`)
- **Proposed provenance header:** `// port-lint: source text/grapheme.rs` (current: `// port-lint: source ratatui-core/src/text/grapheme.rs`)
- **Lint issues:** 2

### 38. buffer.diff

- **Target:** `buffer.BufferDiff [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1710.0
- **Functions:** 14/14 matched (target 17)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_
- **Tests:** 10/10 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/buffer/diff.rs` vs expected `buffer/diff.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/buffer/diff.rs` vs expected `buffer/diff.rs`
- **Proposed provenance header:** `// port-lint: source buffer/diff.rs` (current: `// port-lint: source ratatui-core/src/buffer/diff.rs`)
- **Proposed provenance header:** `// port-lint: source buffer/diff.rs` (current: `// port-lint: source ratatui-core/src/buffer/diff.rs`)
- **Lint issues:** 2

### 39. terminal.frame

- **Target:** `terminal.Frame [PROVENANCE-FALLBACK]`
- **Similarity:** 0.94
- **Dependents:** 0
- **Priority Score:** 1000.6
- **Functions:** 8/8 matched (target 10)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/terminal/frame.rs` vs expected `terminal/frame.rs`
- **Proposed provenance header:** `// port-lint: source terminal/frame.rs` (current: `// port-lint: source ratatui-core/src/terminal/frame.rs`)
- **Lint issues:** 1

### 40. symbols.marker

- **Target:** `symbols.Marker [PROVENANCE-FALLBACK]`
- **Similarity:** 0.68
- **Dependents:** 0
- **Priority Score:** 303.2
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 11)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/marker.rs` vs expected `symbols/marker.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/marker.rs` vs expected `symbols/marker.rs`
- **Proposed provenance header:** `// port-lint: source symbols/marker.rs` (current: `// port-lint: source ratatui-core/src/symbols/marker.rs`)
- **Proposed provenance header:** `// port-lint: source symbols/marker.rs` (current: `// port-lint: source ratatui-core/src/symbols/marker.rs`)
- **Lint issues:** 2

### 41. palette.material

- **Target:** `material.Material [PROVENANCE-FALLBACK]`
- **Similarity:** 0.91
- **Dependents:** 0
- **Priority Score:** 300.9
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/style/palette/material.rs` vs expected `style/palette/material.rs`
- **Proposed provenance header:** `// port-lint: source style/palette/material.rs` (current: `// port-lint: source ratatui-core/src/style/palette/material.rs`)
- **Lint issues:** 1

### 42. symbols.block

- **Target:** `block.Block [PROVENANCE-FALLBACK]`
- **Similarity:** 0.99
- **Dependents:** 0
- **Priority Score:** 200.1
- **Functions:** 1/1 matched (target 43)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 4)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/block.rs` vs expected `symbols/block.rs`
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui-widgets/src/block.rs` vs expected `symbols/block.rs`
- **Proposed provenance header:** `// port-lint: source symbols/block.rs` (current: `// port-lint: source ratatui-core/src/symbols/block.rs`)
- **Proposed provenance header:** `// port-lint: source symbols/block.rs` (current: `// port-lint: source ratatui-widgets/src/block.rs`)
- **Lint issues:** 2

### 43. symbols.bar

- **Target:** `bar.Bar [PROVENANCE-FALLBACK]`
- **Similarity:** 0.99
- **Dependents:** 0
- **Priority Score:** 200.1
- **Functions:** 1/1 matched (target 16)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/bar.rs` vs expected `symbols/bar.rs`
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui-widgets/src/barchart/bar.rs` vs expected `symbols/bar.rs`
- **Proposed provenance header:** `// port-lint: source symbols/bar.rs` (current: `// port-lint: source ratatui-core/src/symbols/bar.rs`)
- **Proposed provenance header:** `// port-lint: source symbols/bar.rs` (current: `// port-lint: source ratatui-widgets/src/barchart/bar.rs`)
- **Lint issues:** 2

### 44. symbols.scrollbar

- **Target:** `scrollbar.Scrollbar [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 0/0 matched (target 38)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 5)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/scrollbar.rs` vs expected `symbols/scrollbar.rs`
- **Provenance warning:** port-lint provenance header matched only by basename: `ratatui-widgets/src/scrollbar.rs` vs expected `symbols/scrollbar.rs`
- **Proposed provenance header:** `// port-lint: source symbols/scrollbar.rs` (current: `// port-lint: source ratatui-core/src/symbols/scrollbar.rs`)
- **Proposed provenance header:** `// port-lint: source symbols/scrollbar.rs` (current: `// port-lint: source ratatui-widgets/src/scrollbar.rs`)
- **Lint issues:** 2

### 45. palette.tailwind

- **Target:** `tailwind.Tailwind [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/style/palette/tailwind.rs` vs expected `style/palette/tailwind.rs`
- **Proposed provenance header:** `// port-lint: source style/palette/tailwind.rs` (current: `// port-lint: source ratatui-core/src/style/palette/tailwind.rs`)
- **Lint issues:** 1

### 46. symbols.half_block

- **Target:** `symbols.HalfBlock [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/halfBlock.rs` vs expected `symbols/half_block.rs`
- **Proposed provenance header:** `// port-lint: source symbols/half_block.rs` (current: `// port-lint: source ratatui-core/src/symbols/halfBlock.rs`)
- **Lint issues:** 1

### 47. symbols.shade

- **Target:** `shade.Shade [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/shade.rs` vs expected `symbols/shade.rs`
- **Proposed provenance header:** `// port-lint: source symbols/shade.rs` (current: `// port-lint: source ratatui-core/src/symbols/shade.rs`)
- **Lint issues:** 1

### 48. symbols.braille

- **Target:** `symbols.Braille [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/braille.rs` vs expected `symbols/braille.rs`
- **Proposed provenance header:** `// port-lint: source symbols/braille.rs` (current: `// port-lint: source ratatui-core/src/symbols/braille.rs`)
- **Lint issues:** 1

### 49. symbols.pixel

- **Target:** `symbols.Pixel [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ratatui-core/src/symbols/pixel.rs` vs expected `symbols/pixel.rs`
- **Proposed provenance header:** `// port-lint: source symbols/pixel.rs` (current: `// port-lint: source ratatui-core/src/symbols/pixel.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../ratatui-core/src rust ../../src/commonMain/kotlin/ratatui kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

