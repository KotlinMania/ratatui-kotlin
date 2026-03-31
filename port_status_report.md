# Code Port - Progress Report

**Generated:** 2026-02-11
**Source:** ratatui/src/
**Target:** src/

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Total source files | 6 | 100% |
| Target units (paired) | 94 | - |
| Target files (total) | 94 | - |
| Porting progress | 4 | 66.7% (matched) |
| Missing files | 2 | 33.3% |

## Port Quality Analysis

**Average Similarity:** 0.55

**Quality Distribution:**
- Excellent (≥0.85): 0 files (0.0% of matched)
- Good (0.60-0.84): 1 files (25.0% of matched)
- Critical (<0.60): 3 files (75.0% of matched)

### Excellent Ports (Similarity ≥ 0.85)

These files are well-ported and likely complete:


### Critical Ports (Similarity < 0.60)

These files need significant work:

- `widgets.stateful_widget_ref` → `widgets.StatefulWidgetRef` (0.58)
- `widgets` → `widgets.Widget` (0.45)
- `lib` → `logo.RatatuiLogo` (0.52)

## High Priority Missing Files

Files with highest dependency counts:

1. **init** (0 deps)
2. **prelude** (0 deps)

## Documentation Gaps

**Documentation coverage:** 275 / 2646 lines (10%)

Files with significant documentation gaps (>80%):

- `widgets` - 97% gap (1422 → 42 lines)
- `lib` - 90% gap (826 → 85 lines)

