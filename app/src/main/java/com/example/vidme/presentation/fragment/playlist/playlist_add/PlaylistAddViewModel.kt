package com.example.vidme.presentation.fragment.playlist.playlist_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidme.domain.usecase.ValidatePlaylistNameUseCase
import com.example.vidme.domain.usecase.ValidatePlaylistUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistAddViewModel @Inject constructor(
    private val validatePlaylistNameUseCase: ValidatePlaylistNameUseCase,
    private val validatePlaylistUrlUseCase: ValidatePlaylistUrlUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<PlaylistAddState>(PlaylistAddState())
    val state get() = _state.asStateFlow()

    private var playlistName = ""
    private var url = ""
    var addNew = false
        private set

    fun setPlaylistName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val validationResult = validatePlaylistNameUseCase(name)
            _state.update {
                if (validationResult.isSuccessful) {
                    playlistName = name
                    it.copy(validPlaylistName = true)
                } else {
                    it.copy(playlistNameError = validationResult.error, validPlaylistName = false)
                }
            }
            resetState()

        }
    }

    fun setAddNew(add: Boolean) {
        addNew = add
    }

    fun setUrl(url: String) {
        val validationResult = validatePlaylistUrlUseCase(url)

        _state.update {
            if (validationResult.isSuccessful) {
                this.url = url
                it.copy(validUrl = true)
            } else {
                it.copy(urlError = validationResult.error, validUrl = false)
            }
        }
        resetState()

    }

    fun validate() {
        setPlaylistName(playlistName)
        setUrl(url)
        val valid = listOf(state.value.validPlaylistName, state.value.validUrl).all { it }
        if (valid) {
            _state.update {
                it.copy(success = true)
            }
            resetState()
        }
    }

    private fun resetState() {

    }
}