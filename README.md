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
- Study history and analytics.
- CSV/TSV import with preview, duplicate detection, delimiter/header mapping, quoted and multiline fields, limits, and validation.
- JSON backup format v2 includes vocabulary, review history, daily goal, and theme. Restore merges vocabulary, maps history to local IDs, skips duplicates, and validates data.
- Version 1 vocabulary-only JSON backups remain supported.

## Build and verification
- GitHub Actions workflow: `.github/workflows/android-build.yml`.
- **Build 103 succeeded**: Android build/tests passed; Debug APK, unsigned Release APK, and unsigned AAB generated.
- Backup v2 changes merged into main via PR #1.
- **Owner-confirmed real-device testing: all tested functionality reported OK** (exact device/build/test evidence not yet recorded).
- Release signing secrets are not configured, so release outputs are unsigned.
- The CI workflow now fails fast on partial/malformed signing configuration and checks keystore password/alias before attempting signed builds; signed artifacts are verified when signing is configured.
- See [release/device QA checklist](docs/RELEASE_QA.md) and [roadmap](docs/ROADMAP.md).

## Original APK parity
- Evidence-based gap inventory: [APK parity audit](docs/APK_PARITY_AUDIT.md). APK identifier evidence suggests XLSX import/export, Markdown import/export, feed/news sync, batch/tag operations, and reminder workers require further verification or reconstruction. Exact behavior is not yet confirmed.

## Browser edition (paused)
`index.html` remains in the repository but is not the current focus.

## Privacy
App vocabulary data is stored locally in the reconstruction database. Cloud sync/accounts are not implemented.

## License
No license has been chosen. Do not assume third-party asset licenses.
