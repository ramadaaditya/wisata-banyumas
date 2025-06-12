plugins {
    alias(libs.plugins.base.feature)
    alias(libs.plugins.base.library.compose)
}

dependencies {
    implementation(project(":feature:auth"))
    implementation(project(":core:data"))
}