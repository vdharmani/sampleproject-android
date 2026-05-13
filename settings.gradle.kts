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
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "sampleproject-android"
include(":app")
include(":core:common")
include(":core:datastore")
include(":core:network")
include(":core:database")
include(":core:ui")
include(":feature:auth")
include(":feature:profile")
include(":feature:premium")
include(":feature:legal")
