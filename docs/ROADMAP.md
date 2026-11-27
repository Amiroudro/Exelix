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
- Review queue ordering updated to put unscheduled and oldest-due words first; ordering tests added and **CI build/test verified in build #53**.

### Next priorities
1. **Physical-device QA:** install alongside original on a test device; verify navigation, DB persistence, imports, backup/restore, review scheduling, and layout/keyboard behavior.
2. **Review UX:** improve session pacing, answer feedback, card layout, and empty/due states based on device observations.
3. **Analytics accuracy:** validate local date boundaries and review-event calculations; consider a simple day-by-day view after runtime QA.
4. **Recovery fidelity:** inspect APK evidence further; implement additional features only where evidence is sufficient and scope is safe.
5. **Release hygiene:** versioning, signed release build, privacy/data-loss warnings, and reproducible release checklist.

## Deferred
- Browser/PWA and public Pages hosting.
- Cloud sync/accounts until offline core and privacy requirements are stable.

## Evidence rule
Original APK is evidence, not recovered source. Distinguish implemented, CI-built, device-tested, and original-app-confirmed behavior. Never claim parity without evidence.