# Exelix

Exelix is a personal vocabulary-learning app. **Android-native APK reconstruction is the primary focus.** Browser/PWA work is paused.

## Recovery facts

- Original APK package: `com.depu.vocabapp`.
- The original Kotlin source has not been recovered; this project is an evidence-based reconstruction, not the original code.
- APK schema evidence is used to reconstruct Room vocabulary and import-batch data models.
- The reconstruction APK uses application ID `com.depu.vocabapp.rebuild` so it can be installed alongside the original without replacing it.
- Do not uninstall or overwrite the original app as part of testing.

## Android project

Native project files are in the repository root and `app/` module.

Initial native features:
- Local Room database, vocabulary add/delete, search, and duplicate detection.
- Recovered vocabulary metadata fields (Bangla meaning, definition, part of speech, synonyms, antonyms, collocations, examples, source, tags).
- Overview, library, review, and backup/transfer tabs.
- Reconstructed spaced review schedule (approximate): Again -> 10 minutes; remembered intervals -> 1, 3, 7, 14, 30 days.

Build status:
- GitHub Actions Android build workflow: `.github/workflows/android-build.yml`.
- The build is being iterated from actual CI failures; no verified APK artifact is available yet.
- Review scheduling unit tests are present; test execution will be added to CI after the APK build is stable.

## Browser edition (paused)

`index.html` remains in the repository but is not the current focus. No active Pages deployment is intended during Android work.

## Privacy and data safety

The reconstructed Android app stores data locally. Backup/restore is a planned Android feature. The reconstruction uses a separate database and app ID and does not access the original app's private database.

## License

No license has been chosen. Do not assume third-party asset licenses.
