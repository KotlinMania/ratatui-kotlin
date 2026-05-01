# High Priority Ports - Action Plan

## Files by Impact

Priority = deps * 1,000,000 + SymDeficit * 10,000 + SrcSymbols * 100 + (1 - function similarity) * 10

Dependency fanout is ranked first so the ladder favors ports that clear downstream compilation failures fastest.

This list is complete and includes function/type detail for every matched file. Function similarity is the required body/parameter comparison; file-level shape does not rescue a port.

| Rank | Source | Target | Function similarity | Deps | Functions | Missing functions | Types | Missing types | SymDeficit | SrcSymbols | Priority |
|------|--------|--------|------------|------|-----------|-------------------|-------|---------------|-----------|------------|----------|
| 1 | `buffer` | `buffer.BufferTest [STUB] [PROVENANCE-FALLBACK]` | 0.00 | 12 | 0/0 matched (target 44) | _none_ | 0/0 matched (target 1) | _none_ | 0 | 0 | 12000010.0 |
| 2 | `layout.rect` | `layout.Rect` | 0.29 | 9 | 23/47 matched (target 33) | `fmt`, `centered_horizontally`, `centered_vertically`, `centered`, `layout`, `layout_vec`, `try_layout`, `to_string`, `negative_offset`, `negative_offset_saturate`, `offset_saturate_max`, `intersection_underflow`, `mutual_intersect`, `size_truncation`, `size_preservation`, `resize_updates_size`, `resize_clamps_at_bounds`, `can_be_const`, `split`, `split_invalid_number_of_recs`, `from_position_and_size`, `from_size`, `layout_invalid_number_of_rects`, `try_layout_invalid_number_of_rects` | 1/1 matched (target 3) | _none_ | 24 | 48 | 9244807.0 |
| 3 | `backend` | `backend.Backend [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 6 | 3/5 matched (target 4) | `clear_type_tostring`, `clear_type_from_str` | 3/3 matched | _none_ | 2 | 8 | 6020810.0 |
| 4 | `widgets.widget` | `widgets.Widget [PROVENANCE-FALLBACK]` | 0.72 | 6 | 7/8 matched (target 11) | `buf` | 2/2 matched (target 3) | _none_ | 1 | 10 | 6011003.0 |
| 5 | `rect.iter` | `rect.Iter` | 0.74 | 5 | 14/16 matched (target 21) | `columns_max`, `columns_min` | 3/4 matched | `Item` | 3 | 20 | 5032002.5 |
| 6 | `layout.position` | `layout.Position [PROVENANCE-FALLBACK]` | 0.51 | 4 | 10/16 matched (target 21) | `fmt`, `add`, `sub`, `add_assign`, `sub_assign`, `to_string` | 1/2 matched | `Output` | 7 | 18 | 4071805.0 |
| 7 | `style.color` | `style.Color [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 4 | 16/21 matched (target 40) | `fmt`, `test_hsl_to_rgb`, `test_hsluv_to_rgb`, `deserialize_error`, `test_from_array_and_tuple_conversions` | 4/5 matched (target 30) | `Err` | 6 | 26 | 4062610.0 |
| 8 | `text.line` | `text.Line [PROVENANCE-FALLBACK]` | 0.17 | 3 | 20/80 matched (target 32) | `fmt`, `cow_to_spans`, `iter`, `iter_mut`, `width_cjk`, `into_iter`, `add`, `add_assign`, `extend`, `spans_after_width`, `small_buf`, `raw_str`, `styled_str`, `styled_string`, `styled_cow`, `spans_vec`, `spans_iter`, `stylize`, `from_string`, `from_str`, `from_vec`, `from_slice_of_spans`, `from_slice_of_strs`, `from_slice_of_strings`, `collect`, `from_span`, `add_span`, `add_line`, `add_assign_span`, `into_string`, `display_line_from_vec`, `display_styled_line`, `display_line_from_styled_span`, `hello_world`, `render_out_of_bounds`, `render_only_styles_line_area`, `render_only_styles_first_line`, `render_truncates`, `render_centered`, `render_right_aligned`, `render_truncates_left`, `render_truncates_right`, `render_truncates_center`, `regression_1032`, `crab_emoji_width`, `render_truncates_emoji`, `render_truncates_emoji_center`, `render_truncates_away_from_0x0`, `render_right_aligned_multi_span`, `flag_emoji`, `render_truncates_flag`, `render_truncates_very_long_line_of_many_spans`, `render_truncates_very_long_single_span_line`, `render_with_newlines`, `into_iter_ref`, `into_iter_mut_ref`, `for_loop_ref`, `for_loop_mut_ref`, `for_loop_into`, `debug` | 2/5 matched (target 2) | `Item`, `IntoIter`, `Output` | 63 | 85 | 3638508.2 |
| 9 | `style.stylize` | `style.Stylize [PROVENANCE-FALLBACK]` | 0.31 | 3 | 7/20 matched (target 391) | `fmt`, `str_styled`, `string_styled`, `cow_string_styled`, `temporary_string_styled`, `other_primitives_styled`, `color_modifier`, `fg_bg`, `repeated_attributes`, `all_chained`, `stylize_debug_foreground`, `stylize_debug_background`, `stylize_debug_underline` | 4/5 matched (target 7) | `Item` | 14 | 25 | 3142507.0 |
| 10 | `style` | `style.Style [PROVENANCE-FALLBACK]` | 0.63 | 3 | 25/33 matched (target 102) | `fmt`, `deserialize_modifier`, `fmt_stylize`, `combine_individual_modifiers`, `style_can_be_const`, `serialize_then_deserialize`, `deserialize_defaults`, `deserialize_null_modifiers` | 1/1 matched (target 3) | _none_ | 8 | 34 | 3083403.8 |
| 11 | `buffer.cell` | `buffer.Cell [PROVENANCE-FALLBACK]` | 0.79 | 3 | 19/21 matched (target 50) | `eq`, `hash` | 2/2 matched (target 7) | _none_ | 2 | 23 | 3022302.0 |
| 12 | `layout.size` | `layout.Size [PROVENANCE-FALLBACK]` | 0.24 | 2 | 3/8 matched (target 6) | `fmt`, `from_tuple`, `to_tuple`, `from_rect`, `display` | 1/1 matched | _none_ | 5 | 9 | 2050907.6 |
| 13 | `layout.constraint` | `layout.Constraint [PROVENANCE-FALLBACK]` | 0.73 | 2 | 10/12 matched (target 32) | `fmt`, `to_string` | 1/1 matched (target 8) | _none_ | 2 | 13 | 2021302.8 |
| 14 | `terminal` | `terminal.Terminal [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 2 | 0/1 matched (target 38) | `drop` | 2/2 matched (target 3) | _none_ | 1 | 3 | 2010310.0 |
| 15 | `buffer.cell_width` | `buffer.CellWidth [PROVENANCE-FALLBACK]` | 0.53 | 2 | 4/4 matched (target 8) | _none_ | 1/1 matched (target 2) | _none_ | 0 | 5 | 2000504.8 |
| 16 | `text.span` | `text.Span [PROVENANCE-FALLBACK]` | 0.24 | 1 | 15/52 matched (target 27) | `fmt`, `to_left_aligned_line`, `to_centered_line`, `to_right_aligned_line`, `width_cjk`, `add`, `small_buf`, `default`, `raw_str`, `raw_string`, `styled_str`, `styled_string`, `set_content`, `from_ref_str_borrowed_cow`, `from_string_ref_str_borrowed_cow`, `from_string_owned_cow`, `from_ref_string_borrowed_cow`, `stylize`, `display_span`, `display_newline_span`, `display_styled_span`, `left_aligned`, `centered`, `right_aligned`, `render_out_of_bounds`, `render_truncates_too_long_content`, `render_patches_existing_style`, `render_multi_width_symbol`, `render_multi_width_symbol_truncates_entire_symbol`, `render_overflowing_area_truncates`, `render_first_zero_width`, `render_second_zero_width`, `render_middle_zero_width`, `render_last_zero_width`, `render_with_newlines`, `issue_1160`, `debug` | 2/4 matched (target 3) | `Output`, `Item` | 39 | 56 | 1395607.6 |
| 17 | `text.masked` | `text.Masked [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 1 | 4/9 matched (target 12) | `fmt`, `from`, `debug`, `display`, `into_cow` | 1/1 matched (target 2) | _none_ | 5 | 10 | 1051010.0 |
| 18 | `layout.margin` | `layout.Margin [PROVENANCE-FALLBACK]` | 0.15 | 1 | 1/6 matched (target 3) | `from`, `fmt`, `margin_to_string`, `margin_new`, `from_u16` | 1/1 matched | _none_ | 5 | 7 | 1050708.5 |
| 19 | `widgets.stateful_widget` | `widgets.StatefulWidget [PROVENANCE-FALLBACK]` | 0.75 | 1 | 2/4 matched (target 6) | `buf`, `state` | 3/4 matched | `State` | 3 | 8 | 1030802.5 |
| 20 | `layout.direction` | `layout.Direction [PROVENANCE-FALLBACK]` | 0.17 | 1 | 1/4 matched (target 2) | `direction_to_string`, `direction_from_str`, `other` | 1/1 matched | _none_ | 3 | 5 | 1030508.3 |
| 21 | `layout.alignment` | `layout.Alignment [PROVENANCE-FALLBACK]` | 0.37 | 1 | 2/4 matched (target 7) | `alignment_from_str`, `vertical_alignment_from_str` | 3/3 matched (target 4) | _none_ | 2 | 7 | 1020706.2 |
| 22 | `terminal.viewport` | `terminal.Viewport [PROVENANCE-FALLBACK]` | 0.00 | 1 | 0/2 matched (target 7) | `fmt`, `viewport_to_string` | 1/1 matched (target 4) | _none_ | 2 | 3 | 1020310.0 |
| 23 | `layout.offset` | `layout.Offset [PROVENANCE-FALLBACK]` | 0.73 | 1 | 5/5 matched | _none_ | 1/1 matched (target 2) | _none_ | 0 | 6 | 1000602.8 |
| 24 | `layout.flex` | `layout.Flex [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 1 | 0/0 matched (target 7) | _none_ | 1/1 matched | _none_ | 0 | 1 | 1000110.0 |
| 25 | `text.text` | `text.Text [PROVENANCE-FALLBACK]` | 0.19 | 0 | 19/70 matched (target 33) | `fmt`, `iter`, `iter_mut`, `width_cjk`, `into_iter`, `add`, `add_assign`, `small_buf`, `from_string`, `from_str`, `from_cow`, `from_span`, `from_line`, `from_slice_of_spans`, `from_slice_of_lines`, `from_slice_of_strs`, `from_slice_of_strings`, `from_vec_line`, `from_iterator`, `collect`, `add_line`, `add_text`, `add_assign_text`, `add_assign_line`, `extend_from_iter`, `extend_from_iter_str`, `display_raw_text`, `display_styled_text`, `display_text_from_vec`, `display_extended_text`, `stylize`, `push_line_empty`, `push_span_empty`, `render_out_of_bounds`, `render_right_aligned`, `render_centered_odd`, `render_centered_even`, `render_right_aligned_with_truncation`, `render_centered_odd_with_truncation`, `render_centered_even_with_truncation`, `render_one_line_right`, `render_only_styles_line_area`, `render_truncates`, `hello_world`, `into_iter_ref`, `into_iter_mut_ref`, `for_loop_ref`, `for_loop_mut_ref`, `for_loop_into`, `debug`, `debug_alternate` | 2/5 matched (target 2) | `Item`, `IntoIter`, `Output` | 54 | 75 | 547508.1 |
| 26 | `layout.layout` | `layout.Layout [PROVENANCE-FALLBACK]` | 0.26 | 0 | 29/75 matched (target 41) | `init_cache`, `resize_cache`, `areas`, `try_areas`, `spacers`, `cached_split`, `split_layout`, `round`, `debug_elements`, `strength_is_valid`, `cache_size`, `margins`, `letters`, `length`, `max`, `min`, `percentage`, `percentage_start`, `percentage_spacebetween`, `ratio`, `ratio_start`, `ratio_spacebetween`, `vertical_split_by_height`, `edge_cases`, `constraint_length`, `table_length`, `length_is_higher_priority`, `length_is_higher_priority_in_flex`, `fixed_with_50_width`, `fill`, `percentage_parameterized`, `min_max`, `flex_constraint`, `flex_overlap`, `flex_spacing`, `constraint_specification_tests_for_priority`, `constraint_specification_tests_for_priority_with_spacing`, `fill_vs_flex`, `fill_spacing`, `fill_overlap`, `flex_spacing_lower_priority_than_user_spacing`, `split_with_spacers_no_spacing`, `split_with_spacers_and_spacing`, `split_with_spacers_and_overlap`, `split_with_spacers_and_too_much_spacing`, `legacy_vs_default` | 3/7 matched (target 6) | `Rects`, `Segments`, `Spacers`, `Cache` | 50 | 82 | 508207.4 |
| 27 | `buffer.buffer` | `buffer.Buffer [PROVENANCE-FALLBACK]` | 0.25 | 0 | 22/63 matched (target 35) | `index`, `index_mut`, `fmt`, `debug_empty_buffer`, `debug_grapheme_override`, `debug_some_example`, `it_translates_to_and_from_coordinates`, `pos_of_panics_on_out_of_bounds`, `index_of_panics_on_out_of_bounds`, `test_cell`, `test_cell_mut`, `index_out_of_bounds_panics`, `index_mut_out_of_bounds_panics`, `set_string_multi_width_overwrite`, `set_string_zero_width`, `set_string_double_width`, `small_one_line_buffer`, `set_line_raw`, `set_line_styled`, `set_style_does_not_panic_when_out_of_area`, `diff_empty_empty`, `diff_empty_filled`, `diff_filled_filled`, `diff_single_width`, `diff_multi_width`, `diff_multi_width_offset`, `merge_diff_idempotent`, `merge_diff_forcedwidth`, `merge_diff_link`, `merge_diff_split_link`, `merge_diff_image_sequences`, `diff_skip`, `merge_with_offset`, `merge_skip`, `with_lines_accepts_into_lines`, `control_sequence_rendered_full`, `control_sequence_rendered_partially`, `renders_emoji`, `index_pos_of_u16_max`, `diff_clears_trailing_cell_for_wide_grapheme`, `diff_ignores_style_only_changes_in_trailing_cells` | 1/2 matched (target 3) | `Output` | 42 | 65 | 426507.4 |
| 28 | `backend.test` | `backend.TestBackend [PROVENANCE-FALLBACK]` | 0.32 | 0 | 25/47 matched (target 29) | `assert_cursor_position`, `fmt`, `scroll_region_up`, `scroll_region_down`, `test_buffer_view`, `buffer_view_with_overwrites`, `assert_buffer_panics`, `assert_scrollback_panics`, `display`, `clear_region_all`, `clear_region_after_cursor`, `clear_region_before_cursor`, `clear_region_current_line`, `clear_region_until_new_line`, `append_lines_not_at_last_line`, `append_lines_at_last_line`, `append_multiple_lines_not_at_last_line`, `append_multiple_lines_past_last_line`, `append_multiple_lines_where_cursor_at_end_appends_height_lines`, `append_multiple_lines_where_cursor_appends_height_lines`, `append_multiple_lines_where_cursor_at_end_appends_more_than_height_lines`, `append_lines_truncates_beyond_u16_max` | 1/3 matched (target 1) | `Result`, `Error` | 24 | 50 | 245006.8 |
| 29 | `symbols.border` | `border.Border [PROVENANCE-FALLBACK]` | 0.08 | 0 | 2/22 matched (target 2) | `render`, `border_set_from_line_set`, `plain`, `rounded`, `double`, `thick`, `light_double_dashed`, `heavy_double_dashed`, `light_triple_dashed`, `heavy_triple_dashed`, `light_quadruple_dashed`, `heavy_quadruple_dashed`, `quadrant_outside`, `quadrant_inside`, `one_eighth_wide`, `one_eighth_tall`, `proportional_wide`, `proportional_tall`, `full`, `empty` | 1/1 matched (target 3) | _none_ | 20 | 23 | 202309.2 |
| 30 | `rect.ops` | `rect.Ops [PROVENANCE-FALLBACK]` | 0.00 | 0 | 0/9 matched (target 10) | `neg`, `add`, `sub`, `add_assign`, `sub_assign`, `add_offset`, `sub_offset`, `add_assign_offset`, `sub_assign_offset` | 0/1 matched (target 0) | `Output` | 10 | 10 | 101010.0 |
| 31 | `terminal.cursor` | `terminal.CursorTest [PROVENANCE-FALLBACK]` | 0.39 | 0 | 5/11 matched (target 5) | `hide_cursor`, `show_cursor`, `get_cursor`, `set_cursor`, `get_cursor_position`, `set_cursor_position` | 0/0 matched (target 1) | _none_ | 6 | 11 | 61106.1 |
| 32 | `terminal.backend` | `backend.ClearTypeTest [PROVENANCE-FALLBACK]` | 0.00 | 0 | 0/6 matched (target 2) | `backend`, `backend_mut`, `size`, `backend_returns_shared_reference`, `backend_mut_allows_mutating_backend_state`, `size_queries_underlying_backend_size` | 0/0 matched (target 1) | _none_ | 6 | 6 | 60610.0 |
| 33 | `symbols.merge` | `merge.Merge [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 6/10 matched (target 11) | `new`, `replace_merge_strategy`, `exact_merge_strategy`, `fuzzy_merge_strategy` | 3/4 matched (target 3) | `BorderSymbolError` | 5 | 14 | 51410.0 |
| 34 | `symbols.line` | `line.Line [PROVENANCE-FALLBACK]` | 0.14 | 0 | 1/6 matched (target 3) | `render`, `normal`, `rounded`, `double`, `thick` | 1/1 matched (target 3) | _none_ | 5 | 7 | 50708.6 |
| 35 | `style.anstyle` | `style.Anstyle [PROVENANCE-FALLBACK]` | 0.43 | 0 | 14/16 matched (target 26) | `from`, `try_from` | 1/2 matched (target 3) | `Error` | 3 | 18 | 31805.7 |
| 36 | `buffer.assert` | `buffer.Assert [PROVENANCE-FALLBACK]` | 0.00 | 0 | 0/3 matched (target 4) | `assert_buffer_eq_does_not_panic_on_equal_buffers`, `assert_buffer_eq_panics_on_unequal_area`, `assert_buffer_eq_panics_on_unequal_style` | 0/0 matched (target 1) | _none_ | 3 | 3 | 30310.0 |
| 37 | `text.grapheme` | `text.Grapheme [PROVENANCE-FALLBACK]` | 0.93 | 0 | 5/5 matched (target 8) | _none_ | 1/2 matched | `Item` | 1 | 7 | 10700.7 |
| 38 | `buffer.diff` | `buffer.BufferDiff [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 14/14 matched (target 17) | _none_ | 3/3 matched (target 4) | _none_ | 0 | 17 | 1710.0 |
| 39 | `terminal.frame` | `terminal.Frame [PROVENANCE-FALLBACK]` | 0.94 | 0 | 8/8 matched (target 10) | _none_ | 2/2 matched | _none_ | 0 | 10 | 1000.6 |
| 40 | `symbols.marker` | `symbols.Marker [PROVENANCE-FALLBACK]` | 0.68 | 0 | 2/2 matched (target 6) | _none_ | 1/1 matched (target 11) | _none_ | 0 | 3 | 303.2 |
| 41 | `palette.material` | `material.Material [PROVENANCE-FALLBACK]` | 0.91 | 0 | 1/1 matched (target 2) | _none_ | 2/2 matched (target 3) | _none_ | 0 | 3 | 300.9 |
| 42 | `symbols.block` | `block.Block [PROVENANCE-FALLBACK]` | 0.99 | 0 | 1/1 matched (target 43) | _none_ | 1/1 matched (target 4) | _none_ | 0 | 2 | 200.1 |
| 43 | `symbols.bar` | `bar.Bar [PROVENANCE-FALLBACK]` | 0.99 | 0 | 1/1 matched (target 16) | _none_ | 1/1 matched (target 2) | _none_ | 0 | 2 | 200.1 |
| 44 | `symbols.scrollbar` | `scrollbar.Scrollbar [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 0/0 matched (target 38) | _none_ | 1/1 matched (target 5) | _none_ | 0 | 1 | 110.0 |
| 45 | `palette.tailwind` | `tailwind.Tailwind [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 0/0 matched | _none_ | 1/1 matched | _none_ | 0 | 1 | 110.0 |
| 46 | `symbols.half_block` | `symbols.HalfBlock [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 0/0 matched | _none_ | 0/0 matched (target 1) | _none_ | 0 | 0 | 10.0 |
| 47 | `symbols.shade` | `shade.Shade [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 0/0 matched | _none_ | 0/0 matched (target 1) | _none_ | 0 | 0 | 10.0 |
| 48 | `symbols.braille` | `symbols.Braille [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 0/0 matched | _none_ | 0/0 matched (target 1) | _none_ | 0 | 0 | 10.0 |
| 49 | `symbols.pixel` | `symbols.Pixel [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 0/0 matched | _none_ | 0/0 matched (target 1) | _none_ | 0 | 0 | 10.0 |

## Cheat Detection / Scoring Failures

- `buffer` -> `buffer.BufferTest [STUB] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. target contains TODO/stub/placeholder markers in function bodies; no source functions found; report scoring is function-by-function only
- `backend` -> `backend.Backend [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Backend.kt: score-padding suppression annotation `@Suppress` in Kotlin code
- `style.color` -> `style.Color [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Color.kt: Rust `match` expression in Kotlin comments; Color.kt: score-padding suppression annotation `@Suppress` in Kotlin code
- `terminal` -> `terminal.Terminal [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Terminal.kt: Rust `match` expression in Kotlin comments
- `text.masked` -> `text.Masked [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Masked.kt: Rust lifetime explanation in Kotlin comments
- `layout.flex` -> `layout.Flex [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.merge` -> `merge.Merge [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Merge.kt: Rust-only type/unsafe terminology in Kotlin comments
- `buffer.diff` -> `buffer.BufferDiff [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. BufferDiff.kt: Rust lifetime explanation in Kotlin comments
- `symbols.scrollbar` -> `scrollbar.Scrollbar [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Scrollbar.kt: Rust-only type/unsafe terminology in Kotlin comments; no source functions found; report scoring is function-by-function only
- `palette.tailwind` -> `tailwind.Tailwind [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.half_block` -> `symbols.HalfBlock [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.shade` -> `shade.Shade [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.braille` -> `symbols.Braille [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.pixel` -> `symbols.Pixel [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only

## Critical Issues (Function Similarity < 0.60 with Dependencies)

These files need immediate attention:

- **buffer** → `buffer.BufferTest [STUB] [PROVENANCE-FALLBACK]`
  - Function similarity: 0.00
  - Dependencies: 12
  - Functions: 0/0 matched (target 44)
  - Missing functions: _none_
  - Types: 0/0 matched (target 1)
  - Missing types: _none_
  - Scoring failure: target contains TODO/stub/placeholder markers in function bodies; no source functions found; report scoring is function-by-function only
  - Lint issues: 1

- **layout.rect** → `layout.Rect`
  - Function similarity: 0.29
  - Dependencies: 9
  - Functions: 23/47 matched (target 33)
  - Missing functions: `fmt`, `centered_horizontally`, `centered_vertically`, `centered`, `layout`, `layout_vec`, `try_layout`, `to_string`, `negative_offset`, `negative_offset_saturate`, `offset_saturate_max`, `intersection_underflow`, `mutual_intersect`, `size_truncation`, `size_preservation`, `resize_updates_size`, `resize_clamps_at_bounds`, `can_be_const`, `split`, `split_invalid_number_of_recs`, `from_position_and_size`, `from_size`, `layout_invalid_number_of_rects`, `try_layout_invalid_number_of_rects`
  - Types: 1/1 matched (target 3)
  - Missing types: _none_

- **backend** → `backend.Backend [ZERO] [PROVENANCE-FALLBACK]`
  - Function similarity: 0.00
  - Dependencies: 6
  - Functions: 3/5 matched (target 4)
  - Missing functions: `clear_type_tostring`, `clear_type_from_str`
  - Types: 3/3 matched
  - Missing types: _none_
  - Scoring failure: Backend.kt: score-padding suppression annotation `@Suppress` in Kotlin code
  - Lint issues: 1

- **layout.position** → `layout.Position [PROVENANCE-FALLBACK]`
  - Function similarity: 0.51
  - Dependencies: 4
  - Functions: 10/16 matched (target 21)
  - Missing functions: `fmt`, `add`, `sub`, `add_assign`, `sub_assign`, `to_string`
  - Types: 1/2 matched
  - Missing types: `Output`
  - Lint issues: 2

- **style.color** → `style.Color [ZERO] [PROVENANCE-FALLBACK]`
  - Function similarity: 0.00
  - Dependencies: 4
  - Functions: 16/21 matched (target 40)
  - Missing functions: `fmt`, `test_hsl_to_rgb`, `test_hsluv_to_rgb`, `deserialize_error`, `test_from_array_and_tuple_conversions`
  - Types: 4/5 matched (target 30)
  - Missing types: `Err`
  - Scoring failure: Color.kt: Rust `match` expression in Kotlin comments; Color.kt: score-padding suppression annotation `@Suppress` in Kotlin code
  - Lint issues: 2

- **text.line** → `text.Line [PROVENANCE-FALLBACK]`
  - Function similarity: 0.17
  - Dependencies: 3
  - Functions: 20/80 matched (target 32)
  - Missing functions: `fmt`, `cow_to_spans`, `iter`, `iter_mut`, `width_cjk`, `into_iter`, `add`, `add_assign`, `extend`, `spans_after_width`, `small_buf`, `raw_str`, `styled_str`, `styled_string`, `styled_cow`, `spans_vec`, `spans_iter`, `stylize`, `from_string`, `from_str`, `from_vec`, `from_slice_of_spans`, `from_slice_of_strs`, `from_slice_of_strings`, `collect`, `from_span`, `add_span`, `add_line`, `add_assign_span`, `into_string`, `display_line_from_vec`, `display_styled_line`, `display_line_from_styled_span`, `hello_world`, `render_out_of_bounds`, `render_only_styles_line_area`, `render_only_styles_first_line`, `render_truncates`, `render_centered`, `render_right_aligned`, `render_truncates_left`, `render_truncates_right`, `render_truncates_center`, `regression_1032`, `crab_emoji_width`, `render_truncates_emoji`, `render_truncates_emoji_center`, `render_truncates_away_from_0x0`, `render_right_aligned_multi_span`, `flag_emoji`, `render_truncates_flag`, `render_truncates_very_long_line_of_many_spans`, `render_truncates_very_long_single_span_line`, `render_with_newlines`, `into_iter_ref`, `into_iter_mut_ref`, `for_loop_ref`, `for_loop_mut_ref`, `for_loop_into`, `debug`
  - Types: 2/5 matched (target 2)
  - Missing types: `Item`, `IntoIter`, `Output`
  - Lint issues: 1

- **style.stylize** → `style.Stylize [PROVENANCE-FALLBACK]`
  - Function similarity: 0.31
  - Dependencies: 3
  - Functions: 7/20 matched (target 391)
  - Missing functions: `fmt`, `str_styled`, `string_styled`, `cow_string_styled`, `temporary_string_styled`, `other_primitives_styled`, `color_modifier`, `fg_bg`, `repeated_attributes`, `all_chained`, `stylize_debug_foreground`, `stylize_debug_background`, `stylize_debug_underline`
  - Types: 4/5 matched (target 7)
  - Missing types: `Item`
  - Lint issues: 2

- **layout.size** → `layout.Size [PROVENANCE-FALLBACK]`
  - Function similarity: 0.24
  - Dependencies: 2
  - Functions: 3/8 matched (target 6)
  - Missing functions: `fmt`, `from_tuple`, `to_tuple`, `from_rect`, `display`
  - Types: 1/1 matched
  - Missing types: _none_
  - Lint issues: 1

- **terminal** → `terminal.Terminal [ZERO] [PROVENANCE-FALLBACK]`
  - Function similarity: 0.00
  - Dependencies: 2
  - Functions: 0/1 matched (target 38)
  - Missing functions: `drop`
  - Types: 2/2 matched (target 3)
  - Missing types: _none_
  - Scoring failure: Terminal.kt: Rust `match` expression in Kotlin comments
  - Lint issues: 2

- **buffer.cell_width** → `buffer.CellWidth [PROVENANCE-FALLBACK]`
  - Function similarity: 0.53
  - Dependencies: 2
  - Functions: 4/4 matched (target 8)
  - Missing functions: _none_
  - Types: 1/1 matched (target 2)
  - Missing types: _none_
  - Lint issues: 2

- **text.span** → `text.Span [PROVENANCE-FALLBACK]`
  - Function similarity: 0.24
  - Dependencies: 1
  - Functions: 15/52 matched (target 27)
  - Missing functions: `fmt`, `to_left_aligned_line`, `to_centered_line`, `to_right_aligned_line`, `width_cjk`, `add`, `small_buf`, `default`, `raw_str`, `raw_string`, `styled_str`, `styled_string`, `set_content`, `from_ref_str_borrowed_cow`, `from_string_ref_str_borrowed_cow`, `from_string_owned_cow`, `from_ref_string_borrowed_cow`, `stylize`, `display_span`, `display_newline_span`, `display_styled_span`, `left_aligned`, `centered`, `right_aligned`, `render_out_of_bounds`, `render_truncates_too_long_content`, `render_patches_existing_style`, `render_multi_width_symbol`, `render_multi_width_symbol_truncates_entire_symbol`, `render_overflowing_area_truncates`, `render_first_zero_width`, `render_second_zero_width`, `render_middle_zero_width`, `render_last_zero_width`, `render_with_newlines`, `issue_1160`, `debug`
  - Types: 2/4 matched (target 3)
  - Missing types: `Output`, `Item`
  - Lint issues: 1

- **text.masked** → `text.Masked [ZERO] [PROVENANCE-FALLBACK]`
  - Function similarity: 0.00
  - Dependencies: 1
  - Functions: 4/9 matched (target 12)
  - Missing functions: `fmt`, `from`, `debug`, `display`, `into_cow`
  - Types: 1/1 matched (target 2)
  - Missing types: _none_
  - Scoring failure: Masked.kt: Rust lifetime explanation in Kotlin comments
  - Lint issues: 2

- **layout.margin** → `layout.Margin [PROVENANCE-FALLBACK]`
  - Function similarity: 0.15
  - Dependencies: 1
  - Functions: 1/6 matched (target 3)
  - Missing functions: `from`, `fmt`, `margin_to_string`, `margin_new`, `from_u16`
  - Types: 1/1 matched
  - Missing types: _none_
  - Lint issues: 1

- **layout.direction** → `layout.Direction [PROVENANCE-FALLBACK]`
  - Function similarity: 0.17
  - Dependencies: 1
  - Functions: 1/4 matched (target 2)
  - Missing functions: `direction_to_string`, `direction_from_str`, `other`
  - Types: 1/1 matched
  - Missing types: _none_
  - Lint issues: 1

- **layout.alignment** → `layout.Alignment [PROVENANCE-FALLBACK]`
  - Function similarity: 0.37
  - Dependencies: 1
  - Functions: 2/4 matched (target 7)
  - Missing functions: `alignment_from_str`, `vertical_alignment_from_str`
  - Types: 3/3 matched (target 4)
  - Missing types: _none_
  - Lint issues: 2

- **terminal.viewport** → `terminal.Viewport [PROVENANCE-FALLBACK]`
  - Function similarity: 0.00
  - Dependencies: 1
  - Functions: 0/2 matched (target 7)
  - Missing functions: `fmt`, `viewport_to_string`
  - Types: 1/1 matched (target 4)
  - Missing types: _none_
  - Lint issues: 1

- **layout.flex** → `layout.Flex [ZERO] [PROVENANCE-FALLBACK]`
  - Function similarity: 0.00
  - Dependencies: 1
  - Functions: 0/0 matched (target 7)
  - Missing functions: _none_
  - Types: 1/1 matched
  - Missing types: _none_
  - Scoring failure: no source functions found; report scoring is function-by-function only
  - Lint issues: 1

## Missing Files (by Dependents)

| Rank | Source file | Expected target | Deps | Functions | Classes/types | Symbols | Source path | Expected path |
|------|-------------|-----------------|------|-----------|---------------|---------|-------------|---------------|
| 1 | `text` | `text.Text` | 2 | 0 | 0 | 0 | `text.rs` | `text/Text.kt` |
| 2 | `layout` | `layout.Layout` | 0 | 0 | 0 | 0 | `layout.rs` | `layout/Layout.kt` |
| 3 | `style.palette` | `style.palette.Palette` | 0 | 0 | 0 | 0 | `style/palette.rs` | `style/palette/Palette.kt` |
| 4 | `style.palette_conversion` | `style.PaletteConversion` | 0 | 4 | 0 | 4 | `style/palette_conversion.rs` | `style/PaletteConversion.kt` |
| 5 | `symbols` | `symbols.Symbols` | 0 | 0 | 0 | 0 | `symbols.rs` | `symbols/Symbols.kt` |
| 6 | `terminal.buffers` | `terminal.Buffers` | 0 | 17 | 0 | 17 | `terminal/buffers.rs` | `terminal/Buffers.kt` |
| 7 | `terminal.init` | `terminal.Init` | 0 | 7 | 0 | 7 | `terminal/init.rs` | `terminal/Init.kt` |
| 8 | `terminal.inline` | `terminal.Inline` | 0 | 16 | 0 | 16 | `terminal/inline.rs` | `terminal/Inline.kt` |
| 9 | `terminal.render` | `terminal.Render` | 0 | 26 | 3 | 29 | `terminal/render.rs` | `terminal/Render.kt` |
| 10 | `terminal.resize` | `terminal.Resize` | 0 | 11 | 0 | 11 | `terminal/resize.rs` | `terminal/Resize.kt` |
| 11 | `widgets` | `widgets.Widgets` | 0 | 0 | 0 | 0 | `widgets.rs` | `widgets/Widgets.kt` |

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

