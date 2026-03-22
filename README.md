# TV Dashboard - Android TV App

A lightweight Android TV app that wraps any HTML dashboard page in a fullscreen WebView. Point it at a local LAN server, GitHub Pages URL, or use the built-in dashboard.

## Features

- **WebView wrapper** — loads any URL (http, https, file://) in fullscreen
- **D-pad optimized** — all navigation works with a TV remote
- **Built-in dashboard** — ships with a default dashboard (links, media, smart home)
- **Settings screen** — change URL, toggle fullscreen, use preset URLs
- **LAN support** — cleartext HTTP allowed for local network servers
- **Persistent config** — remembers your URL between launches

## Quick Start

### Build with Android Studio
1. Open this folder in Android Studio
2. Let Gradle sync
3. Build → Build APK
4. Install the APK on your Android TV via `adb install`

### Build from command line
```bash
cd tv-dashboard-app
./gradlew assembleDebug
# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

### Install on Android TV
```bash
adb connect YOUR_TV_IP:5555
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Usage

1. Launch "TV Dashboard" from your Android TV home screen
2. On first launch, it loads the **built-in dashboard**
3. Press **MENU** or **S** on your remote/keyboard to open Settings
4. Enter your URL:
   - **LAN**: `http://192.168.1.100:8080/tv-dashboard.html`
   - **GitHub Pages**: `https://yourusername.github.io/tv-dashboard/tv-dashboard.html`
   - **Local file**: `file:///sdcard/tv-dashboard.html`
5. Tap **Save & Load**

## Remote Controls

| Key | Action |
|-----|--------|
| Arrow keys | Navigate tiles (D-pad) |
| OK / Enter | Select / activate |
| MENU or S | Open settings |
| R | Reload page |
| BACK | Go back in WebView, or exit |

## Serving Your Dashboard

### From a PC/NAS (LAN)
```bash
cd /path/to/your/dashboard
python3 -m http.server 8080
# Access at http://YOUR_IP:8080/tv-dashboard.html
```

### From GitHub Pages
1. Push `tv-dashboard.html` to a GitHub repo
2. Enable Pages in repo Settings → Pages → Deploy from main
3. Access at `https://username.github.io/repo-name/tv-dashboard.html`

## Customization

Edit `app/src/main/assets/default-dashboard.html` to change the built-in dashboard.
The app's theme colors can be adjusted in `res/values/styles.xml`.

## Requirements

- Android 5.0+ (API 21)
- Android TV or device with Leanback launcher
- Android Studio or Gradle for building
