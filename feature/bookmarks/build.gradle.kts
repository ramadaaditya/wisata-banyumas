plugins {
    alias(libs.plugins.base.feature)
}
dependencies {
    implementation(project(":feature:auth"))
    api(project(":core:data"))
}