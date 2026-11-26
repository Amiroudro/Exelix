# Exelix recovery facts and boundaries

## Original APK evidence
- Package name: `com.depu.vocabapp`.
- APK SHA-256: `58356d0eb5655dd52fa50cf1723487923e7fd6ada69f8a7709041c7df5899dec`.
- Three DEX files; app-owned class and method inventories recovered from identifier tables.
- Architecture evidence indicates Kotlin/Jetpack Compose and Room.
- Reconstructed SQL evidence includes `vocabulary` and `import_batches` tables, their columns, and indexes.
- Class/method names include `VocabWord`, `VocabDao`, `VocabDatabase`, `VocabRepository`, `BackupManager`, `MarkdownImporter`, `MarkdownExporter`, `XlsxImporter`, `XlsxExporter`, `FeedClient`, `FeedSyncer`, and review/feed workers.
- Original source code and original review algorithm are not recovered. DEX identifiers and SQL strings are evidence, not decompiled method bodies.

## Android reconstruction (primary focus)
- Native Gradle/Kotlin/Compose/Room project added under repository root and `app/`.
- Reconstructed Room entities align with APK schema evidence, including vocabulary metadata and import batches.
- App ID is `com.depu.vocabapp.rebuild`, while namespace remains `com.depu.vocabapp`, to allow side-by-side installation and avoid replacing the original app.
- Current native screens: Home overview; searchable vocabulary library; add word with metadata and normalized duplicate check; delete word; review queue with Again/Remembered; Backup tab placeholder.
- Spaced review schedule is an approximation: Again schedules 10 minutes; remembered intervals are 1, 3, 7, 14, 30 days. Unit tests have been added for this scheduler.
- CI exposed and corrected a missing Lifecycle Compose artifact and Java/Kotlin JVM-target mismatch. Android debug build run #16 completed successfully for commit `33715721`; the APK artifact was downloaded, extracted, and ZIP integrity verified. Physical/emulator installation and runtime remain untested.
- Review scheduler unit tests are present and CI has been updated to execute them; the test-enabled build is pending.
- APK is not tested on a physical device or emulator. Backup/restore, imports, exports, history, feed, reminders, Excel support, and original UI fidelity are not yet implemented.

## Browser reconstruction (paused)
- Browser app remains in `index.html`, with basic vocabulary CRUD/review/import/export reconstructed.
- User requested prioritizing Android APK and pausing browser work. Pages/validation workflows now filter web-only changes and are not intended to run for Android-only changes.

## Safety/data handling
- Never uninstall or overwrite the original APK as part of reconstruction testing.
- Do not access original private app data. The reconstruction has its own database and app identity.
- Mark features reconstructed or untested until a real build and runtime test confirms them.
