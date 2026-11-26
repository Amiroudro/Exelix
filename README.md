# Exelix

Exelix is a personal vocabulary-learning app. This repository is the clean home for the recovered project and its browser-first reconstruction.

## Project status

- Original Android APK: available in the recovery workspace; the original Kotlin source has **not** been recovered.
- Android reconstruction: evidence-based project scaffold exists in the recovery workspace, but it has not been built.
- Browser edition: offline-first single-page app under development.
- Never represent a reconstruction as the original source or claim an Android build passed unless it has been run and verified.

## Browser Edition

Open `index.html` in a browser. The browser edition is designed for local use and stores data in the browser's local storage. Use the export/backup function regularly; clearing browser data can erase locally stored words.

## Roadmap

1. Establish repository and documentation.
2. Build and test the offline browser vocabulary core.
3. Recreate import/export formats with preview and duplicate detection.
4. Improve review scheduling and progress reporting.
5. Recover and map APK assets only where attribution and mapping are supported by evidence.
6. Reconstruct Android source carefully, documenting uncertainties and testing before release.
7. Add automated checks, release notes, and a reproducible deployment.

## Privacy

This browser prototype does not require a server or account. Vocabulary data is kept locally in the browser unless the user exports it.

## License

License has not yet been chosen. Do not assume third-party asset licenses.

## Progressive web app (PWA)

Exelix includes a web manifest, app icon, and service worker for offline-shell caching. It requires HTTPS (or localhost) to activate the service worker. Hosting and installability are not yet confirmed because GitHub Actions deployment runs are currently failing before steps start. See [PWA notes](docs/PWA_NOTES.md).
