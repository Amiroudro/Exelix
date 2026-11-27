# Exelix Release & Device QA Checklist

This checklist applies to the reconstructed native Android app. Device QA is marked passed based on the owner's confirmation that real-device testing completed successfully.

## Build provenance
- [x] CI Android build and unit tests passed (Build 103 baseline and Build 111 / workflow run 46046650507 for Markdown import/export).
- [x] Debug APK, unsigned release APK, and unsigned AAB artifacts generated (Build 111).
- [x] Backup v2 implementation merged into `main`.
- [x] Owner reports real-device testing completed; all tested items OK (device/build/date details not recorded here).
- [ ] Record device model, Android version, exact tested build number, test date, and tester when available.

## Safe test setup (Windows + Android)
1. Download the Debug APK from the latest verified Build 111 artifact package and keep the original app installed.
2. Install the reconstruction as a separate app. Its package is `com.depu.vocabapp.rebuild`; the original package is `com.depu.vocabapp`.
3. If using USB/ADB, enable Developer options and USB debugging only on your own device, connect by USB, then use:
   - `adb devices` to confirm the device is authorized.
   - `adb install -r app-debug.apk` to install/update the reconstruction.
   - `adb shell pm list packages | findstr vocabapp` to confirm both package IDs are present.
4. Launch the reconstruction, not the original. Never uninstall the original app as part of this QA.
5. If a test causes data loss or a crash, record exact steps, screen, and whether it reproduces after a clean app-data reset. Do not reset the original app.

## Install / navigation
- [x] Owner-confirmed real-device test: launch/relaunch, navigation, back/scroll/keyboard/small-screen behavior, themes (details not individually logged).
- [ ] Add detailed test evidence/build/device info to results log.

## Vocabulary CRUD and data integrity
- [x] Owner-confirmed real-device test: vocabulary and data-related functionality passed (individual test cases not separately logged).
- [ ] Add detailed test evidence/build/device info to results log.

## Review
- [x] Owner-confirmed real-device test: review functionality passed (individual test cases not separately logged).
- [ ] Add detailed test evidence/build/device info to results log.

## Import and backup/restore
- [x] Owner-confirmed real-device test: import/backup-related functionality passed (individual test cases not separately logged).
- [ ] Add detailed test evidence/build/device info to results log.

## Analytics / settings
- [x] Owner-confirmed real-device test: analytics/settings functionality passed (individual test cases not separately logged).
- [ ] Add detailed test evidence/build/device info to results log.

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
- [x] Unsigned-build CI path validated in Build 111; signed-keystore path remains untested because signing secrets are not configured.
- [ ] Build signed release APK/AAB and verify signing identity.
- [ ] Compare recovered features against the original APK; record evidence and gaps.
- [ ] Complete release regression test on the exact candidate build and document evidence.
- [ ] Prepare release notes, versioning, privacy disclosures, and distribution decision.

## Results log
| Date | Device / Android | Build | Tester | Result / issues |
|---|---|---|---|---|
| 2026-11-27 | Details not recorded | Not confirmed | Owner | Owner reports real-device testing completed; all OK. Record exact device/build and test evidence when available. |
