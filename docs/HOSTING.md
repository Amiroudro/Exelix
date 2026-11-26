# Hosting Exelix with GitHub Pages

The browser edition is a static, offline-first app. GitHub Pages can host it without a backend.

## Enable once
1. Open repository **Settings → Pages**.
2. Under **Build and deployment**, choose **GitHub Actions** as the source.
3. Save. This repository includes `.github/workflows/deploy-pages.yml`; pushes to `main` trigger deployment.
4. Check **Actions → Deploy Exelix to GitHub Pages** for the published URL.

If Pages is unavailable for a private repository on the current GitHub plan, keep the repository private and use the downloaded HTML locally, or choose a hosting provider that supports private-source deployment. Do not make this repository public just to host the app.

## Privacy and data
The app stores vocabulary in the browser's local storage. Hosting the static HTML does not sync personal vocabulary to GitHub. Each browser/device has its own data store; use the app's backup/export feature to transfer data.

