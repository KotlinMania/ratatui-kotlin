# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Current Progress:** 60.1% (157/233 files)
- **Matched Files:** 140
- **Average Similarity:** 0.36
- **Critical Issues:** 105 files with <0.60 similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. ratatui-core.buffer
- **Similarity:** 0.14 (needs 71% improvement)
- **Dependencies:** 88
- **Priority Score:** 75.4
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

### 4. ratatui-core.text
- **Similarity:** 0.15 (needs 70% improvement)
- **Dependencies:** 13
- **Priority Score:** 11.1
- **Action:** Deep review - likely missing major functionality

### 5. style.color
- **Similarity:** 0.33 (needs 52% improvement)
- **Dependencies:** 15
- **Priority Score:** 10.1
- **Action:** Deep review - likely missing major functionality

### 6. terminal.frame
- **Similarity:** 0.69 (needs 16% improvement)
- **Dependencies:** 22
- **Priority Score:** 6.9
- **Action:** Review and complete missing sections

### 7. commands.format
- **Similarity:** 0.68 (needs 17% improvement)
- **Dependencies:** 17
- **Priority Score:** 5.4
- **Action:** Review and complete missing sections

### 8. ratatui-core.style
- **Similarity:** 0.63 (needs 22% improvement)
- **Dependencies:** 14
- **Priority Score:** 5.1
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
