# Exelix recovery facts and boundaries

## Original APK evidence
- Original package: `com.depu.vocabapp`.
- Original APK SHA-256: `58356d0eb5655dd52fa50cf1723487923e7fd6ada69f8a7709041c7df5899dec`.
- Evidence indicates Kotlin/Jetpack Compose and Room; identifiers and SQL strings were inspected.
- Original source code and original review algorithm were not recovered. Reconstructed scheduler is approximate.

## Native reconstruction
- Kotlin/Compose/Room project in repository.
- App ID `com.depu.vocabapp.rebuild` (keeps original app separate).
- Vocabulary CRUD, search, metadata fields, local database.
- Home overview, library, review, history, backup/transfer.
- Review scheduler approximate: Again ~10 minutes; remembered intervals 1, 3, 7, 14, 30 days.
- JSON backup export and merge restore with duplicate skipping.
- CSV/TSV import with preview, duplicate counts, header/delimiter detection, quote/multiline handling, strict validation, 2 MB input and 10,000-row limits.
- Dashboard today and 7-day review stats, including recall rate.
- Dedicated Analytics screen (all-time metrics, active days, streak, 7-day trends and daily bars).
- Settings: daily goal 5–100, System/Light/Dark theme persistence, schedule explanation and offline/local-data guidance.
- Session review counter added in build #50.

## CI status
- Run #49: success, commit `df683b46`, 7-day analytics.
- Run #50: success, commit `b3bc492d`, session completion counter.
- Build #50 APK was downloaded and archive-inspected. SHA-256: `38988f82c02e5a79ff3676c12bb6f9ef80fbf355b1b28bb59ae4cb9113966725`.
- CI validation is not physical device testing. Runtime, install, accessibility, and data flow still require device verification.

## Latest verified reconstruction (build #76)
- CI run #76, commit `2e2e4abb673e0777af6f1e942e5250f13f3f7268`: success (`testDebugUnitTest assembleDebug`).
- Unit tests passed in CI; debug APK produced and archive integrity checked after download.
- APK: 16,597,359 bytes; SHA-256 `cc129a1e1dc43a9f92a711503040965732f6decd02a39fd4fab906e55a2e7732`.
- Build #76 includes: review-session progress indicator/completion message; compact bottom navigation (Home, Library, Review, History, More); More hub for Analytics, Backup, Settings; More remains selected on secondary pages; calendar-aware study streak that continues from yesterday if today has not been studied yet; tests for streak behavior.
- APK ZIP integrity: passed (`unzip -t` reported no errors).
- Physical-device installation and runtime QA remain unperformed.
- CI workflow was extended after build #76 to assemble the Release variant and upload it as a separate unsigned-release artifact; the new workflow run is pending.

## Release-pipeline verification (build #77)
- CI run #77, commit `b7bbd81927579dc2f991d3bd32565c3ea1609ca2`: success (`testDebugUnitTest assembleDebug assembleRelease`).
- CI tests passed; both Debug and unsigned Release APKs were produced.
- Debug APK: 16,597,496 bytes; SHA-256 `c29509cbc1e8c9f781ed79f58f44ba82992faf32f7a12e57f28b3744f44463f0`; archive integrity passed.
- Unsigned Release APK: 10,854,746 bytes; SHA-256 `38b03062888831a74c0e46e1f241582d7d264c0310a59f16ee4505434629d471`; archive integrity passed.
- These are unsigned release artifacts. Signing with a user-owned private key is still required for distribution/installation as a production release.
- Subsequent commits fix analytics truncation, cap imported file reads, validate backup format/version, clarify backup exclusions, update app label/version, and add unsigned AAB output. Their CI run is pending; do not treat these changes as verified until that run passes.

## Latest verified build #90 (release candidates)
- Run #90, commit `1ff7131250ba38aa2c63e2881c3b1e4bff8e462b`: success. CI ran `testDebugUnitTest assembleDebug assembleRelease bundleRelease`; tests passed.
- Debug APK: 16,597,343 bytes; SHA-256 `5a55ecfc9576028eb0fae54e19829f6e1c85140ce22964da514db8650d593a35`; ZIP integrity passed.
- Unsigned Release APK: 10,854,738 bytes; SHA-256 `4bd9e7986a5eab070477d32e80216a17185fbee760c20e50e1b7038e81a46bf7`; ZIP integrity passed.
- Unsigned Release AAB: 10,524,070 bytes; SHA-256 `45db4fb63354b4c38a0e4a2ba90e2cd39890dd8afe36ff2e82a6d74a8bed300d`; ZIP integrity passed.
- Build #90 includes corrected unbounded analytics, capped input reads, backup validation/limitations notice, app label/version, dashboard launch, and scrollable Home/More/Backup/Analytics/Settings.
- Later commits add optional repository-secret signing and an additional scheduler interval-cap test; the current latest CI run must pass before those changes are considered verified.

## Pending validation
- Install reconstruction beside original without replacing original.
- Test dashboard numbers and date-boundary behavior.
- Add/edit/delete/search and Bangla text display.
- Review Again/Remembered schedule and history records.
- Import representative CSV/TSV files, including duplicates and quoted/multiline fields.
- Export backup, restore into a fresh reconstruction install, and verify content.
- Verify navigation, keyboard, scrolling, and screen-size layout.
- Record issues and fix them before release builds.

## Browser reconstruction
- Web/PWA work is paused by request.

## Safety and data handling
- Do not uninstall or overwrite the original app during reconstruction tests.
- Do not access the original app's private data.
- Clearly label reconstructed behavior and unverified claims.