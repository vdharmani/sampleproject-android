package com.vdharmani.starter.core.common

/**
 * Three-state wrapper for asynchronous UI data: not yet loaded, in-flight,
 * successful, or failed. Mirrors what most screens actually render — a
 * spinner / a list / an error message.
 *
 * Domain layer returns `Result<T>`; the ViewModel converts to `Resource<T>`
 * for the screen.
 */
sealed class Resource<out T> {
    data object Idle : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable, val message: String? = throwable.message) :
        Resource<Nothing>()
}

inline fun <T> Resource<T>.onSuccess(block: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) block(data)
    return this
}

inline fun <T> Resource<T>.onError(block: (Throwable) -> Unit): Resource<T> {
    if (this is Resource.Error) block(throwable)
    return this
}
