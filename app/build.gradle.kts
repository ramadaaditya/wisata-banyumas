plugins {
    alias(libs.plugins.base.application)
    alias(libs.plugins.base.application.compose)
    alias(libs.plugins.base.firebase)
    alias(libs.plugins.base.hilt)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")

}

android {
    defaultConfig {
        applicationId = "com.banyumas.wisata"
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.model)

    implementation(projects.feature.dashboard)
    implementation(projects.feature.admin)
    implementation(projects.feature.search)
    implementation(projects.feature.bookmarks)
    implementation(projects.feature.profile)
    implementation(projects.feature.auth)
    implementation(projects.feature.detail)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}