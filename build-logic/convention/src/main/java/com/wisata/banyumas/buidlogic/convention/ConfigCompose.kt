package com.wisata.banyumas.buidlogic.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val bom = libs.androidx.compose.bom.get()
            implementation(platform(bom))
            androidTestImplementation(platform(bom))
            implementation(libs.androidx.activity.compose.get())
            implementation(libs.timber.get())
            implementation(libs.androidx.ui.tooling.preview.get())
            debugImplementation(libs.androidx.ui.tooling.debug.get())
            implementation(libs.androidx.ui)
            implementation(libs.androidx.ui.graphics)
            implementation(libs.androidx.material3)
            implementation(libs.androidx.navigation.runtime.ktx)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
        }
    }
}