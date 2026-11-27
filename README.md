# Exelix

Exelix is a personal vocabulary-learning app. **Android-native APK reconstruction is the primary focus.** Browser/PWA work is paused.

## Recovery facts
- Original APK package: `com.depu.vocabapp`.
- Original source was not recovered. This project is an evidence-based reconstruction, not the original source.
- APK schema evidence informed reconstruction of vocabulary and import-batch models.
- Reconstruction APK application ID: `com.depu.vocabapp.rebuild`, so it can coexist with the original app.
- Do not uninstall or overwrite the original app while testing.

## Native Android features
- Offline Room database.
- Vocabulary add/edit/delete/search with recovered metadata (Bangla meaning, definition, part of speech, examples, synonyms, antonyms, collocations, source, tags).
- Home dashboard: word count, due count, mastery/learning stats, today's reviews, and last-7-days review count and recall percentage.
- Review queue with Again/Remembered, reconstructed approximate intervals (Again ~10 minutes; remembered 1, 3, 7, 14, 30 days).
- Study history.
- CSV/TSV import with preview, duplicate counts, delimiter/header mapping, quoted commas, escaped quotes, multiline fields, input/row limits, and validation.
- JSON backup export and merge restore, skipping duplicates.

## Build and verification
- GitHub Actions Android workflow: `.github/workflows/android-build.yml`.
- Build #49 succeeded for 7-day analytics (commit `df683b46`).
- Build #50 succeeded for the review-session counter (commit `b3bc492d`).
- Build #50 APK artifact downloaded and checked as an Android APK archive. SHA-256: `38988f82c02e5a79ff3676c12bb6f9ef80fbf355b1b28bb59ae4cb9113966725`.
- CI build/test is not a substitute for physical-device runtime testing. Device installation, app behavior, and original-app comparison remain pending.

## Browser edition (paused)
`index.html` remains in the repository but is not the current focus.

## Privacy
App vocabulary data is stored locally in the reconstruction database. Cloud sync/accounts are not implemented.

## License
No license has been chosen. Do not assume third-party asset licenses.