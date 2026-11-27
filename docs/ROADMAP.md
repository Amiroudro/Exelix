# Exelix Roadmap

## Primary focus: Android reconstruction and validation

### Implemented (CI-built)
- Native Kotlin/Jetpack Compose shell and Room offline database.
- Vocabulary add/edit/delete/search and metadata fields.
- Review queue and approximate reconstructed scheduler.
- Review history.
- JSON backup export and merge restore with duplicate skipping.
- CSV/TSV importer with preview, header/delimiter handling, duplicate detection, validation, multiline/quoted fields, and limits.
- Dashboard stats including today's activity and 7-day review/recall analytics.
- Session-level review completion counter.
- Dedicated Analytics screen: all-time reviews/recall, active days, streak, 7-day trend and daily activity.
- Settings: persistent daily goal (5–100), System/Light/Dark theme, review-schedule explanation and local/offline privacy notes.
- Review queue ordering updated to put unscheduled and oldest-due words first; ordering tests added and **CI build/test verified in build #53**.

### Next priorities
1. **Physical-device QA:** install alongside original on a test device; verify navigation, DB persistence, imports, backup/restore, review scheduling, and layout/keyboard behavior. This remains blocked until hands-on device verification is recorded.
2. **Review UX:** automated implementation now includes a session progress indicator, completion message, and compact five-item bottom navigation with a More hub. Further refinements should follow real-device observation (answer feedback, card layout, empty/due states).
3. **Analytics accuracy:** automated tests now cover streak continuity when today has no activity (yesterday-active streak), activity today, gaps, no activity, and future-only timestamps. Validate local date boundaries and review-event calculations on-device; consider a simple day-by-day view after runtime QA.
4. **Recovery fidelity:** inspect APK evidence further; implement additional features only where evidence is sufficient and scope is safe.
5. **Release hygiene:** CI now builds and publishes an unsigned Release APK alongside Debug. Remaining release step is signing with a private production keystore, then install/upgrade verification and final release checklist.

## Deferred
- Browser/PWA and public Pages hosting.
- Cloud sync/accounts until offline core and privacy requirements are stable.

## Evidence rule
Original APK is evidence, not recovered source. Distinguish implemented, CI-built, device-tested, and original-app-confirmed behavior. Never claim parity without evidence.