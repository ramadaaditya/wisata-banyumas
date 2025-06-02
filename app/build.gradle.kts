plugins {
    alias(libs.plugins.base.application)
    alias(libs.plugins.base.application.compose)
    id("com.google.gms.google-services")
    alias(libs.plugins.base.firebase)
    alias(libs.plugins.base.hilt)
}

android {
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
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))


    implementation(project(":feature:login"))
    implementation(project(":feature:register"))
    implementation(project(":feature:resetpassword"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:admin"))
    implementation(project(":feature:search"))



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.core.splashscreen)


//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
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
//    debugImplementation(libs.androidx.ui.test.manifest)
//
//    implementation(libs.hilt.android)
//    ksp(libs.hilt.android.compiler)
//    implementation(libs.androidx.hilt.navigation.compose)
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