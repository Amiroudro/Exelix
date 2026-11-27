# Exelix Release & Device QA Checklist

This checklist is for the reconstructed native Android app. It must be completed on a physical device before a production release is claimed.

## Build provenance
- [x] CI Android build and unit tests passed (Build 103 / workflow run 46036326956).
- [x] Debug APK, unsigned release APK, and unsigned AAB artifacts generated.
- [x] Backup v2 implementation merged into `main`.
- [ ] Install the current Debug APK on a physical Android device.
- [ ] Record device model, Android version, build number, test date, and tester.

## Install / navigation
- [ ] Install alongside the original app (do not uninstall or overwrite original).
- [ ] Fresh launch and subsequent relaunch.
- [ ] Home, Library, Review, History, Analytics, Backup, Settings / More navigation.
- [ ] Back navigation, scroll behavior, keyboard handling, and small-screen layout.
- [ ] Dark, light, and system themes.

## Vocabulary CRUD and data integrity
- [ ] Add a word with Bangla meaning, definition, POS, example, synonyms, antonyms, collocations, source, and tags.
- [ ] Edit and save fields; confirm persistence after force-stop/relaunch.
- [ ] Search using English and Bangla text.
- [ ] Delete a word and confirm review/history handling is understandable.
- [ ] Empty library and large library states.

## Review
- [ ] Review queue ordering, due count, and unscheduled items.
- [ ] Reveal answer and Again / Remembered actions.
- [ ] Verify review history records expected word, result, and time.
- [ ] Verify schedule dates and session progress.
- [ ] Empty queue and completed-session states.

## Import and backup/restore
- [ ] CSV and TSV preview, header mapping, duplicate detection, malformed rows, and Bangla.
- [ ] Quoted commas, escaped quotes, and multiline cells.
- [ ] Import file-size and row-limit errors are clear.
- [ ] Export v2 backup; verify it contains vocabulary, review events, goal, and theme.
- [ ] Restore into an empty installation and verify all data/preferences.
- [ ] Restore into a populated installation; confirm merge/no-overwrite and duplicate skipping.
- [ ] Restore a v1 backup for compatibility.
- [ ] Invalid JSON, unsupported version, and oversized backup produce safe error messages.

## Analytics / settings
- [ ] Totals and recall calculations match review history.
- [ ] Streak, active days, 7-day activity, and date boundaries.
- [ ] Daily goal range and persistence.
- [ ] Theme preference persistence.

## Release blockers
- [ ] Choose permanent application ID. Current reconstruction ID is `com.depu.vocabapp.rebuild`; it is not the original app ID.
- [ ] Create and securely store a release keystore; configure signing secrets in GitHub Actions. Never commit the keystore or passwords.
- [ ] Build signed release APK/AAB and verify signing identity.
- [ ] Compare recovered features against the original APK; record evidence and gaps.
- [ ] Final regression run on physical device; resolve crashes and high-severity usability issues.
- [ ] Prepare release notes, versioning, privacy disclosures, and distribution decision.

## Results log
| Date | Device / Android | Build | Tester | Result / issues |
|---|---|---|---|---|
| Pending | Pending | 103 | Pending | Device QA not yet performed |
