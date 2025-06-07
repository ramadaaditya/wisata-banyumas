plugins {
    alias(libs.plugins.base.library)
    alias(libs.plugins.base.api)
    alias(libs.plugins.base.firebase)
    alias(libs.plugins.base.hilt)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))
}