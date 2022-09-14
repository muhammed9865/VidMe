package com.example.vidme.presentation.activity.main

import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.domain.usecase.*
import com.example.vidme.domain.util.StringUtil
import com.example.vidme.presentation.base.BaseViewModel
import com.example.vidme.presentation.callback.SingleDownloadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPlaylists: GetStoredPlaylistsUseCase,
    private val getSingles: GetStoredVideosUseCase,
    private val synchronizePlaylist: SynchronizePlaylistUseCase,
    private val downloadVideo: DownloadVideoUseCase,
    private val deleteVideo: DeleteVideoUseCase,
    private val deletePlaylist: DeletePlaylistUseCase,
) : BaseViewModel() {

    private val _playlists = MutableStateFlow<List<YoutubePlaylistInfo>>(mutableListOf())
    val playlists: StateFlow<List<YoutubePlaylistInfo>> = _playlists

    private val _singles = MutableStateFlow<List<VideoInfo>>(mutableListOf())
    val singles: StateFlow<List<VideoInfo>> = _singles

    private val _state = MutableStateFlow(MainState())
    val state get() = _state.asStateFlow()

    private fun loadPlaylists() {
        tryAsync(
            onError = {
                setState(_state.value.copy(error = it.message))
            }
        ) {
            _playlists.update { getPlaylists().toMutableList() }
        }
    }

    private fun loadSingles() {
        tryAsync(
            onError = {
                Timber.d(it)
                setState(_state.value.copy(error = it.message))
            }
        ) {
            _singles.update {
                getSingles()
            }
        }
    }

    private var cachedPlaylists = _playlists.value
    private var isNotSearchingPlaylists = true
    fun searchPlaylists(query: String?) {
        // Saving an instance of the entire playlist before searching
        if (isNotSearchingPlaylists) {
            cachedPlaylists = _playlists.value
        }

        // setting the playlist elements that match the query if query isNotEmpty
        if (query?.isNotEmpty() == true) {
            isNotSearchingPlaylists = false
            _playlists.update { list ->
                list.filter { it.name == query }
            }
        } else {
            // if query is empty, returning back the saved instance
            isNotSearchingPlaylists = true
            _playlists.update {
                cachedPlaylists
            }
        }
    }

    private var cachedSingles = _singles.value
    private var isNotSearchingSingles = true
    fun searchSingles(query: String?) {
        // Saving an instance of the entire singles list before searching
        if (isNotSearchingSingles) {
            cachedSingles = _singles.value
        }

        // setting the singles elements that match the query if query isNotEmpty
        if (query?.isNotEmpty() == true) {
            isNotSearchingSingles = false
            _singles.update { list ->
                list.filter { it.title == query }
            }
        } else {
            // if query is empty, returning back the saved instance
            isNotSearchingSingles = true
            _singles.update {
                cachedSingles
            }
        }
    }

    fun synchronizePlaylist(youtubePlaylistInfo: YoutubePlaylistInfo) {
        tryAsync {
            setState(_state.value.copy(syncing = true))

            synchronizePlaylist(youtubePlaylistInfo) { synced ->
                if (synced.isSuccessful) {
                    val playlistIndex = _playlists.value.indexOf(youtubePlaylistInfo)
                    _playlists.update {
                        it.mapIndexed { index, info -> if (index == playlistIndex) synced.data!! else info }
                    }
                } else {
                    setState(_state.value.copy(error = synced.error))
                }
                setState(_state.value.copy(syncing = false))
            }
        }
    }

    fun downloadSingle(
        videoInfo: VideoInfo,
        audioOnly: Boolean = false,
        singleDownloadState: SingleDownloadState,
    ) {
        val videoIndex = _singles.value.indexOf(videoInfo)

        tryAsync {
            setState(_state.value.copy(downloading = true))
            downloadVideo(videoInfo, audioOnly) {
                if (it.isSuccessful) {
                    val data = it.data!!
                    if (data.isFinished) {

                        _singles.update { singles ->
                            singles.mapIndexed { index, videoInfo -> if (videoIndex == index) data.videoInfo!! else videoInfo }
                        }

                        singleDownloadState.onFinished(data.videoInfo!!)
                        setState(_state.value.copy(downloading = false))
                    } else {
                        singleDownloadState.onDownloading(videoInfo,
                            data.progress,
                            StringUtil.durationAsString(data.timeRemaining.toInt()))
                    }
                }
            }
        }
    }

    fun deletePlaylist(playlistInfo: YoutubePlaylistInfo) {
        tryAsync(
            onError = {
                Timber.d(it)
                setState(_state.value.copy(error = "Something went wrong"))
            }
        ) {
            deletePlaylist(playlistInfo.name)
            _playlists.update { list ->
                list.filter { it.name != playlistInfo.name }
            }
        }
    }

    fun deleteSingle(videoInfo: VideoInfo) {
        tryAsync(
            onError = {
                Timber.d(it)
                setState(_state.value.copy(error = "Something went wrong"))
            }
        ) {
            deleteVideo(videoInfo)
            _singles.update { list ->
                list.filter { it.id != videoInfo.id }
            }
        }
    }

    private fun setState(state: MainState) {
        _state.value = state
    }
}