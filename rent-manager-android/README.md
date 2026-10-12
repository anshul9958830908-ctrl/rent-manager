# Rent Manager Android App

This Android app wraps the existing Google Apps Script web app as a mobile WebView.

## Build
1. Open this project in Android Studio (Ladybug or newer recommended).
2. Let Gradle sync.
3. Choose **Build > Build APK(s)**.
4. The debug APK will be under `app/build/outputs/apk/debug/`.

### GitHub build
Push the project to GitHub. The included workflow under `.github/workflows/build-apk.yml` builds the debug APK and uploads it as a workflow artifact.

## App
- Name: Rent Manager
- URL: the supplied Google Apps Script deployment
- Internet permission enabled
- Pull to refresh
- Android back navigation
- Offline screen
- HTTPS-only web content
