plugins {
    alias(libs.plugins.base.library)
    alias(libs.plugins.base.library.compose)
}

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))
    implementation(libs.androidx.ui.text.google.fonts)

    api(libs.androidx.material3)
    api(libs.androidx.material.icons.extended)
    api(libs.androidx.ui)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.ui.tooling.debug)


    implementation(libs.coil.compose)
    implementation(libs.coil.network)

}