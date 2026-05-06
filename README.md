# Car-Man

Car-Man was built as a Kotlin Multiplatform project demonstrating shared mobile development across Android and iOS. The app includes persistent local storage, network requests, maps, camera support, local notifications, background work, DataStore settings, and runtime translation support.

## Overview

Car-Man helps users keep track of their cars in one place. Users can add vehicles, view vehicle details, log mileage, record maintenance events, save mechanic information, and search for nearby automotive-related locations using map functionality.

The project targets:

- Android
- iOS

The shared application code lives in `composeApp`, while the native iOS entry point lives in `iosApp`.

## Features

- Add and manage cars
- View detailed information for each vehicle
- Log mileage records
- Add maintenance events
- Track service and repair history
- Save mechanic contact information
- View map-based automotive locations
- Capture or attach vehicle-related images
- Store app settings locally
- Shared Compose Multiplatform UI for Android and iOS

## Project Requirements Met

This project satisfies the required technical features as follows:

### 1–2. Database with 3+ Entity Types

Car-Man uses a Room Kotlin Multiplatform database with three persistent entity types:

- `CarEntity`

- `MaintenanceEventEntity`

- `MechanicEntity`

These entities are stored locally and persist across app restarts on both Android and iOS.

### 3. Ktor for Network Requests

The app uses Ktor in `OverpassService` to query the Overpass API. This is used to find nearby car repair shops based on the user's current map view.

Discovered repair shops are displayed as pins on the map.

### 4. DataStore with 6+ Values

Car-Man uses DataStore to store user settings and preferences. The app stores seven values:

- Owner name

- Dark theme toggle

- Notifications enabled

- Default oil change interval

- Default tire rotation interval

- Default brake service interval

- Language preference

These settings persist between app sessions.

### 5. Camera

The Add Car screen supports taking a photo of a vehicle using `ActivityResultContracts.TakePicture`.

The captured photo is stored and displayed throughout the app using Coil's `AsyncImage`.

### 6. Maps

The Map screen embeds a MapLibre map.

The map displays:

- Saved mechanic pins in blue

- Overpass-discovered mechanic pins in red

Users can tap pins to view mechanic details and save discovered mechanics.

### 7. Local Notifications

The app uses `MaintenanceWorker`, a WorkManager `CoroutineWorker`, to send local maintenance reminders.

The worker runs weekly in the background and sends a notification reminding the user to log their mileage.

### 8. Full Translation

All UI strings are externalized into Compose Multiplatform string resources.

The app includes complete translations for:

- English

- German

Users can switch languages at runtime from the Settings screen, and the UI rebuilds immediately using the selected language.

### 9. Background Tasks

Background work is handled by `MaintenanceWorker`, implemented as a WorkManager `CoroutineWorker`.

On Android, this worker runs in the background to deliver weekly mileage reminder notifications.

## Tech Stack

- Kotlin Multiplatform
- Compose Multiplatform
- Material 3
- AndroidX Lifecycle
- AndroidX Navigation Compose
- Room Database
- SQLite
- DataStore Preferences
- Ktor Client
- Kotlinx Serialization
- Kotlinx DateTime
- Coil
- MapLibre Compose
- KSP
- Gradle Kotlin DSL

## Project Structure

```text
Car-Man/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/
│   │   │   ├── kotlin/edu/moravian/csci395/carman/
│   │   │   │   ├── App.kt
│   │   │   │   ├── CameraCapture.kt
│   │   │   │   ├── Platform.kt
│   │   │   │   ├── data/
│   │   │   │   ├── screens/
│   │   │   │   └── theme/
│   │   │   └── composeResources/
│   │   ├── androidMain/
│   │   ├── iosMain/
│   │   └── commonTest/
│   └── build.gradle.kts
├── iosApp/
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── gradlew.bat
```

The shared `commonMain` source set contains the main app code, including `App.kt`, shared camera and platform declarations, the `data` package, the `screens` package, and the theme package.

## Main Screens

The app includes screens and view models for:

- Home
- Cars list
- Add car
- Car details
- Add maintenance event
- Log mileage
- Map
- Settings

These files are located in:

```text
composeApp/src/commonMain/kotlin/edu/moravian/csci395/carman/screens
```

## Data Layer

The data layer is located in:

```text
composeApp/src/commonMain/kotlin/edu/moravian/csci395/carman/data
```

It includes Room entities, DAOs, database setup, settings, and an Overpass service for map-related location data.

Important files include:

- `CarEntity.kt`
- `CarDao.kt`
- `MaintenanceEventEntity.kt`
- `MaintenanceEventDao.kt`
- `MechanicEntity.kt`
- `MechanicDao.kt`
- `CarManDatabase.kt`
- `CarManSettings.kt`
- `OverpassService.kt`

## Requirements

Before running the project, install:

- Android Studio
- JDK 11 or newer
- Xcode, for iOS builds
- Kotlin Multiplatform support in your IDE
- Android SDK matching the project compile SDK
- An Android emulator or physical Android device
- An iOS simulator or physical iOS device

## Getting Started

Clone the repository:

```bash
git clone https://github.com/ranayum/Car-Man.git
cd Car-Man
```

Open the project in Android Studio.

Allow Gradle sync to complete before running the application.

## Build and Run Android

To build the Android debug version on macOS or Linux:

```bash
./gradlew :composeApp:assembleDebug
```

To build the Android debug version on Windows:

```powershell
.\gradlew.bat :composeApp:assembleDebug
```

You can also run the Android app directly from Android Studio by selecting the Android run configuration and pressing Run.

## Build and Run iOS

To run the iOS app:

1. Open the project in Android Studio and let Gradle sync.
2. Open the `iosApp` directory in Xcode.
3. Select an iOS simulator or connected device.
4. Press Run in Xcode.

The iOS application uses the shared Compose framework generated from the `composeApp` module.

## Gradle Configuration

The main shared application module is `composeApp`. It applies the following major plugins:

- Kotlin Multiplatform
- Android Application
- Compose Multiplatform
- Compose Compiler
- Kotlin Serialization
- KSP
- AndroidX Room

The project configures Android and iOS targets, including physical iOS devices and iOS simulators.

## Database

Car-Man uses Room for local database storage. The database stores information about cars, maintenance events, and mechanics.

Room schema files are generated under:

```text
composeApp/schemas
```

## Maps and Location Search

The app includes map functionality through MapLibre Compose and an Overpass service. This allows the app to work with map-based automotive location data, such as nearby mechanics or service-related points of interest.

## Settings Storage

App preferences are handled with DataStore Preferences. This is used for storing lightweight local settings across app sessions.

## Testing

To run tests:

```bash
./gradlew test
```

To run all checks:

```bash
./gradlew check
```

## Common Troubleshooting

### Gradle sync fails

Try cleaning and syncing again:

```bash
./gradlew clean
```

Then reopen Android Studio and sync Gradle.

### Android build fails because of SDK version

Make sure your installed Android SDK supports the compile SDK version configured in the project.

### iOS build fails in Xcode

Make sure the shared framework has been generated by Gradle and that Xcode is opening the `iosApp` project directory.

### Room or KSP errors

Because this project uses Room with Kotlin Multiplatform and KSP, make sure the Kotlin, KSP, and Room versions remain compatible when updating dependencies.

## Future Improvements

Potential future features include:

- Push notifications for upcoming maintenance
- Cloud backup and sync
- Exporting maintenance history
- Fuel economy tracking
- More detailed mechanic profiles
- Photo gallery for each vehicle
- Reminder scheduling for inspections, oil changes, and registration renewals

## Collaborators

- Rana Yum
- Sean Creveling

