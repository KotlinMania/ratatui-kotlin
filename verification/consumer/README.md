# Independent public API consumer

This separate Gradle project depends only on the Ratatui root coordinate in commonMain. It imports the public supertypes of Text, Modifier and ParseColorError and the Color serializer interface. No sibling source project, composite build, mavenLocal or direct workaround dependencies are available.

Run with the existing producer wrapper after obtaining the shared build allocation. Pass producerVersion explicitly. Without producerRepository the consumer uses published Central artifacts. For a staged metadata check, pass the absolute path of the producer build/staging-deploy repository as producerRepository. Only Ratatui root, Android and JVM coordinates are selected from that repository; transitive dependencies use Google/Central.

Before a staged run, invoke the existing root, Android and JVM publication staging tasks:

    ./gradlew publishKotlinMultiplatformPublicationToCentralPortalStagingRepository publishAndroidPublicationToCentralPortalStagingRepository publishJvmPublicationToCentralPortalStagingRepository --no-daemon --max-workers=2 --no-configuration-cache

Then invoke this independent consumer, using explicit version and staging path properties:

    ./gradlew --project-dir verification/consumer compileAndroidMain compileAndroidHostTest testAndroidHostTest jvmTest --no-daemon --max-workers=2 --no-configuration-cache -PproducerVersion=VERSION -PproducerRepository=ABSOLUTE_STAGING_PATH

A successful result must include the four named tests on JVM and Android host with nonzero executed XML counts. Preserve selected artifact receipts and run the Android publication verifier against a ZIP of the current root and Android coordinates, asserting public bytecode plus all four reviewed API edges in GMM and POM. Actual Android POM target module names must come from generated metadata, including the selected Serde runtime module.

Repeat without producerRepository after the new immutable version is published. This fixture proves public dependency compile selection and host runtime behavior; Android device, Native, Apple, JS and Wasm platform tests remain separate required gates.

Focused validation on 2026-10-20 passed: root/Android/JVM filesystem staging, all four GMM/POM API edges and AAR public classes, independent Android/JVM consumer compilation, and four executed tests on each host with zero failures, errors or skips. The selected Android AAR hash matched the staged payload. Compile and runtime raw classpaths were checked independently with no duplicate classes. Full KMP, Swift, Android device, remote CI and immutable external publication remain separate pending gates.

The producer-local guard is verification/verify_android_publication.py. Supply the actual deployment ZIP with --group io.github.kotlinmania --module ratatui-kotlin --version VERSION --receipt RECEIPT.json, the --public-class paths ratatui/text/Text.class, ratatui/style/Modifier.class, ratatui/style/ParseColorError.class and ratatui/style/ColorSerializer.class, and four --api-dependency triples. Each triple is group:root-module:actual-Android-POM-module; use bitflags-kotlin, thiserror-kotlin, unicode-width-kotlin and serde-core roots with their verified selected Android POM module names.

The existing Central 0.1.12 Android POM confirms the selected modules for these reviewed edges: io.github.kotlinmania:bitflags-kotlin:bitflags-kotlin-android, io.github.kotlinmania:thiserror-kotlin:thiserror-kotlin-android, io.github.kotlinmania:unicode-width-kotlin:unicode-width-kotlin-android and org.jetbrains.kotlinx:kotlinx-serialization-core:kotlinx-serialization-core-jvm. Recheck the generated staged POM and root/API metadata on each version.

Full CI now invokes consumer.yml: stage only the current root/Android/JVM coordinates, verify the four public API edges and bytecode, compile and run the independent consumer, then require all eight named tests, separate duplicate checks and an exact staged-versus-selected AAR hash match. This preserves dependency metadata proof independently of producer self-tests. The new workflow still requires its first allocated execution; replaying the helper against historical artifacts is guard validation only.

The Rust dependency alignment repair changes the fourth public contract to
`Deserialize<Color>` from the published `io.github.kotlinmania:serde-core:0.1.11`
runtime. Its Android POM target is `serde-core-android` (verified from Central).
The older 0.1.12 serialization-core edge above describes historical evidence,
not the new candidate. Re-run staging and consumer tests before accepting it.
Serde JSON belongs only to producer tests; the independent consumer exercises
`StrDeserializer` from the public runtime edge without a direct dependency.
