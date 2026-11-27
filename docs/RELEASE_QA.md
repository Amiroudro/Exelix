# Exelix Release & Device QA Checklist

This checklist applies to the reconstructed native Android app. It must be completed on a physical device before a production release is claimed.

## Build provenance
- [x] CI Android build and unit tests passed (Build 103 / workflow run 46036326956).
- [x] Debug APK, unsigned release APK, and unsigned AAB artifacts generated.
- [x] Backup v2 implementation merged into `main`.
- [ ] Install the current Debug APK on a physical Android device.
- [ ] Record device model, Android version, build number, test date, and tester.

## Safe test setup (Windows + Android)
1. Download the Debug APK from the Build 103 artifact package and keep the original app installed.
2. Install the reconstruction as a separate app. Its package is `com.depu.vocabapp.rebuild`; the original package is `com.depu.vocabapp`.
3. If using USB/ADB, enable Developer options and USB debugging only on your own device, connect by USB, then use:
   - `adb devices` to confirm the device is authorized.
   - `adb install -r app-debug.apk` to install/update the reconstruction.
   - `adb shell pm list packages | findstr vocabapp` to confirm both package IDs are present.
4. Launch the reconstruction, not the original. Never uninstall the original app as part of this QA.
5. If a test causes data loss or a crash, record exact steps, screen, and whether it reproduces after a clean app-data reset. Do not reset the original app.

## Install / navigation
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

## Release signing (owner-controlled)
The project intentionally does not contain a keystore or signing credentials. Do this from the repository's GitHub **Settings → Secrets and variables → Actions**:
- Create a release keystore locally and store a secure offline backup; do not commit it or upload it to chat.
- Add repository secrets with these exact names: `EXELIX_RELEASE_KEYSTORE_BASE64`, `EXELIX_RELEASE_STORE_PASSWORD`, `EXELIX_RELEASE_KEY_ALIAS`, `EXELIX_RELEASE_KEY_PASSWORD`.
- The Base64 secret is the keystore file encoded as one Base64 string. Generate it locally in PowerShell with `[Convert]::ToBase64String([IO.File]::ReadAllBytes('PATH_TO_KEYSTORE'))`; copy the result into the secret field and keep the original keystore backed up safely.
- Set strong unique passwords; never share them in issue comments, commit history, or chat.
- Run the Android APK Build workflow manually (Actions → Android APK Build → Run workflow) after secrets are configured.
- Confirm the run reports successful APK/AAB signature verification. Download artifacts only from the trusted repository workflow run.
- Keep the keystore and passwords backed up securely. Losing the keystore can prevent future updates using the same signing identity.

## Release blockers
- [ ] Choose permanent application ID. Current reconstruction ID is `com.depu.vocabapp.rebuild`; it is not the original app ID.
- [ ] Owner creates and securely stores release keystore; configure signing secrets in GitHub Actions. Never commit the keystore or passwords.
- [ ] Build signed release APK/AAB and verify signing identity.
- [ ] Compare recovered features against the original APK; record evidence and gaps.
- [ ] Final regression run on physical device; resolve crashes and high-severity usability issues.
- [ ] Prepare release notes, versioning, privacy disclosures, and distribution decision.

## Results log
| Date | Device / Android | Build | Tester | Result / issues |
|---|---|---|---|---|
| Pending | Pending | 103 | Pending | Device QA not yet performed |
