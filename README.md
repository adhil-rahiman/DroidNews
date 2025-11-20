# DroidNews

Android news app with clean architecture and Jetpack Compose.

## Tech Stack

**Language & UI**
- Kotlin
- Jetpack Compose
- Material 3

**Architecture**
- Clean Architecture (domain, data, presentation)
- MVVM pattern
- Multi-module structure

**Libraries**
- Hilt (dependency injection)
- Retrofit + OkHttp (networking)
- Room (local database)
- Paging 3 (pagination)
- Coil (image loading)
- Kotlinx Serialization (JSON parsing)
- Navigation Compose (navigation)
- WorkManager (background tasks)
- Coroutines + Flow (async)

## Setup

**1. Get API Key**
- Sign up at [gnews.io](https://gnews.io/)
- Free tier: 100 requests/day

**2. Add API Key**

Create `local.properties` in project root:
```properties
NEWS_API_KEY=your_api_key_here
API_BASE_URL=https://gnews.io/api/v4/
```

**3. Build & Run**
```bash
./gradlew installDebug
```

## Modules

```
app/                    # Main app, Hilt setup, WorkManager
├── feature/
│   └── news/          # UI screens, ViewModels, navigation
├── domain/
│   └── news/          # Business logic, use cases, models
├── data/
│   └── news/          # Repository, data sources, paging
├── core/
│   ├── network/       # Retrofit, API, interceptors
│   ├── database/      # Room, DAOs, entities
│   └── ui/            # Theme, shared components, error handling
└── common/
    ├── kotlin/        # Shared utilities, Result wrapper
    └── android/       # Android utilities
```

## Module Dependencies

```
app
 ├─> feature/news
 │    ├─> domain/news
 │    ├─> data/news
 │    └─> core/ui
 ├─> data/news
 │    ├─> domain/news
 │    ├─> core/network
 │    ├─> core/database
 │    └─> common/kotlin
 ├─> core/network
 │    └─> common/kotlin
 ├─> core/database
 │    └─> domain/news
 └─> core/ui
      └─> common/kotlin
```

**Dependency Rules**
- `domain` has no dependencies (pure Kotlin)
- `data` depends on `domain` + `core`
- `feature` depends on `domain` + `data` + `core/ui`
- `app` wires everything together

## Features

- Browse news by category
- Search articles
- Bookmark articles
- Offline caching (10 min expiry)
- Pull-to-refresh
- Background sync (every 5 min)
- Deep links: `https://droidnews.app/article/{id}`
- Error handling with retry

## Commands

```bash
./gradlew build                    # Build project
./gradlew test                     # Run tests
./gradlew detekt                   # Lint code
./gradlew installDebug             # Install on device
```

## Configuration

**Background Work** (`app/src/.../work/WorkConfig.kt`)
```kotlin
NEWS_REFRESH_INTERVAL_MINUTES = 5L    # Refresh interval
CACHE_EXPIRY_MINUTES = 10L            # Cache expiry
BREAKING_NEWS_INTERVAL_HOURS = 2L     # Notification interval
```

**API Endpoint** (`core/network/.../NetworkConstant.kt`)
```kotlin
GNEWS_API_BASE_URL = "https://gnews.io/api/v4/"
```

## Troubleshooting

**Build fails**
- Check JDK 17 is installed
- Sync Gradle files

**No data loading**
- Verify API key in `local.properties`
- Check internet connection
- API free tier: 100 requests/day

**Network errors**
- App shows Snackbar with retry option
- Check logcat for detailed errors (tag: `ErrorHandler`)

## Project Structure

```
DroidNews/
├── app/                          # Android app module
├── feature/news/                 # News UI
│   ├── ui/
│   │   ├── feed/                # Home screen
│   │   ├── search/              # Search screen
│   │   ├── detail/              # Article detail
│   │   └── bookmarks/           # Bookmarks screen
│   └── viewmodel/               # ViewModels
├── domain/news/                  # Business logic
│   ├── model/                   # Domain models
│   ├── repo/                    # Repository interfaces
│   └── usecase/                 # Use cases
├── data/news/                    # Data layer
│   ├── repo/                    # Repository implementations
│   ├── dataSource/              # Remote & local data sources
│   └── paging/                  # Paging sources
├── core/network/                 # Networking
│   ├── interceptor/             # Network interceptors
│   └── util/                    # API response handler
├── core/database/                # Database
│   ├── dao/                     # Room DAOs
│   └── model/                   # Database entities
└── core/ui/                      # Shared UI
    ├── theme/                   # Material theme
    └── error/                   # Error handling
```
