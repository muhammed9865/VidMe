package com.example.vidme.presentation.fragment.single.single_add

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class VideoAddViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(VideoAddState())
    val state get() = _state.asStateFlow()


    private var url = ""
    var addNew = false
        private set


    fun setUrl(url: String) {
        if (url.isEmpty()) {
            _state.update {
                it.copy(urlError = "URL can't be empty")
            }
            return
        }
        this.url = url
        _state.update {
            it.copy(validUrl = true)
        }
    }

    fun finishAdding() {
        _state.update {
            it.copy(addClicked = true, urlError = null, validUrl = false, success = false)
        }
    }

    fun resetState() {
        _state.update {
            it.copy(urlError = null, validUrl = false, addClicked = false, success = false)
        }
    }

}