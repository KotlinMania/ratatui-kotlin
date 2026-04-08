# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Current Progress:** 55.8% (136/233 files)
- **Matched Files:** 130
- **Average Similarity:** 0.34
- **Critical Issues:** 101 files with <0.60 similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. ratatui-core.buffer
- **Similarity:** 0.00 (needs 85% improvement)
- **Dependencies:** 88
- **Priority Score:** 88.0
- **Action:** Deep review - likely missing major functionality

### 2. widgets.widget
- **Similarity:** 0.34 (needs 51% improvement)
- **Dependencies:** 44
- **Priority Score:** 29.2
- **Action:** Deep review - likely missing major functionality

### 3. ratatui-macros.line
- **Similarity:** 0.52 (needs 33% improvement)
- **Dependencies:** 37
- **Priority Score:** 17.7
- **Action:** Deep review - likely missing major functionality

### 4. style.color
- **Similarity:** 0.17 (needs 68% improvement)
- **Dependencies:** 15
- **Priority Score:** 12.5
- **Action:** Deep review - likely missing major functionality

### 5. ratatui-core.text
- **Similarity:** 0.13 (needs 72% improvement)
- **Dependencies:** 13
- **Priority Score:** 11.3
- **Action:** Deep review - likely missing major functionality

### 6. commands.format
- **Similarity:** 0.59 (needs 26% improvement)
- **Dependencies:** 17
- **Priority Score:** 6.9
- **Action:** Deep review - likely missing major functionality

### 7. terminal.frame
- **Similarity:** 0.69 (needs 16% improvement)
- **Dependencies:** 22
- **Priority Score:** 6.9
- **Action:** Review and complete missing sections

### 8. ratatui-core.style
- **Similarity:** 0.62 (needs 23% improvement)
- **Dependencies:** 14
- **Priority Score:** 5.4
- **Action:** Review and complete missing sections

### 9. buffer.cell
- **Similarity:** 0.67 (needs 18% improvement)
- **Dependencies:** 10
- **Priority Score:** 3.3
- **Action:** Review and complete missing sections

### 10. tests.terminal
- **Similarity:** 0.80 (needs 5% improvement)
- **Dependencies:** 13
- **Priority Score:** 2.6
- **Action:** Minor refinements needed

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

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
./ast_distance --init-tasks ../../tmp/ratatui rust ../../src kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
