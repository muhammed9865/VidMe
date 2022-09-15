package com.example.vidme.presentation.fragment.playlist_add

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

    private val _state = MutableStateFlow(PlaylistAddState())
    val state get() = _state.asStateFlow()


    fun setPlaylistName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val validationResult = validatePlaylistNameUseCase(name)
            _state.update {
                if (validationResult.isSuccessful) {
                    it.copy(validPlaylistName = true)
                } else {
                    it.copy(playlistNameError = validationResult.error)
                }
            }


        }
    }


    fun setUrl(url: String) {
        val validationResult = validatePlaylistUrlUseCase(url)

        _state.update {
            if (validationResult.isSuccessful) {
                it.copy(validUrl = true)
            } else {
                it.copy(urlError = validationResult.error)
            }
        }

    }
}