plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.base.application.compose)
//    alias(libs.plugins.hilt)
//    alias(libs.plugins.ksp)
//    id("com.google.devtools.ksp")
//    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.wisata.banyumas"

    defaultConfig {
        applicationId = "com.wisata.banyumas"
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

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
//    implementation(libs.androidx.navigation.runtime.ktx)
//    implementation(libs.androidx.navigation.compose)
//    implementation(libs.androidx.paging.common.android)
//    implementation(libs.androidx.espresso.core)
//    implementation(libs.androidx.runtime.livedata)
//    implementation(libs.androidx.paging.compose.android)
//    implementation(libs.transport.runtime)
//    implementation(libs.androidx.ui.text.google.fonts)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling.debug)
//    debugImplementation(libs.androidx.ui.test.manifest)
//
//    implementation(libs.hilt.android)
//    ksp(libs.hilt.android.compiler)
//    implementation(libs.androidx.hilt.navigation.compose)
//
//    implementation(libs.converter.gson)
//    implementation(libs.retrofit)
//    implementation(libs.okhttp.interceptor)
//
//    implementation(libs.androidx.material.icons.extended)
//
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//    implementation(libs.androidx.lifecycle.livedata.ktx)
//
//    implementation(libs.coil.compose)
//    implementation(libs.coil.network)
//
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.firestore)
//    implementation(libs.firebase.storage)
//    implementation(libs.firebase.database)
//    implementation(libs.firebase.auth)
//    implementation(libs.play.services.auth)
//
//    implementation(libs.core.splashscreen)
//
//    implementation(libs.androidx.room.runtime)
//    ksp(libs.androidx.room.compiler)
//
//    implementation(libs.edge.to.edge.preview)
//
//    testImplementation(libs.kotlinx.coroutines.test)
//    testImplementation(libs.mockito.core)
//    testImplementation(libs.mockito.inline)
//    testImplementation(libs.androidx.core.testing)
//    testImplementation(libs.junit)
//
//    implementation(libs.androidx.datastore.preferences)
}