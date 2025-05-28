package com.wisata.banyumas.common

sealed class UiState<out T> {
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: UiText, val throwable: Throwable? = null) : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}