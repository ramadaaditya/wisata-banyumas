@file:Suppress("UnstableApiUsage")

include(":feature:profile")


include(":feature:resetpassword")


include(":feature:register")


include(":feature:login")


include(":feature:admin")


include(":feature:search")


include(":feature:dashboard")


include(":feature:bookmarks")


rootProject.name = "WisataBanyumas"
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
        google()
        mavenCentral()
    }
}

rootProject.name = "Wisata Banyumas"
include(":app")
include(":core:data")
include(":core:designsystem")
include(":core:model")
include(":core:common")
