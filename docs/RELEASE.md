# Exelix Android Release Checklist

## Release identity
- Reconstructed application ID: `com.depu.vocabapp.rebuild` (kept separate from original `com.depu.vocabapp`).
- Release version currently set to `1.0.0`, version code `1`.
- This is a reconstruction, not recovered original source or verified feature parity.
- **Choose the permanent production application ID before publishing.** The current `com.depu.vocabapp.rebuild` ID is provisional; changing package ID after publication creates a different app. It cannot be signed as an update to the original package without the original app's signing key.
- Automatic Android backup is disabled. Use the in-app JSON export before uninstalling or moving devices; the JSON export currently contains vocabulary only, not review history or preferences.

## CI artifacts
GitHub Actions builds and tests the project and uploads:
- Debug APK
- Unsigned Release APK
- Unsigned Release Android App Bundle (AAB)

Unsigned artifacts are build outputs, not installable production releases. A production install requires signing.

## Signing (owner-controlled)
1. Create a private release keystore locally using Android Studio's Generate Signed Bundle/APK flow or the Android SDK `keytool`.
2. Keep the keystore, passwords, and recovery copies private. Do not commit them or paste them into chat.
3. To enable CI signing, add these repository Actions secrets (Settings → Secrets and variables → Actions): `EXELIX_RELEASE_KEYSTORE_BASE64`, `EXELIX_RELEASE_STORE_PASSWORD`, `EXELIX_RELEASE_KEY_ALIAS`, and `EXELIX_RELEASE_KEY_PASSWORD`. The workflow decodes the keystore into the temporary runner directory, signs Release APK/AAB, and runs signature verification. Without these secrets, CI intentionally produces unsigned artifacts.
4. Keep the signing key and passwords backed up securely; losing the key can prevent updates to the same app identity.
5. Verify the signed artifact's signature, version, package ID, and hash before sharing.

## Mandatory QA before distribution
- Install on a physical Android device without uninstalling/replacing the original app.
- Confirm launch, navigation, database persistence, add/edit/delete/search.
- Confirm review Again/Remembered, due scheduling, history and analytics.
- Test CSV/TSV import with duplicates and malformed files.
- Export and restore vocabulary backup; confirm the vocabulary-only limitation.
- Verify settings persist, Bangla text renders, keyboard/layout is usable, and app survives force-close/relaunch.
- Confirm no data loss on upgrade from the prior reconstruction build.
- Record device model, Android version, build ID, and pass/fail. Do not label release production-ready until this QA is actually performed.

## Current limitation
The developer environment cannot perform physical-device QA or sign with the owner's production key. CI can compile, test, and verify unsigned build artifacts.
