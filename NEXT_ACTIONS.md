# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Current Progress:** 66.7% (95/6 files)
- **Matched Files:** 4
- **Average Similarity:** 0.12
- **Critical Issues:** 4 files with <0.60 similarity

## Priority 1: Fix Incomplete High-Dependency Files

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
./ast_distance --init-tasks ../../tmp/ratatui/ratatui/src rust ../../src/commonMain/kotlin/ratatui kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
