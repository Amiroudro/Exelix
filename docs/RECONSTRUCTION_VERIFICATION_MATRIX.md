# Reconstruction Verification Matrix

This document turns the APK parity audit into repeatable checks. It is intentionally evidence-based: a check is not marked passed merely because a feature exists in code.

## Test environment record

Record for each release candidate:
- Candidate commit and CI run URL
- APK version/name and SHA-256
- Device model and Android version
- Test date
- Tester and outcome (Pass / Fail / Blocked)
- Screenshots or screen recordings for failures and critical flows

## Core offline workflows

| ID | Scenario | Acceptance criteria | Status |
|---|---|---|---|
| CORE-01 | Launch with empty database | No crash; empty state is clear; navigation available | Retest |
| CORE-02 | Add vocabulary | Required fields validated; saved word appears in list and count | Retest |
| CORE-03 | Edit vocabulary | Changes persist after navigating away and relaunching | Retest |
| CORE-04 | Delete vocabulary | Confirmation/feedback appropriate; deleted item absent after relaunch | Retest |
| CORE-05 | Search | Search matches intended fields; empty/no-match states behave cleanly | Retest |
| CORE-06 | Vocabulary details | Bangla meaning, definition, POS, examples, synonyms, antonyms, collocations, source and tags render without layout issues when populated/empty | Retest |
| CORE-07 | Offline use | Core CRUD and review workflows remain usable with connectivity disabled | Retest |

## Review, history, and statistics

| ID | Scenario | Acceptance criteria | Status |
|---|---|---|---|
| REV-01 | Review queue | Due items load; empty queue communicates next action | Retest |
| REV-02 | Again response | Item is rescheduled using documented reconstruction interval; queue updates | Retest |
| REV-03 | Remembered response | Item advances to expected reconstructed interval; queue updates | Retest |
| REV-04 | History | Review actions appear with correct date and word linkage | Retest |
| REV-05 | Dashboard/analytics | Counts and recall summaries agree with underlying records | Retest |
| REV-06 | Daily goal | Setting persists and dashboard progress reflects it | Retest |
| REV-07 | Reminder/worker behavior | Only mark pass after worker/notification behavior has been implemented and tested; currently a parity gap | Blocked |

## Import and export

| ID | Scenario | Acceptance criteria | Status |
|---|---|---|---|
| IO-01 | CSV import preview | Preview before commit; validation errors visible; no partial import on invalid input | Retest |
| IO-02 | TSV import | Tabs recognized; fields with quotes/newlines parsed correctly | Retest |
| IO-03 | Header mapping | Mapping changes preview values correctly | Retest |
| IO-04 | Duplicate handling | Duplicates identified and behavior explained before commit | Retest |
| IO-05 | Import limits | Oversized inputs fail gracefully without app instability | Retest |
| IO-06 | JSON backup v2 | Backup contains vocabulary, review history, daily goal, theme | Retest |
| IO-07 | JSON backup v1 restore | Legacy vocabulary-only backup restores | Retest |
| IO-08 | Restore merge | Duplicate vocabulary is handled; restored review records map to local word IDs | Retest |
| IO-09 | XLSX | Spreadsheet import/export absent or unverified; do not pass | Gap |
| IO-10 | Markdown export | New human-readable Markdown exporter creates sorted word headings and labeled fields; round-trip test covers escaping and metadata; verify on device | Retest (CI passed; device retest pending) |
| IO-11 | Markdown import | Not implemented; no import preview yet | Gap |

## Settings, navigation, and data safety

| ID | Scenario | Acceptance criteria | Status |
|---|---|---|---|
| UX-01 | Navigation | Every screen reachable; back navigation returns to sensible prior state | Retest |
| UX-02 | Theme | Selected theme applies consistently and survives relaunch | Retest |
| UX-03 | Large text/accessibility | Text remains readable; no essential content clipped | Retest |
| DATA-01 | Process restart | Saved data survives force-stop and relaunch | Retest |
| DATA-02 | Backup/restore | Restore can be completed without losing existing entries unexpectedly | Retest |
| DATA-03 | Corrupt backup | Invalid JSON/schema rejected with clear feedback; app remains usable | Retest |
| DATA-04 | Empty/large datasets | List and analytics remain stable with no words and with a large test dataset | Retest |

## Release gates

- [ ] Candidate is built from a recorded commit; CI is green.
- [ ] Device test evidence is tied to the exact candidate SHA/build.
- [ ] Known APK parity gaps are listed in release notes.
- [ ] Release application ID decision is documented.
- [ ] Release keystore is created and safely backed up by the owner.
- [ ] GitHub signing secrets are configured; signing workflow is validated.
- [ ] APK and AAB signatures verified.
- [ ] Privacy, distribution, versioning, and support notes reviewed.

## Current known blockers

- Original source and signing certificate were not recovered.
- XLSX is not implemented; Markdown export/import now exists in a reconstructed format, but original APK compatibility remains unresolved.
- Feed/news sync, tags/batch actions, and review-worker/reminder behavior need verification.
- Prior device testing was owner-confirmed, but test evidence was not tied to a candidate SHA in this repository.
- Release signing is owner-controlled and not configured in CI yet.

See [APK parity audit](APK_PARITY_AUDIT.md) and [release QA checklist](RELEASE_QA.md).
