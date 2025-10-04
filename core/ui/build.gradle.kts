plugins {
    alias(libs.plugins.base.library)
    alias(libs.plugins.base.library.compose)
}
dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)
}