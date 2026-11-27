# Original APK Feature-Parity Audit

## Evidence scope and limits
Original APK: `Exelix_1.0.apk`, package `com.depu.vocabapp`.

Evidence retained from Phase 5 is an APK/DEX inventory, app-owned class/method identifier inventory, feature strings, and recovered database schema. It does **not** include full decompiled source or recovered method bodies. Names below are indicators of feature surface, not proof of exact behavior. This audit compares evidence with the current reconstruction and identifies verification gaps.

## Feature matrix

| Original APK evidence | Reconstruction status | Notes / next verification |
|---|---|---|
| Vocabulary entities, DAO, repository, database | Implemented (Room) | Verify exact schema semantics and migrations against recovered schema evidence. |
| `addWord`, `updateWord`, `deleteWord` | Implemented | Owner reports device testing OK. |
| `previewImport`, `commitImport` | CSV/TSV import implemented | Original formats and UI parity need evidence. |
| `exportBackup`, `restoreBackup`, BackupManager | JSON v1/v2 reconstruction implemented | Current v2 includes words, review history, goal, theme; exact original backup format unknown. |
| `exportToMarkdown`, `importMarkdown`, MarkdownImporter/Exporter | Markdown export + matching import/preview implemented in reconstruction | Portable format is newly designed and not original-compatible verified; tests added, CI status pending.
| `exportToXlsx`, `importXlsx`, XlsxImporter/Exporter | Not implemented/confirmed | Spreadsheet import/export is a notable parity gap. |
| Tag handling and batch deletion method identifiers | Not fully verified | Audit current UI/DAO for bulk delete and tag operations. |
| Due-review queries, DueReviewWorker | Review queue + approximate scheduler implemented | Worker/reminder semantics not recovered or implemented/confirmed. |
| FeedClient, FeedManifest, FeedSyncer, FeedSyncWorker | Not implemented | Feed/news synchronization is a known gap; network behavior and source requirements require evidence. |
| SettingsRepository | Basic settings implemented | Current daily goal and theme; original setting set/behavior unknown. |
| VocabApplication | Not confirmed | App-level initialization/DI behavior not reconstructed from method bodies. |

## High-priority parity work
1. **Spreadsheet support (XLSX):** determine expected columns and implement import/export only after reviewing original strings/schema or user-provided behavior screenshots.
2. **Markdown import/export:** Markdown export now exists in reconstruction; verify original format and add safe preview-based Markdown import.
3. **Feeds/news synchronization:** identify expected source configuration and privacy/network behavior; do not invent endpoints.
4. **Batch actions and tags:** verify current native app and original evidence for bulk operations.
5. **Review reminders/workers:** determine notification permissions, reminder timing, and worker conditions from evidence.
6. **Schema/data compatibility:** compare reconstructed Room schema with recovered SQL and inventory; document differences.

## Release position
The reconstruction is owner-reported device-tested and CI-built, but this matrix is **not a parity certification**. The original APK source and signing certificate were not recovered. Do not claim feature equivalence until the evidence gaps are closed.
