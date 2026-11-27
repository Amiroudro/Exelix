# Exelix Roadmap

## Primary focus: Android reconstruction and validation

### Implemented in the reconstruction (CI-built)
- Native Kotlin/Jetpack Compose shell and Room offline database.
- Vocabulary add/edit/delete/search and metadata fields.
- Review queue and approximate reconstructed scheduler.
- Review history.
- CSV/TSV importer with preview, header/delimiter handling, duplicate detection, validation, multiline/quoted fields, and limits.
- Dashboard stats and Analytics screen (all-time aggregates, recall, streak, active days, daily trend).
- Settings: persistent daily goal (5–100), System/Light/Dark theme, review-schedule explanation and local/offline privacy notes.
- JSON backup format v2: vocabulary, complete review-event history, daily goal, and theme.
- Restore merges vocabulary, remaps imported review events to local word IDs, avoids duplicate history events, and retains Version 1 vocabulary-only backup compatibility.
- Backup data and preferences are bounded and validated.
- Unit tests cover backup snapshot round-trip, v1 compatibility, settings sanitization, unsupported versions, and other core logic.

### Verified by CI
- **Build 103 succeeded** on 2026-11-27: Android variants and tests passed.
- Debug APK, unsigned Release APK, and unsigned AAB were generated.
- Backup v2 was merged into `main` via PR #1.
- Physical-device runtime behavior is **not yet verified**.
- Release workflow now validates signing-secret completeness, decodes the keystore, checks the configured alias/password before build, and verifies signed APK/AAB when signing is enabled. CI will validate this workflow change on its next run.

### Remaining release priorities
1. **Physical-device QA:** run the full checklist in [RELEASE_QA.md](RELEASE_QA.md). Installation, navigation, database persistence, import/export/restore, scheduling, layout, and keyboard behavior require hands-on testing.
2. **Original-app parity audit:** compare reconstructed screens/features against original APK evidence; mark verified behavior and gaps. Do not claim parity without evidence.
3. **Permanent application ID:** current reconstruction uses `com.depu.vocabapp.rebuild`; decide permanent ID before public release. Original app package is `com.depu.vocabapp`; original signing certificate/source are not recovered.
4. **Release signing:** configure a secure owner-controlled keystore and GitHub secrets; generate and verify signed APK/AAB. Signing secrets are not currently configured.
5. **Release readiness:** versioning, privacy disclosures, release notes, and final device regression.

## Deferred
- Browser/PWA and public Pages hosting.
- Cloud sync/accounts until offline core and privacy requirements are stable.

## Evidence rule
Original APK is evidence, not recovered source. Distinguish implemented, CI-built, device-tested, and original-app-confirmed behavior. Never claim parity without evidence.
