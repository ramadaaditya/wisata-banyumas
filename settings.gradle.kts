pluginManagement {
    includeBuild("build-logic")
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
        google{
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "wisatabanyumas"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":feature:auth")
include(":feature:profile")
include(":feature:admin")
include(":feature:search")
include(":feature:dashboard")
include(":feature:bookmarks")
include(":app")
include(":core:data")
include(":core:designsystem")
include(":core:common")

include(":core:network")
include(":feature:detail")
include(":core:domain")
include(":core:model")
