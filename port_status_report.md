# Code Port - Progress Report

**Generated:** 2026-08-28
**Source:** ratatui-core/src/buffer/buffer.rs
**Target:** src/commonMain/kotlin/ratatui/buffer/Buffer.kt

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Function parity | 22/63 matched (target 38) | 34.9% |
| Class/type parity | 1/2 matched (target 3) | 50.0% |
| Combined symbol parity | 23/65 matched (target 41) | 35.4% |
| Average function body similarity | 0.00 | inline-code cosine |
| Average documentation similarity | 0.72 | doc text cosine |
| Missing source functions | 0 | 0% parity until ported |
| Missing source classes/types | 0 | 0% parity until ported |
| Missing source symbol files | 0 | 0 symbols |
| Cheat/scoring failures | 1 | forced to 0% |
| Total source files | 1 | 100% |
| Target units (paired) | 1 | - |
| Target files (total) | 1 | - |
| Porting progress | 1 | 100.0% (matched) |
| Missing files | 0 | 0.0% |

## Port Quality Analysis

**Average Function Similarity:** 0.00

Similarity in this report is the required function-by-function body/parameter score. Class/type parity and symbol deficits are reported beside it; whole-file shape is diagnostic only.

**Work Distribution:**
- Critical (<0.60): 1 files (100.0% of matched)
- Needs review (0.60-0.84): 0 files (0.0% of matched)

## Worst Function Scores First

Every matched file is listed from lowest function body/parameter similarity upward. Missing symbol names are not capped.

| Rank | Source | Target | Function similarity | Functions | Missing functions | Types | Missing types | Tests | Symbol deficit | Priority |
|------|--------|--------|---------------------|-----------|-------------------|-------|---------------|-------|----------------|----------|
| 1 | `buffer` | `Buffer [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 22/63 matched (target 38) | `content`, `area`, `fmt`, `debug_empty_buffer`, `debug_grapheme_override`, `debug_some_example`, `it_translates_to_and_from_coordinates`, `pos_of_panics_on_out_of_bounds`, `index_of_panics_on_out_of_bounds`, `test_cell`, `test_cell_mut`, `index_out_of_bounds_panics`, `index_mut_out_of_bounds_panics`, `set_string_multi_width_overwrite`, `set_string_zero_width`, `set_string_double_width`, `small_one_line_buffer`, `set_line_raw`, `set_line_styled`, `set_style_does_not_panic_when_out_of_area`, `diff_empty_empty`, `diff_empty_filled`, `diff_filled_filled`, `diff_single_width`, `diff_multi_width`, `diff_multi_width_offset`, `merge_diff_idempotent`, `merge_diff_forcedwidth`, `merge_diff_link`, `merge_diff_split_link`, `merge_diff_image_sequences`, `diff_skip`, `merge_with_offset`, `merge_skip`, `with_lines_accepts_into_lines`, `control_sequence_rendered_full`, `control_sequence_rendered_partially`, `renders_emoji`, `index_pos_of_u16_max`, `diff_clears_trailing_cell_for_wide_grapheme`, `diff_ignores_style_only_changes_in_trailing_cells` | 1/2 matched (target 3) | `Output` | 0/38 | 42 | 426510.0 |

## Cheat Detection / Scoring Failures

- `buffer` -> `Buffer [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. Buffer.kt: score-padding suppression annotation `@Suppress` in Kotlin code

### Critical Ports (Similarity < 0.60, Worst First)

These files need significant work:

- `buffer` -> `Buffer [ZERO] [PROVENANCE-FALLBACK]` (0.00)

## Incorrect Ports (Missing Types)

These files are matched (often via `// port-lint`) but appear to be missing one or more type declarations
present in the Rust source file.

| Source | Target | Missing types | Examples |
|--------|--------|---------------|----------|
| `buffer` | `Buffer [ZERO] [PROVENANCE-FALLBACK]` | 1/2 | `Output` |

## High Priority Missing Files

No missing files detected.

## Documentation Gaps

There is missing documentation that is hurting overall scoring.

**Documentation coverage:** 104 / 634 lines (16%)

Documentation gaps (>20%), complete list:

- `buffer` - 84% gap (634 → 104 lines)

