# Recovery facts and boundaries

## Verified observations
- The supplied APK is package `com.depu.vocabapp`.
- APK inventory includes three DEX files, image resources, fonts, and XML.
- Recovered class names and resource strings indicate Kotlin/Jetpack Compose and Room-based vocabulary features.
- The original source code has not been recovered.
- This repository's browser app is a new reconstruction, not the original app.

## Reconstruction limits
- Review intervals are simplified and approximate.
- Android build has not been verified in this workspace.
- APK image assets have not been reliably mapped to original screens.
- Online feeds, notifications, and Excel-specific integrations remain unimplemented.

## Handling rule
Keep claims evidence-based. Mark features as reconstructed or untested until validated.

## Browser reconstruction status (2026-11-27)
- Browser-first vocabulary app is in `index.html`; local persistence, import/backup, and basic review logic are reconstructed.
- Responsive UI styling has been refined toward a clean editorial study-workspace look with a restrained Bangladesh-inspired green/red accent.
- Recent functional improvements: due-now review filter; search across part of speech, synonyms, antonyms, collocations, source, and tags; expanded card/flashcard detail display; CSV/TSV/Markdown parsing with quoted delimiters; header-based import mapping for metadata; normalized duplicate checks for legacy backup records.
- Repository readback confirms the updated HTML and PWA files are committed.
- GitHub Actions runners are now available again. The latest validation workflow completed successfully, including inline JavaScript syntax checks and import-parser smoke tests for quoted CSV, TSV, Markdown tables, and plain-text separators (run #25, commit `480a584`). The Pages workflow's static build and artifact upload succeeded, but the deploy job failed; Pages hosting is therefore not yet verified. The exact deploy error is not accessible through the current connector.
- Live hosting, browser rendering, installability, and offline behavior have not been tested; do not treat them as verified.
