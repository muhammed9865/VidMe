package com.example.vidme.domain

class DataState<out T> private constructor(val data: T? = null, val error: String? = null) {
    companion object {
        fun <T> success(data: T) = DataState(data)
        fun <T> failure(error: String?) = DataState<T>(error = error)
    }
}