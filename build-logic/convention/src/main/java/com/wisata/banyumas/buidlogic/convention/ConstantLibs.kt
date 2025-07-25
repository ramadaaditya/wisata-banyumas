package com.wisata.banyumas.buidlogic.convention

object ConstantLibs {
    // Alternatif: Resource excludes khusus untuk release build
    val releaseResourceExcludes = listOf(
        "META-INF/DEPENDENCIES",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "META-INF/*.kotlin_module",
        "META-INF/*.version",
        "META-INF/AL2.0",
        "META-INF/LGPL2.1",
        "**/*.properties",
        "*.txt",
        "*.md"
    )

    // Debug build tanpa exclude yang agresif
    val debugResourceExcludes = listOf(
        "META-INF/DEPENDENCIES",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt"
    )
    const val BASE_NAME = "com.banyumas.wisata"
    const val MIN_SDK_VERSION = 26
    const val MAX_SDK_VERSION = 35
    const val FREE_COMPILER = "-opt-in=kotlin.RequiresOptIn"
    const val COMPILER_VERSION = "1.5.21"
}