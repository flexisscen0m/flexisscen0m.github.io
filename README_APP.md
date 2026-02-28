# Dab Tracker - Personal Dabbing Session Tracker

Native Android app built with Kotlin, Jetpack Compose, and Material 3 for tracking dabbing sessions.

## Features

### Extract Library
- Add, edit, and delete extracts with name, category, strain, weight, and notes
- Built-in categories: Rosin, Live Resin, BHO, Bubble Hash, plus custom categories
- Auto-calculated remaining weight from logged sessions
- Filter by category, sort by name/date/remaining weight

### Session Logging
- Select extract from library (auto-deducts weight)
- Track dab weight (0.01g precision), temperature, and device used
- Customizable device/method list
- Auto-timestamped sessions

### Session Rating
- Rate each session on 5 dimensions (1-5 stars):
  - Flavor, Vapor Quality, Effect Intensity, Effect Duration, Overall Satisfaction
- Free-text review notes

### Home Screen Widget
- Glance API widget showing active extract + remaining weight
- Last session summary with time ago
- Tap to open app for quick logging

### Data Export/Import
- Export full database as JSON
- Import from JSON to restore data
- Share via Android share sheet

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3 with dynamic color theming
- **Architecture:** MVVM with clean architecture layers (data → domain → presentation)
- **Database:** Room (SQLite) with migrations support
- **Navigation:** Jetpack Navigation Compose
- **State:** ViewModels + StateFlow
- **Widget:** Glance AppWidget API
- **Serialization:** Kotlinx Serialization (for JSON export/import)
- **Min SDK:** 26 (Android 8.0) | **Target SDK:** 35 (Android 15)

## Project Structure

```
app/src/main/java/com/dabtracker/app/
├── data/
│   ├── local/
│   │   ├── entity/          # Room entities
│   │   ├── dao/             # Room DAOs
│   │   └── AppDatabase.kt   # Room database
│   ├── repository/          # Repository implementations
│   └── export/              # JSON export/import
├── domain/
│   ├── model/               # Domain models
│   └── repository/          # Repository interfaces
├── presentation/
│   ├── navigation/          # Nav graph + Screen routes
│   ├── theme/               # Material 3 theme
│   ├── components/          # Reusable composables
│   └── screens/
│       ├── home/            # Dashboard
│       ├── extract/         # Extract library CRUD
│       ├── session/         # Session log, rate, history
│       └── settings/        # Settings + data management
├── widget/                  # Glance home screen widget
├── DabTrackerApplication.kt
└── presentation/MainActivity.kt
```

## Build

### Requirements
- Android Studio Iguana or newer
- Android SDK 35
- JDK 17

### Instructions

1. Open project in Android Studio
2. Sync Gradle (File > Sync Project with Gradle Files)
3. Build (Build > Make Project)
4. Run on device/emulator (Run > Run 'app')

### Command Line
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Widget Setup

1. Install the app
2. Long-press on home screen
3. Add widget > Dab Tracker Widget
4. Widget shows active extract and last session info
