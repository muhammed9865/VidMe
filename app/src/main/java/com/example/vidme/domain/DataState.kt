package com.example.vidme.domain

class DataState<out T> private constructor(
    val data: T? = null,
    val error: String? = null,
    val isSuccessful: Boolean,
    val cached: Boolean = false,
) {
    companion object {
        fun <T> success(data: T, cached: Boolean = false) =
            DataState(data, isSuccessful = true, cached = cached)

        fun <T> failure(error: String?) = DataState<T>(error = error, isSuccessful = false)
    }
}