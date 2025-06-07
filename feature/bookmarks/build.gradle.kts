plugins {
    alias(libs.plugins.base.feature)
}
dependencies {
    implementation(project(":feature:auth"))
}