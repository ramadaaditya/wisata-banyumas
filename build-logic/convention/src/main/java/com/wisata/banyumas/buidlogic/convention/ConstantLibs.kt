package com.wisata.banyumas.buidlogic.convention

object ConstantLibs {
    val resourceExcludes = listOf(
        "/META-INF/{AL2.0,LGPL2.1}",
        "/META-INF/gradle/incremental.annotation.processors"
    )

    const val BASE_NAME = "com.banyumas.wisata"
    const val MIN_SDK_VERSION = 26
    const val MAX_SDK_VERSION = 35
    const val FREE_COMPILER = "-opt-in=kotlin.RequiresOptIn"
    const val COMPILER_VERSION = "1.5.21"
}