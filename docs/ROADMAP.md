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

### Verified by CI and device
- **Build 103 succeeded** on 2026-11-27: Android variants and tests passed.
- Debug APK, unsigned Release APK, and unsigned AAB were generated.
- Backup v2 was merged into `main` via PR #1.
- **Owner reports physical-device testing completed with all tested items OK.** Device/build/test details have not been logged; see [RELEASE_QA.md](RELEASE_QA.md).
- Release workflow hardening validates signing-secret completeness, decodes the keystore, checks configured alias/password before build, and verifies signed APK/AAB when signing is enabled. A CI run for the hardening change remains unconfirmed.

### Remaining release priorities
1. **Original-app parity audit:** initial evidence-based matrix added in [APK_PARITY_AUDIT.md](APK_PARITY_AUDIT.md). High-risk gaps include XLSX import/export, Markdown import/export, feed/news sync, batch actions/tags, and review worker/reminder behavior; exact original behavior remains unconfirmed.
2. **Permanent application ID:** current reconstruction uses `com.depu.vocabapp.rebuild`; original app package is `com.depu.vocabapp`; original signing certificate/source are not recovered. Decide final ID before release.
3. **Release signing:** configure a secure owner-controlled keystore and GitHub secrets; generate and verify signed APK/AAB. Signing secrets are not currently configured.
4. **CI validation:** confirm the hardened signing workflow is tested by a successful CI run.
5. **Release readiness:** record candidate-build QA evidence, versioning, privacy disclosures, release notes, and distribution decision.

## Parity audit notes
- A first evidence-based audit now maps recovered APK class/method identifiers to the reconstruction and lists unverified features. It is a gap inventory, not a parity certification. See [APK_PARITY_AUDIT.md](APK_PARITY_AUDIT.md).

## Deferred
- Browser/PWA and public Pages hosting.
- Cloud sync/accounts until offline core and privacy requirements are stable.

## Evidence rule
Original APK is evidence, not recovered source. Distinguish implemented, CI-built, device-tested, and original-app-confirmed behavior. Never claim parity without evidence.
