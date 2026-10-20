# Independent published-root consumer

This separate Gradle project declares only `io.github.kotlinmania:ratatui-kotlin`
in commonMain. It imports Text's width trait, Modifier's bitflags trait,
ParseColorError's standard-error trait and Color's Serde Deserialize contract.
It has no direct workaround dependencies, sibling source/composite substitutions
or mavenLocal repository. The real root metadata must expose owning crate APIs.

Run the producer's `stageConsumerPublications` to stage the current facade,
core, widgets and macros root/Android/JVM coordinates into `build/staging-deploy`.
The consumer project uses an explicit absolute repository path and version:

```sh
./gradlew --project-dir verification/consumer compileAndroidMain compileAndroidHostTest testAndroidHostTest jvmTest -PproducerVersion=VERSION -PproducerRepository=/ABSOLUTE/PRODUCER/build/staging-deploy --no-daemon --max-workers=2 --no-configuration-cache --console=plain
```

Both ANDROID_HOME and ANDROID_SDK_ROOT must identify the same existing SDK.
Transitive dependencies resolve from public Google/Central repositories. The
selected Android root variant, exact AAR hash and compile/runtime raw classpaths
are recorded. Duplicate classes are checked separately for compile and runtime;
combining configurations creates false collisions between annotations versions.

`check_publication_consumer.py stage` packages only the exact current coordinates
and verifies Android API/runtime redirects, owning component URLs, AAR hashes,
POM packaging and compile-scope dependencies for every component:

- facade: API core/widgets/macros project-coordinate edges;
- core: actual Text/Modifier/ParseColorError/ColorSerializer bytecode and four
  API edges to bitflags, thiserror, unicode-width and serde-core;
- widgets: actual ListState/TableState/ScrollbarState bytecode and core API edge;
- macros: actual SpanKt/RowKt bytecode and core/widgets API edges.

These edges use the actual `-android` POM modules selected by Android KMP. No
manually assembled second AAR is published. The root is an API facade; concrete
class ownership stays in the appropriate crate archive, never duplicate sources
or Kotlin wrapper/typealias reexports.

`check_publication_consumer.py consumer` requires all four named tests on BOTH
JVM and Android host (eight actual cases), no missing/duplicate/failed/skipped
cases, empty duplicate receipts and the selected staged AAR's exact hash. Tests
exercise actual public contracts, including a Serde string decode through the
facade's transitive API edge. Android device/native runtime remains separate;
consumer host proof cannot substitute for those platform gates.

Historical eight-pass receipts at the earlier flattened source head are retained
in automation-artifacts. They do not validate the changed multi-module graph.
Fresh staging, consumer execution and full platform/strict Swift gates are
required before version bump, merge and publication.
