# Exelix browser app: PWA milestone

## What changed
- Added `manifest.webmanifest` for standalone app display and app metadata.
- Added `icon.svg` as a vector app icon.
- Added `sw.js` with a small cache-first offline shell for the app HTML and manifest.
- Registered the service worker from `index.html` on HTTP(S) hosts.

## Limits and testing status
- This is a progressive-web-app foundation, not proof that the app is installed or hosted.
- Service workers require a secure origin (HTTPS, or localhost).
- App data remains in browser local storage on the device. It is not synced to GitHub or a cloud database.
- GitHub Actions runs have been created, but they currently finish as **failed with zero recorded steps and no runner assigned**. That indicates the jobs did not reach execution; the logs available through this connection do not explain why. Do not report CI as passing.
- The Pages deployment workflow has also failed before a step is recorded. A working public HTTPS deployment is not yet confirmed.

## Next
1. Diagnose why Actions jobs are failing before runner assignment.
2. Confirm repository Pages configuration and deployment permissions.
3. Once hosting works, test the live app on phone and desktop.
4. Add real browser-level interaction tests and a small set of import fixtures.
