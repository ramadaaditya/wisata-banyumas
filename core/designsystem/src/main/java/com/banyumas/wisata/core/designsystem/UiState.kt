package com.banyumas.wisata.core.designsystem

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart


//sealed class UiState<out T> {
//    data class Success<out T>(val data: T) : UiState<T>()
//    data class Error(val message: UiText, val throwable: Throwable? = null) : UiState<Nothing>()
//    data object Loading : UiState<Nothing>()
//    data object Empty : UiState<Nothing>()
//}

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> = map<T, Result<T>> { Result.Success(it) }
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(it)) }