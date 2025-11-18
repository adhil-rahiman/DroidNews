pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DroidNews"
include(":app")

// Common utilities
include(":common:kotlin")
include(":common:android")

// Core layers
include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:analytics")

// News vertical
include(":domain:news")
include(":data:news")
include(":feature:news")
