# 📍 MemoryLane

An expressive, location-based Android app that turns your captured photos into interactive geographic memories on a map. Built using **Jetpack Compose**, **Clean Architecture**, **Mapbox SDK**, and **Supabase**.

---

## 🌟 Key Features

- **🗺️ Interactive Location Map**: View your captured photo memories pinned directly onto a 3D Mapbox map with custom image markers and location chips.
- **📸 High-Quality Image Compression**: Camera photos are automatically compressed by ~95% in file size (from 10MB down to ~350KB) with zero perceptible quality loss before cloud upload.
- **🖼️ Memory Gallery**: Browse all your captured memories in a responsive grid layout powered by **Coil 3** dual-layer (RAM & Disk) image caching.
- **✨ Shared Element Transitions**: Smooth, expressive Material 3 morphing animations when opening photo details from the gallery.
- **📍 Automated Reverse Geocoding**: Automatically tags captured photos with place names (e.g., *"Central Park, New York"*) using GPS coordinates.
- **🔐 Secure Cloud Sync & Auth**: Synchronized with **Supabase** (PostgreSQL + Cloud Storage) using Row-Level Security (RLS) and short-lived signed URLs.
- **🔑 Flexible Authentication**: Supports Google Sign-In, Email/Password, OTP verification, and custom profile onboarding.

---

## 🏗️ Architecture

MemoryLane strictly follows **Clean Architecture** principles and **MVI / MVVM** patterns:

```
app/src/main/java/com/ghostbug/memorylane/
├── core/
│   ├── navigation/        # Navigation 3 setup, NavDisplay, and Route keys
│   └── permissions/       # Runtime Camera & Location permission handling
└── features/
    ├── capture/           # Camera capture, JPEG compression, and upload repository
    ├── gallery/           # Memories gallery grid and full-screen image viewer
    ├── location/          # Mapbox map, custom markers, and location bottom sheet
    ├── profile/           # User onboarding, user profile, and settings
    └── signUp/            # Auth repository, Google ID token sign-in, and OTP
```

### Layer Responsibilities:
1. **Data Layer (`data/`)**: Supabase DTOs, Postgrest queries, Storage uploads, and Image Compression.
2. **Domain Layer (`domain/`)**: Pure Kotlin data models (`Memories`, `Profile`), repository interfaces, and use cases.
3. **Presentation Layer (`presentation/`)**: Jetpack Compose UI, ViewModels with `StateFlow` and single-shot `SharedFlow` UI events.

---

## 🛠️ Tech Stack & Libraries

| Component | Library / Framework |
| :--- | :--- |
| **Language** | Kotlin 2.x |
| **UI Toolkit** | Jetpack Compose + Material 3 Expressive |
| **Navigation** | Jetpack Navigation 3 (`androidx.navigation3`) |
| **Maps & Search** | Mapbox Maps SDK v11 & Mapbox Search |
| **Location** | Google Play Services `FusedLocationProviderClient` |
| **Image Loading** | Coil 3 (`coil-compose` & `coil-network-okhttp`) |
| **Backend & Storage** | Supabase Kotlin SDK v3.8.0 (`postgrest-kt`, `storage-kt`, `auth-kt`) |
| **Dependency Injection** | Koin |
| **Serialization** | `kotlinx-serialization-json` & `kotlinx-datetime` |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug / Jellyfish (2024.1+) or newer
- **JDK**: Version 11 or higher
- **Android SDK**: `minSdk 24` (Android 7.0+), `targetSdk 36`

### Environment Configuration

Create a `local.properties` file in your root project directory with your credentials:

```properties
SUPABASE_URL=https://your-supabase-project.supabase.co
SUPABASE_PUBLISHABLE_KEY=your-supabase-publishable-key
GOOGLE_CLIENT_ID=your-google-oauth-client-id
WEB_CLIENT_ID=your-web-client-id
```

### Building the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/MemoryLane.git
   cd MemoryLane
   ```

2. Open the project in **Android Studio**.

3. Sync Gradle and build the app:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📱 License

Distributed under the MIT License.
