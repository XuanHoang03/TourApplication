// ────────────────────────────────────────────────────────────────────────────────
// settings.gradle.kts – TourApplication
// ────────────────────────────────────────────────────────────────────────────────

pluginManagement {
    repositories {
        // Google Maven – chỉ lấy các nhóm (group) Android / Google / AndroidX
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }

        // Kho Maven Central chung
        mavenCentral()
        maven { url = uri("https://api.mapbox.com/downloads/v2/releases/maven") }
        // Kho plugin Gradle chính thức
        gradlePluginPortal()
    }
}

// ────────────────────────────────────────────────────────────────────────────────

dependencyResolutionManagement {
    // Chặn khai báo repositories trong từng module – mọi repo phải nằm ở đây
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://mapbox.bintray.com/mapbox") }
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        }
    }
}
rootProject.name = "TourApplication"
include(":app")
