plugins {
    alias(libs.plugins.base.application)
    alias(libs.plugins.base.application.compose)
    alias(libs.plugins.base.firebase)
    alias(libs.plugins.base.hilt)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.serialization)
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
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(project(":feature:dashboard"))
    implementation(project(":feature:admin"))
    implementation(project(":feature:search"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:bookmarks"))
    implementation(project(":feature:auth"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.core.splashscreen)
}