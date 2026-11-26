# Exelix Roadmap

## Primary focus: Android APK reconstruction
1. Establish native Kotlin/Jetpack Compose project with original package identity.
2. Build a local Room vocabulary database and native CRUD screens.
3. Reconstruct review scheduling, mastery, history, and due-state behavior from APK evidence.
4. Implement import/export, backup/restore, and robust duplicate detection.
5. Build debug APK in CI; verify install and runtime on a device/emulator before release.
6. Investigate APK resource mappings and UI details; document uncertainties.

## Deferred
- Browser/PWA feature work and public Pages hosting.
- Cloud sync or account features until offline core is stable and privacy requirements are clear.

## Evidence rule
Original APK is evidence, but reconstructed source is not original source. Never claim builds, installation, feature parity, or UI fidelity unless verified.
