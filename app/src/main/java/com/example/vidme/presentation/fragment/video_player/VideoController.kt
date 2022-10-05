package com.example.vidme.presentation.fragment.video_player

import android.content.Context
import android.util.AttributeSet
import android.widget.MediaController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VideoController : MediaController {
    constructor(context: Context, useFastForward: Boolean = true) : super(context, useFastForward)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val _visibility = MutableStateFlow(false)
    val controlsVisibility = _visibility.asStateFlow()

    override fun isShown(): Boolean {
        return super.isShown().also { isVisible ->
            _visibility.update {
                isVisible
            }
        }
    }

}