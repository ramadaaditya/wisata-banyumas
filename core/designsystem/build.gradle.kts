import com.wisata.banyumas.buidlogic.convention.implementation
import com.wisata.banyumas.buidlogic.convention.libs

plugins {
    alias(libs.plugins.base.library)
    alias(libs.plugins.base.library.compose)
}

dependencies {
    implementation(libs.androidx.ui.text.google.fonts)
    api(libs.androidx.material3)
    api(libs.androidx.material.icons.extended)
    api(libs.androidx.ui)
    api(libs.coil.compose)
    api(libs.coil.network)
    api(libs.coil.video)
    api(project(":core:model"))
    api(project(":core:common"))
}