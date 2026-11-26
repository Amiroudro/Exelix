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

Native features currently implemented:
- Local Room database, vocabulary add/delete, search, and duplicate detection.
- Recovered vocabulary metadata fields (Bangla meaning, definition, part of speech, synonyms, antonyms, collocations, examples, source, tags).
- Overview, library, review, and backup/transfer tabs.
- JSON backup export and merge restore, with duplicate skipping and metadata preservation.
- Reconstructed spaced review schedule (approximate): Again -> 10 minutes; remembered intervals -> 1, 3, 7, 14, 30 days.

Build status:
- GitHub Actions Android build workflow: `.github/workflows/android-build.yml`.
- Android CI successfully produced a debug APK for commit `33715721` (build run #16). The artifact was downloaded and APK archive integrity checked. It is not device-tested.
- CI runs review-scheduler unit tests before assembling the APK; scheduler tests passed in run #17.
- Review scheduling unit tests are in CI and passed in run #17.

## Browser edition (paused)

`index.html` remains in the repository but is not the current focus. No active Pages deployment is intended during Android work.

## Privacy and data safety

The reconstructed Android app stores data locally. Backup/restore is a planned Android feature. The reconstruction uses a separate database and app ID and does not access the original app's private database.

## License

No license has been chosen. Do not assume third-party asset licenses.
