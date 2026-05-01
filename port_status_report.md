# Code Port - Progress Report

**Generated:** 2026-05-01
**Source:** ratatui-core/src
**Target:** src/commonMain/kotlin/ratatui

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Function parity | 368/829 matched (target 1299) | 44.4% |
| Class/type parity | 68/95 matched (target 167) | 71.6% |
| Combined symbol parity | 436/924 matched (target 1466) | 47.2% |
| Average function body similarity | 0.32 | required score |
| Missing source functions | 81 | 0% parity until ported |
| Missing source classes/types | 3 | 0% parity until ported |
| Missing source symbol files | 6 | 84 symbols |
| Cheat/scoring failures | 15 | forced to 0% |
| Total source files | 61 | 100% |
| Target units (paired) | 128 | - |
| Target files (total) | 128 | - |
| Porting progress | 49 | 80.3% (matched) |
| Missing files | 11 | 18.0% |
| Reexport/wiring files | 1 | consult-only |

## Port Quality Analysis

**Average Function Similarity:** 0.32

Similarity in this report is the required function-by-function body/parameter score. Class/type parity and symbol deficits are reported beside it; whole-file shape is diagnostic only.

**Work Distribution:**
- Critical (<0.60): 36 files (73.5% of matched)
- Needs review (0.60-0.84): 8 files (16.3% of matched)

## Worst Function Scores First

Every matched file is listed from lowest function body/parameter similarity upward. Missing symbol names are not capped.

