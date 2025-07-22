package com.wisata.banyumas.buidlogic.convention

import com.android.build.api.dsl.CommonExtension
import groovyjarjarantlr.Version
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.wisata.banyumas.buidlogic.convention.ConstantLibs.COMPILER_VERSION

internal fun Project.configCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            "implementation"(platform(bom))
            "androidTestImplementation"(platform(bom))
            "implementation"(libs.findLibrary("androidx-ui-tooling-preview").get())
            "debugImplementation"(libs.findLibrary("androidx-ui-tooling-debug").get())
        }
    }
}