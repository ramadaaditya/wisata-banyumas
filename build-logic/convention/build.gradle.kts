import org.gradle.initialization.DependenciesAccessors
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.android.lint)
}

group = "com.banyumas.wisata.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.ksp.gradlePlugin)
    gradle.serviceOf<DependenciesAccessors>().classes.asFiles.forEach {
        compileOnly(files(it.absolutePath))
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.base.application.asProvider().get().pluginId
            implementationClass = "AppPlugin"
        }
        register("androidApplicationCompose") {
            id = libs.plugins.base.application.compose.get().pluginId
            implementationClass = "AppComposePlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.base.library.asProvider().get().pluginId
            implementationClass = "LibPlugin"
        }
        register("androidLibraryCompose") {
            id = libs.plugins.base.library.compose.get().pluginId
            implementationClass = "LibComposePlugin"
        }
        register("androidHilt") {
            id = libs.plugins.base.hilt.get().pluginId
            implementationClass = "HiltPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.base.feature.get().pluginId
            implementationClass = "FeaturePlugin"
        }
        register("androidFirebase") {
            id = libs.plugins.base.firebase.get().pluginId
            implementationClass = "FirebasePlugin"
        }

        register("jvmLibrary") {
            id = libs.plugins.base.jvm.get().pluginId
            implementationClass = "JvmLibraryPlugin"
        }
    }
}