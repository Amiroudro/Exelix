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