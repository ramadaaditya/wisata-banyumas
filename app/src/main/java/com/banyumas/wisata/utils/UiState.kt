package com.banyumas.wisata.utils

sealed class UiState<out T : Any?> {
    data class Success<out T : Any>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
}