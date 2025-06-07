import com.wisata.banyumas.buidlogic.convention.implementation
import com.wisata.banyumas.buidlogic.convention.libs

plugins {
    alias(libs.plugins.base.application)
    alias(libs.plugins.base.application.compose)
    id("com.google.gms.google-services")
    alias(libs.plugins.base.firebase)
    alias(libs.plugins.base.hilt)
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
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.paging.compose.android)
    implementation(libs.transport.runtime)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.junit)

    implementation(libs.androidx.datastore.preferences)
}