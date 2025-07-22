package com.wisata.banyumas.buidlogic.convention

object ConstantLibs {
    val resourceExcludes = listOf(
        "META-INF/DEPENDENCIES",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "META-INF/*.kotlin_module", // Mengeluarkan file metadata Kotlin yang mungkin tidak diperlukan
        "META-INF/*.version",
        "META-INF/*.txt",
        "**/*.properties", // Contoh: mengeluarkan semua file .properties
        // Pola untuk menghindari konflik di library tertentu
        "META-INF/AL2.0", // Apache License 2.0
        "META-INF/LGPL2.1", // GNU Lesser General Public License
        "*.txt",
        "*.md"
    )
    const val BASE_NAME = "com.banyumas.wisata"
    const val MIN_SDK_VERSION = 26
    const val MAX_SDK_VERSION = 35
    const val FREE_COMPILER = "-opt-in=kotlin.RequiresOptIn"
    const val COMPILER_VERSION = "1.5.21"
}