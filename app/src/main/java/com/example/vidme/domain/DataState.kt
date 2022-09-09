package com.example.vidme.domain

class DataState <out T>  private constructor(private val data: T? = null, private val error: String? = null) {
    companion object {
        fun <T> success(data: T)  =  DataState(data)
        fun <T> failure(error: String?) = DataState<T>(error = error)
    }
}