| Rank | Source | Target | Function similarity | Functions | Missing functions | Types | Missing types | Tests | Symbol deficit | Priority |
|------|--------|--------|---------------------|-----------|-------------------|-------|---------------|-------|----------------|----------|
| 1 | `buffer` | `buffer.BufferTest [STUB] [PROVENANCE-FALLBACK]` | 0.00 | 0/0 matched (target 44) | _none_ | 0/0 matched (target 1) | _none_ | - | 0 | 12000010.0 |
| 2 | `layout.rect` | `layout.Rect [ZERO]` | 0.00 | 45/47 matched (target 81) | `fmt`, `to_string` | 1/1 matched (target 4) | _none_ | 16/17 | 2 | 9024810.0 |
| 3 | `backend` | `backend.Backend [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 3/5 matched (target 4) | `clear_type_tostring`, `clear_type_from_str` | 3/3 matched | _none_ | 0/2 | 2 | 6020810.0 |
| 4 | `style.color` | `style.Color [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 16/21 matched (target 40) | `fmt`, `test_hsl_to_rgb`, `test_hsluv_to_rgb`, `deserialize_error`, `test_from_array_and_tuple_conversions` | 4/5 matched (target 30) | `Err` | 7/11 | 6 | 4062610.0 |
| 5 | `terminal` | `terminal.Terminal [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0/1 matched (target 38) | `drop` | 2/2 matched (target 3) | _none_ | - | 1 | 2010310.0 |
| 6 | `text.masked` | `text.Masked [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 4/9 matched (target 12) | `fmt`, `from`, `debug`, `display`, `into_cow` | 1/1 matched (target 2) | _none_ | 1/4 | 5 | 1051010.0 |
| 7 | `layout.flex` | `layout.Flex [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0/0 matched (target 7) | _none_ | 1/1 matched | _none_ | - | 0 | 1000110.0 |
| 8 | `symbols.merge` | `merge.Merge [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 6/10 matched (target 11) | `new`, `replace_merge_strategy`, `exact_merge_strategy`, `fuzzy_merge_strategy` | 3/4 matched (target 3) | `BorderSymbolError` | 0/3 | 5 | 51410.0 |
| 9 | `buffer.diff` | `buffer.BufferDiff [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 14/14 matched (target 17) | _none_ | 3/3 matched (target 4) | _none_ | 10/10 | 0 | 1710.0 |
| 10 | `palette.tailwind` | `tailwind.Tailwind [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0/0 matched | _none_ | 1/1 matched | _none_ | - | 0 | 110.0 |
| 11 | `symbols.braille` | `symbols.Braille [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0/0 matched | _none_ | 0/0 matched (target 1) | _none_ | - | 0 | 10.0 |
| 12 | `symbols.half_block` | `symbols.HalfBlock [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0/0 matched | _none_ | 0/0 matched (target 1) | _none_ | - | 0 | 10.0 |
| 13 | `symbols.pixel` | `symbols.Pixel [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0/0 matched | _none_ | 0/0 matched (target 1) | _none_ | - | 0 | 10.0 |
| 14 | `symbols.scrollbar` | `scrollbar.Scrollbar [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0/0 matched (target 38) | _none_ | 1/1 matched (target 5) | _none_ | - | 0 | 110.0 |
| 15 | `symbols.shade` | `shade.Shade [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0/0 matched | _none_ | 0/0 matched (target 1) | _none_ | - | 0 | 10.0 |
| 16 | `terminal.viewport` | `terminal.Viewport [PROVENANCE-FALLBACK]` | 0.00 | 0/2 matched (target 7) | `fmt`, `viewport_to_string` | 1/1 matched (target 4) | _none_ | 0/1 | 2 | 1020310.0 |
| 17 | `rect.ops` | `rect.Ops [PROVENANCE-FALLBACK]` | 0.00 | 0/9 matched (target 10) | `neg`, `add`, `sub`, `add_assign`, `sub_assign`, `add_offset`, `sub_offset`, `add_assign_offset`, `sub_assign_offset` | 0/1 matched (target 0) | `Output` | 0/4 | 10 | 101010.0 |
| 18 | `terminal.backend` | `backend.ClearTypeTest [PROVENANCE-FALLBACK]` | 0.00 | 0/6 matched (target 2) | `backend`, `backend_mut`, `size`, `backend_returns_shared_reference`, `backend_mut_allows_mutating_backend_state`, `size_queries_underlying_backend_size` | 0/0 matched (target 1) | _none_ | 0/3 | 6 | 60610.0 |
| 19 | `buffer.assert` | `buffer.Assert [PROVENANCE-FALLBACK]` | 0.00 | 0/3 matched (target 4) | `assert_buffer_eq_does_not_panic_on_equal_buffers`, `assert_buffer_eq_panics_on_unequal_area`, `assert_buffer_eq_panics_on_unequal_style` | 0/0 matched (target 1) | _none_ | 0/3 | 3 | 30310.0 |
| 20 | `symbols.border` | `border.Border [PROVENANCE-FALLBACK]` | 0.08 | 2/22 matched (target 2) | `render`, `border_set_from_line_set`, `plain`, `rounded`, `double`, `thick`, `light_double_dashed`, `heavy_double_dashed`, `light_triple_dashed`, `heavy_triple_dashed`, `light_quadruple_dashed`, `heavy_quadruple_dashed`, `quadrant_outside`, `quadrant_inside`, `one_eighth_wide`, `one_eighth_tall`, `proportional_wide`, `proportional_tall`, `full`, `empty` | 1/1 matched (target 3) | _none_ | 0/20 | 20 | 202309.2 |
| 21 | `symbols.line` | `line.Line [PROVENANCE-FALLBACK]` | 0.14 | 1/6 matched (target 3) | `render`, `normal`, `rounded`, `double`, `thick` | 1/1 matched (target 3) | _none_ | 0/5 | 5 | 50708.6 |
| 22 | `layout.margin` | `layout.Margin [PROVENANCE-FALLBACK]` | 0.15 | 1/6 matched (target 3) | `from`, `fmt`, `margin_to_string`, `margin_new`, `from_u16` | 1/1 matched | _none_ | 0/3 | 5 | 1050708.5 |
| 23 | `layout.direction` | `layout.Direction [PROVENANCE-FALLBACK]` | 0.17 | 1/4 matched (target 2) | `direction_to_string`, `direction_from_str`, `other` | 1/1 matched | _none_ | 0/3 | 3 | 1030508.3 |
| 24 | `text.line` | `text.Line [PROVENANCE-FALLBACK]` | 0.17 | 20/80 matched (target 32) | `fmt`, `cow_to_spans`, `iter`, `iter_mut`, `width_cjk`, `into_iter`, `add`, `add_assign`, `extend`, `spans_after_width`, `small_buf`, `raw_str`, `styled_str`, `styled_string`, `styled_cow`, `spans_vec`, `spans_iter`, `stylize`, `from_string`, `from_str`, `from_vec`, `from_slice_of_spans`, `from_slice_of_strs`, `from_slice_of_strings`, `collect`, `from_span`, `add_span`, `add_line`, `add_assign_span`, `into_string`, `display_line_from_vec`, `display_styled_line`, `display_line_from_styled_span`, `hello_world`, `render_out_of_bounds`, `render_only_styles_line_area`, `render_only_styles_first_line`, `render_truncates`, `render_centered`, `render_right_aligned`, `render_truncates_left`, `render_truncates_right`, `render_truncates_center`, `regression_1032`, `crab_emoji_width`, `render_truncates_emoji`, `render_truncates_emoji_center`, `render_truncates_away_from_0x0`, `render_right_aligned_multi_span`, `flag_emoji`, `render_truncates_flag`, `render_truncates_very_long_line_of_many_spans`, `render_truncates_very_long_single_span_line`, `render_with_newlines`, `into_iter_ref`, `into_iter_mut_ref`, `for_loop_ref`, `for_loop_mut_ref`, `for_loop_into`, `debug` | 2/5 matched (target 2) | `Item`, `IntoIter`, `Output` | 0/50 | 63 | 3638508.2 |
| 25 | `text.text` | `text.Text [PROVENANCE-FALLBACK]` | 0.19 | 19/70 matched (target 33) | `fmt`, `iter`, `iter_mut`, `width_cjk`, `into_iter`, `add`, `add_assign`, `small_buf`, `from_string`, `from_str`, `from_cow`, `from_span`, `from_line`, `from_slice_of_spans`, `from_slice_of_lines`, `from_slice_of_strs`, `from_slice_of_strings`, `from_vec_line`, `from_iterator`, `collect`, `add_line`, `add_text`, `add_assign_text`, `add_assign_line`, `extend_from_iter`, `extend_from_iter_str`, `display_raw_text`, `display_styled_text`, `display_text_from_vec`, `display_extended_text`, `stylize`, `push_line_empty`, `push_span_empty`, `render_out_of_bounds`, `render_right_aligned`, `render_centered_odd`, `render_centered_even`, `render_right_aligned_with_truncation`, `render_centered_odd_with_truncation`, `render_centered_even_with_truncation`, `render_one_line_right`, `render_only_styles_line_area`, `render_truncates`, `hello_world`, `into_iter_ref`, `into_iter_mut_ref`, `for_loop_ref`, `for_loop_mut_ref`, `for_loop_into`, `debug`, `debug_alternate` | 2/5 matched (target 2) | `Item`, `IntoIter`, `Output` | 0/44 | 54 | 547508.1 |
| 26 | `text.span` | `text.Span [PROVENANCE-FALLBACK]` | 0.24 | 15/52 matched (target 27) | `fmt`, `to_left_aligned_line`, `to_centered_line`, `to_right_aligned_line`, `width_cjk`, `add`, `small_buf`, `default`, `raw_str`, `raw_string`, `styled_str`, `styled_string`, `set_content`, `from_ref_str_borrowed_cow`, `from_string_ref_str_borrowed_cow`, `from_string_owned_cow`, `from_ref_string_borrowed_cow`, `stylize`, `display_span`, `display_newline_span`, `display_styled_span`, `left_aligned`, `centered`, `right_aligned`, `render_out_of_bounds`, `render_truncates_too_long_content`, `render_patches_existing_style`, `render_multi_width_symbol`, `render_multi_width_symbol_truncates_entire_symbol`, `render_overflowing_area_truncates`, `render_first_zero_width`, `render_second_zero_width`, `render_middle_zero_width`, `render_last_zero_width`, `render_with_newlines`, `issue_1160`, `debug` | 2/4 matched (target 3) | `Output`, `Item` | 0/31 | 39 | 1395607.6 |
| 27 | `layout.size` | `layout.Size [PROVENANCE-FALLBACK]` | 0.24 | 3/8 matched (target 6) | `fmt`, `from_tuple`, `to_tuple`, `from_rect`, `display` | 1/1 matched | _none_ | 0/4 | 5 | 2050907.6 |
| 28 | `buffer.buffer` | `buffer.Buffer [PROVENANCE-FALLBACK]` | 0.25 | 22/63 matched (target 35) | `index`, `index_mut`, `fmt`, `debug_empty_buffer`, `debug_grapheme_override`, `debug_some_example`, `it_translates_to_and_from_coordinates`, `pos_of_panics_on_out_of_bounds`, `index_of_panics_on_out_of_bounds`, `test_cell`, `test_cell_mut`, `index_out_of_bounds_panics`, `index_mut_out_of_bounds_panics`, `set_string_multi_width_overwrite`, `set_string_zero_width`, `set_string_double_width`, `small_one_line_buffer`, `set_line_raw`, `set_line_styled`, `set_style_does_not_panic_when_out_of_area`, `diff_empty_empty`, `diff_empty_filled`, `diff_filled_filled`, `diff_single_width`, `diff_multi_width`, `diff_multi_width_offset`, `merge_diff_idempotent`, `merge_diff_forcedwidth`, `merge_diff_link`, `merge_diff_split_link`, `merge_diff_image_sequences`, `diff_skip`, `merge_with_offset`, `merge_skip`, `with_lines_accepts_into_lines`, `control_sequence_rendered_full`, `control_sequence_rendered_partially`, `renders_emoji`, `index_pos_of_u16_max`, `diff_clears_trailing_cell_for_wide_grapheme`, `diff_ignores_style_only_changes_in_trailing_cells` | 1/2 matched (target 3) | `Output` | 0/38 | 42 | 426507.4 |
| 29 | `layout.layout` | `layout.Layout [PROVENANCE-FALLBACK]` | 0.26 | 29/75 matched (target 41) | `init_cache`, `resize_cache`, `areas`, `try_areas`, `spacers`, `cached_split`, `split_layout`, `round`, `debug_elements`, `strength_is_valid`, `cache_size`, `margins`, `letters`, `length`, `max`, `min`, `percentage`, `percentage_start`, `percentage_spacebetween`, `ratio`, `ratio_start`, `ratio_spacebetween`, `vertical_split_by_height`, `edge_cases`, `constraint_length`, `table_length`, `length_is_higher_priority`, `length_is_higher_priority_in_flex`, `fixed_with_50_width`, `fill`, `percentage_parameterized`, `min_max`, `flex_constraint`, `flex_overlap`, `flex_spacing`, `constraint_specification_tests_for_priority`, `constraint_specification_tests_for_priority_with_spacing`, `fill_vs_flex`, `fill_spacing`, `fill_overlap`, `flex_spacing_lower_priority_than_user_spacing`, `split_with_spacers_no_spacing`, `split_with_spacers_and_spacing`, `split_with_spacers_and_overlap`, `split_with_spacers_and_too_much_spacing`, `legacy_vs_default` | 3/7 matched (target 6) | `Rects`, `Segments`, `Spacers`, `Cache` | 0/37 | 50 | 508207.4 |
| 30 | `style.stylize` | `style.Stylize [PROVENANCE-FALLBACK]` | 0.31 | 7/20 matched (target 390) | `fmt`, `str_styled`, `string_styled`, `cow_string_styled`, `temporary_string_styled`, `other_primitives_styled`, `color_modifier`, `fg_bg`, `repeated_attributes`, `all_chained`, `stylize_debug_foreground`, `stylize_debug_background`, `stylize_debug_underline` | 4/5 matched (target 7) | `Item` | 0/12 | 14 | 3142507.0 |
| 31 | `backend.test` | `backend.TestBackend [PROVENANCE-FALLBACK]` | 0.32 | 25/47 matched (target 29) | `assert_cursor_position`, `fmt`, `scroll_region_up`, `scroll_region_down`, `test_buffer_view`, `buffer_view_with_overwrites`, `assert_buffer_panics`, `assert_scrollback_panics`, `display`, `clear_region_all`, `clear_region_after_cursor`, `clear_region_before_cursor`, `clear_region_current_line`, `clear_region_until_new_line`, `append_lines_not_at_last_line`, `append_lines_at_last_line`, `append_multiple_lines_not_at_last_line`, `append_multiple_lines_past_last_line`, `append_multiple_lines_where_cursor_at_end_appends_height_lines`, `append_multiple_lines_where_cursor_appends_height_lines`, `append_multiple_lines_where_cursor_at_end_appends_more_than_height_lines`, `append_lines_truncates_beyond_u16_max` | 1/3 matched (target 1) | `Result`, `Error` | 0/18 | 24 | 245006.8 |
| 32 | `layout.alignment` | `layout.Alignment [PROVENANCE-FALLBACK]` | 0.37 | 2/4 matched (target 7) | `alignment_from_str`, `vertical_alignment_from_str` | 3/3 matched (target 4) | _none_ | 2/4 | 2 | 1020706.2 |
| 33 | `terminal.cursor` | `terminal.CursorTest [PROVENANCE-FALLBACK]` | 0.39 | 5/11 matched (target 5) | `hide_cursor`, `show_cursor`, `get_cursor`, `set_cursor`, `get_cursor_position`, `set_cursor_position` | 0/0 matched (target 1) | _none_ | 5/5 | 6 | 61106.1 |
| 34 | `style.anstyle` | `style.Anstyle [PROVENANCE-FALLBACK]` | 0.43 | 14/16 matched (target 26) | `from`, `try_from` | 1/2 matched (target 3) | `Error` | 14/14 | 3 | 31805.7 |
| 35 | `layout.position` | `layout.Position [PROVENANCE-FALLBACK]` | 0.51 | 10/16 matched (target 21) | `fmt`, `add`, `sub`, `add_assign`, `sub_assign`, `to_string` | 1/2 matched | `Output` | 7/8 | 7 | 4071805.0 |
| 36 | `buffer.cell_width` | `buffer.CellWidth [PROVENANCE-FALLBACK]` | 0.53 | 4/4 matched (target 8) | _none_ | 1/1 matched (target 2) | _none_ | 3/3 | 0 | 2000504.8 |
| 37 | `style` | `style.Style [PROVENANCE-FALLBACK]` | 0.63 | 25/33 matched (target 102) | `fmt`, `deserialize_modifier`, `fmt_stylize`, `combine_individual_modifiers`, `style_can_be_const`, `serialize_then_deserialize`, `deserialize_defaults`, `deserialize_null_modifiers` | 1/1 matched (target 3) | _none_ | 15/20 | 8 | 3083403.8 |
| 38 | `symbols.marker` | `symbols.Marker [PROVENANCE-FALLBACK]` | 0.68 | 2/2 matched (target 6) | _none_ | 1/1 matched (target 11) | _none_ | 2/2 | 0 | 303.2 |
| 39 | `widgets.widget` | `widgets.Widget [PROVENANCE-FALLBACK]` | 0.72 | 7/8 matched (target 11) | `buf` | 2/2 matched (target 3) | _none_ | 6/7 | 1 | 6011003.0 |
| 40 | `layout.offset` | `layout.Offset [PROVENANCE-FALLBACK]` | 0.73 | 5/5 matched | _none_ | 1/1 matched (target 2) | _none_ | 3/3 | 0 | 1000602.8 |
| 41 | `layout.constraint` | `layout.Constraint [PROVENANCE-FALLBACK]` | 0.73 | 10/12 matched (target 32) | `fmt`, `to_string` | 1/1 matched (target 8) | _none_ | 0/1 | 2 | 2021302.8 |
| 42 | `rect.iter` | `rect.Iter` | 0.74 | 14/16 matched (target 23) | `columns_max`, `columns_min` | 3/4 matched | `Item` | 10/12 | 3 | 5032002.5 |
| 43 | `widgets.stateful_widget` | `widgets.StatefulWidget [PROVENANCE-FALLBACK]` | 0.75 | 2/4 matched (target 6) | `buf`, `state` | 3/4 matched | `State` | 2/4 | 3 | 1030802.5 |
| 44 | `buffer.cell` | `buffer.Cell [PROVENANCE-FALLBACK]` | 0.79 | 19/21 matched (target 50) | `eq`, `hash` | 2/2 matched (target 7) | _none_ | 4/4 | 2 | 3022302.2 |
| 45 | `palette.material` | `material.Material [PROVENANCE-FALLBACK]` | 0.91 | 1/1 matched (target 2) | _none_ | 2/2 matched (target 3) | _none_ | - | 0 | 300.9 |
| 46 | `text.grapheme` | `text.Grapheme [PROVENANCE-FALLBACK]` | 0.93 | 5/5 matched (target 8) | _none_ | 1/2 matched | `Item` | 1/1 | 1 | 10700.7 |
| 47 | `terminal.frame` | `terminal.Frame [PROVENANCE-FALLBACK]` | 0.94 | 8/8 matched (target 10) | _none_ | 2/2 matched | _none_ | - | 0 | 1000.6 |
| 48 | `symbols.bar` | `bar.Bar [PROVENANCE-FALLBACK]` | 0.99 | 1/1 matched (target 16) | _none_ | 1/1 matched (target 2) | _none_ | - | 0 | 200.1 |
| 49 | `symbols.block` | `block.Block [PROVENANCE-FALLBACK]` | 0.99 | 1/1 matched (target 43) | _none_ | 1/1 matched (target 4) | _none_ | - | 0 | 200.1 |

## Cheat Detection / Scoring Failures

- `buffer` -> `buffer.BufferTest [STUB] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. target contains TODO/stub/placeholder markers in function bodies; no source functions found; report scoring is function-by-function only
- `layout.rect` -> `layout.Rect [ZERO]`: function-by-function score forced to 0. Rect.kt: snake_case identifier `try_layout` in Kotlin comments; RectUnitTest.kt: snake_case identifier `no_intersect` in Kotlin comments; RectUnitTest.kt: Rust attribute syntax in Kotlin comments
- `backend` -> `backend.Backend [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Backend.kt: score-padding suppression annotation `@Suppress` in Kotlin code
- `style.color` -> `style.Color [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Color.kt: Rust `match` expression in Kotlin comments; Color.kt: score-padding suppression annotation `@Suppress` in Kotlin code
- `terminal` -> `terminal.Terminal [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Terminal.kt: Rust `match` expression in Kotlin comments
- `text.masked` -> `text.Masked [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Masked.kt: Rust lifetime explanation in Kotlin comments
- `layout.flex` -> `layout.Flex [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.merge` -> `merge.Merge [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Merge.kt: Rust-only type/unsafe terminology in Kotlin comments
- `buffer.diff` -> `buffer.BufferDiff [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. BufferDiff.kt: Rust lifetime explanation in Kotlin comments
- `palette.tailwind` -> `tailwind.Tailwind [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.braille` -> `symbols.Braille [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.half_block` -> `symbols.HalfBlock [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.pixel` -> `symbols.Pixel [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only
- `symbols.scrollbar` -> `scrollbar.Scrollbar [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Scrollbar.kt: Rust-only type/unsafe terminology in Kotlin comments; no source functions found; report scoring is function-by-function only
- `symbols.shade` -> `shade.Shade [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. no source functions found; report scoring is function-by-function only

### Critical Ports (Similarity < 0.60, Worst First)

These files need significant work:

- `buffer` -> `buffer.BufferTest [STUB] [PROVENANCE-FALLBACK]` (0.00, 12 deps)
- `layout.rect` -> `layout.Rect [ZERO]` (0.00, 9 deps)
- `backend` -> `backend.Backend [ZERO] [PROVENANCE-FALLBACK]` (0.00, 6 deps)
- `style.color` -> `style.Color [ZERO] [PROVENANCE-FALLBACK]` (0.00, 4 deps)
- `terminal` -> `terminal.Terminal [ZERO] [PROVENANCE-FALLBACK]` (0.00, 2 deps)
- `text.masked` -> `text.Masked [ZERO] [PROVENANCE-FALLBACK]` (0.00, 1 deps)
- `layout.flex` -> `layout.Flex [ZERO] [PROVENANCE-FALLBACK]` (0.00, 1 deps)
- `symbols.merge` -> `merge.Merge [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `buffer.diff` -> `buffer.BufferDiff [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `palette.tailwind` -> `tailwind.Tailwind [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `symbols.braille` -> `symbols.Braille [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `symbols.half_block` -> `symbols.HalfBlock [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `symbols.pixel` -> `symbols.Pixel [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `symbols.scrollbar` -> `scrollbar.Scrollbar [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `symbols.shade` -> `shade.Shade [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `terminal.viewport` -> `terminal.Viewport [PROVENANCE-FALLBACK]` (0.00, 1 deps)
- `rect.ops` -> `rect.Ops [PROVENANCE-FALLBACK]` (0.00)
- `terminal.backend` -> `backend.ClearTypeTest [PROVENANCE-FALLBACK]` (0.00)
- `buffer.assert` -> `buffer.Assert [PROVENANCE-FALLBACK]` (0.00)
- `symbols.border` -> `border.Border [PROVENANCE-FALLBACK]` (0.08)
- `symbols.line` -> `line.Line [PROVENANCE-FALLBACK]` (0.14)
- `layout.margin` -> `layout.Margin [PROVENANCE-FALLBACK]` (0.15, 1 deps)
- `layout.direction` -> `layout.Direction [PROVENANCE-FALLBACK]` (0.17, 1 deps)
- `text.line` -> `text.Line [PROVENANCE-FALLBACK]` (0.17, 3 deps)
- `text.text` -> `text.Text [PROVENANCE-FALLBACK]` (0.19)
- `text.span` -> `text.Span [PROVENANCE-FALLBACK]` (0.24, 1 deps)
- `layout.size` -> `layout.Size [PROVENANCE-FALLBACK]` (0.24, 2 deps)
- `buffer.buffer` -> `buffer.Buffer [PROVENANCE-FALLBACK]` (0.25)
- `layout.layout` -> `layout.Layout [PROVENANCE-FALLBACK]` (0.26)
- `style.stylize` -> `style.Stylize [PROVENANCE-FALLBACK]` (0.31, 3 deps)
- `backend.test` -> `backend.TestBackend [PROVENANCE-FALLBACK]` (0.32)
- `layout.alignment` -> `layout.Alignment [PROVENANCE-FALLBACK]` (0.37, 1 deps)
- `terminal.cursor` -> `terminal.CursorTest [PROVENANCE-FALLBACK]` (0.39)
- `style.anstyle` -> `style.Anstyle [PROVENANCE-FALLBACK]` (0.43)
- `layout.position` -> `layout.Position [PROVENANCE-FALLBACK]` (0.51, 4 deps)
- `buffer.cell_width` -> `buffer.CellWidth [PROVENANCE-FALLBACK]` (0.53, 2 deps)

## Incorrect Ports (Missing Types)

These files are matched (often via `// port-lint`) but appear to be missing one or more type declarations
present in the Rust source file.

| Source | Target | Missing types | Examples |
|--------|--------|---------------|----------|
| `rect.iter` | `rect.Iter` | 1/4 | `Item` |
| `layout.position` | `layout.Position [PROVENANCE-FALLBACK]` | 1/2 | `Output` |
| `style.color` | `style.Color [ZERO] [PROVENANCE-FALLBACK]` | 1/5 | `Err` |
| `text.line` | `text.Line [PROVENANCE-FALLBACK]` | 3/5 | `Item`, `IntoIter`, `Output` |
| `style.stylize` | `style.Stylize [PROVENANCE-FALLBACK]` | 1/5 | `Item` |
| `text.span` | `text.Span [PROVENANCE-FALLBACK]` | 2/4 | `Output`, `Item` |
| `widgets.stateful_widget` | `widgets.StatefulWidget [PROVENANCE-FALLBACK]` | 1/4 | `State` |
| `text.text` | `text.Text [PROVENANCE-FALLBACK]` | 3/5 | `Item`, `IntoIter`, `Output` |
| `layout.layout` | `layout.Layout [PROVENANCE-FALLBACK]` | 4/7 | `Rects`, `Segments`, `Spacers`, `Cache` |
| `buffer.buffer` | `buffer.Buffer [PROVENANCE-FALLBACK]` | 1/2 | `Output` |
| `backend.test` | `backend.TestBackend [PROVENANCE-FALLBACK]` | 2/3 | `Result`, `Error` |
| `rect.ops` | `rect.Ops [PROVENANCE-FALLBACK]` | 1/1 | `Output` |
| `symbols.merge` | `merge.Merge [ZERO] [PROVENANCE-FALLBACK]` | 1/4 | `BorderSymbolError` |
| `style.anstyle` | `style.Anstyle [PROVENANCE-FALLBACK]` | 1/2 | `Error` |
| `text.grapheme` | `text.Grapheme [PROVENANCE-FALLBACK]` | 1/2 | `Item` |

## High Priority Missing Files

| Rank | Source file | Expected target | Deps | Functions | Classes/types | Symbols | Source path | Expected path |
|------|-------------|-----------------|------|-----------|---------------|---------|-------------|---------------|
| 1 | `terminal.render` | `terminal.Render` | 0 | 26 | 3 | 29 | `terminal/render.rs` | `terminal/Render.kt` |
| 2 | `terminal.buffers` | `terminal.Buffers` | 0 | 17 | 0 | 17 | `terminal/buffers.rs` | `terminal/Buffers.kt` |
| 3 | `terminal.inline` | `terminal.Inline` | 0 | 16 | 0 | 16 | `terminal/inline.rs` | `terminal/Inline.kt` |
| 4 | `terminal.resize` | `terminal.Resize` | 0 | 11 | 0 | 11 | `terminal/resize.rs` | `terminal/Resize.kt` |
| 5 | `terminal.init` | `terminal.Init` | 0 | 7 | 0 | 7 | `terminal/init.rs` | `terminal/Init.kt` |
| 6 | `style.palette_conversion` | `style.PaletteConversion` | 0 | 4 | 0 | 4 | `style/palette_conversion.rs` | `style/PaletteConversion.kt` |
| 7 | `text` | `text.Text` | 2 | 0 | 0 | 0 | `text.rs` | `text/Text.kt` |
| 8 | `layout` | `layout.Layout` | 0 | 0 | 0 | 0 | `layout.rs` | `layout/Layout.kt` |
| 9 | `style.palette` | `style.palette.Palette` | 0 | 0 | 0 | 0 | `style/palette.rs` | `style/palette/Palette.kt` |
| 10 | `symbols` | `symbols.Symbols` | 0 | 0 | 0 | 0 | `symbols.rs` | `symbols/Symbols.kt` |
| 11 | `widgets` | `widgets.Widgets` | 0 | 0 | 0 | 0 | `widgets.rs` | `widgets/Widgets.kt` |

## Documentation Gaps

There is missing documentation that is hurting overall scoring.

**Documentation coverage:** 3844 / 13422 lines (29%)

Documentation gaps (>20%), complete list:

- `layout.layout` - 91% gap (1212 → 110 lines)
- `palette.material` - 94% gap (854 → 49 lines)
- `layout.rect` - 80% gap (816 → 167 lines)
- `symbols.merge` - 92% gap (640 → 52 lines)
- `palette.tailwind` - 97% gap (600 → 20 lines)
- `text.line` - 63% gap (914 → 338 lines)
- `terminal` - 81% gap (712 → 136 lines)
- `buffer.buffer` - 83% gap (634 → 105 lines)
- `text.text` - 54% gap (898 → 409 lines)
- `layout.constraint` - 77% gap (560 → 131 lines)
- `backend` - 68% gap (616 → 196 lines)
- `style.color` - 70% gap (540 → 161 lines)
- `style` - 44% gap (816 → 454 lines)
- `text.span` - 61% gap (552 → 217 lines)
- `layout.flex` - 65% gap (374 → 132 lines)
- `symbols.border` - 61% gap (336 → 131 lines)
- `backend.test` - 89% gap (218 → 23 lines)
- `buffer.cell` - 78% gap (212 → 46 lines)
- `terminal.frame` - 58% gap (264 → 110 lines)
- `widgets.stateful_widget` - 58% gap (256 → 107 lines)
- `style.stylize` - 65% gap (178 → 63 lines)
- `widgets.widget` - 56% gap (150 → 66 lines)
- `terminal.viewport` - 70% gap (112 → 34 lines)
- `layout.position` - 46% gap (168 → 91 lines)
- `terminal.cursor` - 100% gap (76 → 0 lines)
- `rect.iter` - 77% gap (74 → 17 lines)
- `terminal.backend` - 100% gap (42 → 0 lines)
- `layout.size` - 43% gap (96 → 55 lines)
- `buffer.diff` - 65% gap (40 → 14 lines)
- `rect.ops` - 50% gap (50 → 25 lines)
- `buffer.cell_width` - 60% gap (40 → 16 lines)
- `layout.margin` - 36% gap (66 → 42 lines)
- `text.masked` - 39% gap (46 → 28 lines)
- `text.grapheme` - 27% gap (22 → 16 lines)
- `buffer.assert` - 30% gap (10 → 7 lines)
- `symbols.braille` - 25% gap (8 → 6 lines)

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

