package com.banyumas.wisata.core.designsystem

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
    return email.matches(emailRegex.toRegex())
}


fun isValidPassword(password: String): Boolean {
    val regex = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$"
    return password.matches(regex.toRegex())
}
