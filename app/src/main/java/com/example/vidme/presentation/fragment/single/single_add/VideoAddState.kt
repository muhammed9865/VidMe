package com.example.vidme.presentation.fragment.single.single_add

data class VideoAddState(
    val urlError: String? = null,
    val validUrl: Boolean = false,
    val addClicked: Boolean = false,
    val success: Boolean = false,
)
