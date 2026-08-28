# port-lint Proposed Changes

**Generated:** 2026-08-27
**Source:** ratatui/src
**Target:** src/commonMain/kotlin

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/ratatui/widgets/WidgetRef.kt` | `// port-lint: source ratatui/src/widgets/widgetRef.rs` | `// port-lint: source widgets/widget_ref.rs` | `widgets/widget_ref.rs` | `port-lint provenance header matched only after fallback normalization: 'ratatui/src/widgets/widgetRef.rs' vs expected 'widgets/widget_ref.rs'` |
| `src/commonMain/kotlin/ratatui/widgets/StatefulWidgetRef.kt` | `// port-lint: source ratatui/src/widgets/statefulWidgetRef.rs` | `// port-lint: source widgets/stateful_widget_ref.rs` | `widgets/stateful_widget_ref.rs` | `port-lint provenance header matched only after fallback normalization: 'ratatui/src/widgets/statefulWidgetRef.rs' vs expected 'widgets/stateful_widget_ref.rs'` |
