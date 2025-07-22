plugins {
    alias(libs.plugins.base.library)
    alias(libs.plugins.base.hilt)
    alias(libs.plugins.base.firebase)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    api(libs.timber)

    implementation(libs.okhttp.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.lifecycle.runtimeCompose)
}