# Exelix Android Device QA Checklist

Use the reconstruction app only for testing. Keep the original app installed and untouched. Do not uninstall the original or clear its data.

## Before testing
- [ ] Confirm the installer file came from the project's GitHub Actions artifact for the expected build.
- [ ] Ensure the original app remains installed and its data is backed up through its own supported method.
- [ ] Install the reconstruction APK only if Android and a trusted adult helping you consider the source safe. Never bypass warnings casually.
- [ ] Confirm the reconstruction appears as a separate app (application ID is `com.depu.vocabapp.rebuild`).

## Basic UI and persistence
- [ ] App launches without crash.
- [ ] Home, Library, Review, History, Analytics, Backup, and Settings tabs switch correctly.
- [ ] Add a test word, close/reopen app, and verify it persists.
- [ ] Edit test word and confirm changes.
- [ ] Search for word, Bangla meaning, and tags.
- [ ] Delete only the test entry and confirm it disappears.

## Review behavior
- [ ] Review due word; reveal answer.
- [ ] Choose Again; verify history entry and due scheduling.
- [ ] Choose Remembered on another test word; verify history and next due date.
- [ ] Session counter increments once for each review response.
- [ ] Counter survives tab switching during this app session.
- [ ] Empty review queue displays a clear message.

## Import and export
- [ ] Import a small CSV with word, Bangla meaning, definition, and example.
- [ ] Review the preview and duplicate counts before confirming.
- [ ] Import a TSV file.
- [ ] Verify malformed or oversized files produce understandable errors.
- [ ] Export JSON backup and verify Android reports the file saved.
- [ ] Restore into a separate/fresh reconstruction install or test environment; verify duplicates are skipped and data is preserved.

## Analytics / Settings
- [ ] All-time review and recall values match review history.
- [ ] Active days and streak are correct for test dates.
- [ ] Daily activity chart matches review events.
- [ ] Set daily goal and verify Home progress updates.
- [ ] Daily goal persists after closing/reopening.
- [ ] Switch System/Light/Dark themes and verify persistence.
- [ ] Review-schedule explanation is accurate and clearly labels approximate intervals.

## Dashboard / analytics
- [ ] Word count and due count match test data.
- [ ] Today's review and remembered counts match review actions.
- [ ] Seven-day review count and recall percentage match expected events.
- [ ] No-review empty-state is understandable.

## Layout/accessibility observations
- [ ] Test portrait orientation and scrolling.
- [ ] Check text readability and Bangla rendering.
- [ ] Check text fields with keyboard open and close.
- [ ] Check buttons do not become hidden behind bottom navigation.
- [ ] Record crashes, confusing labels, or layout problems with screenshots and steps.

## Outcome
Record date, Android version, device model, build number, pass/fail for each item, and any issue details. Do not claim physical-device validation until these checks have actually been performed.
