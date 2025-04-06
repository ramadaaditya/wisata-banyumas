package com.banyumas.wisata.utils

fun isValidEmail(email : String) : Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    val regex = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$"
    return password.matches(regex.toRegex())
}
