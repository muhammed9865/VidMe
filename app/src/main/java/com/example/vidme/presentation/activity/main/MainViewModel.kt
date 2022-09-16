package com.example.vidme.presentation.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.domain.usecase.*
import com.example.vidme.domain.util.StringUtil
import com.example.vidme.presentation.callback.SingleDownloadState
import com.example.vidme.presentation.fragment.singles.SingleFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val addPlaylist: FetchYoutubePlaylistInfoUseCase,
) : ViewModel() {


    private val _playlists = MutableStateFlow<List<YoutubePlaylistInfo>>(mutableListOf())
    val playlists: StateFlow<List<YoutubePlaylistInfo>> = _playlists

    private val _singles = MutableStateFlow<List<VideoInfo>>(mutableListOf())
    val singles: StateFlow<List<VideoInfo>> = _singles

    private val _state = MutableStateFlow(MainState())
    val state get() = _state.asSharedFlow()

    private lateinit var cachedSingles: List<VideoInfo>
    private lateinit var cachedPlaylist: List<YoutubePlaylistInfo>
    private val singleFilters =
        hashMapOf<String, SingleFilter?>().also { it["filter"] = SingleFilter.All }

    init {
        loadPlaylists()
        loadSingles()
    }

    private fun loadPlaylists() {
        tryAsync {
            _playlists.update {
                getPlaylists().also {
                    cachedPlaylist = it
                }
            }
        }
    }

    private fun loadSingles() {
        tryAsync {
            _singles.update {
                getSingles().also {
                    cachedSingles = it
                }
            }
        }
    }


    fun searchPlaylists(query: String?) {
        // setting the playlist elements that match the query if query isNotEmpty
        if (query?.isNotEmpty() == true) {
            _playlists.update {
                cachedPlaylist.filter { query.lowercase() in it.name.lowercase() }
            }
        } else {
            // if query is empty, returning back the saved instance
            _playlists.update {
                cachedPlaylist
            }
        }
    }


    fun searchSingles(query: String?) {
        // setting the singles elements that match the query if query isNotEmpty
        if (query?.isNotEmpty() == true) {
            _singles.update { _ ->
                cachedSingles.filter { query.lowercase() in it.title.lowercase() }
            }
        } else {
            // if query is empty, returning back the saved instance
            _singles.update {
                cachedSingles
            }
        }
    }


    fun addSinglesFilter(filter: SingleFilter) {
        if (filter is SingleFilter.DownloadedOnly) {
            if (singleFilters["download"] != null) {
                singleFilters["download"] = null
            } else singleFilters["download"] = filter
        } else {
            singleFilters["filter"] = filter
        }
        filterSingles()
    }


    private fun filterSingles() {
        singleFilters.values.filterNotNull().forEach { fil ->
            _singles.update {
                fil.filter(cachedSingles)
            }
        }
    }


    fun synchronizePlaylist(youtubePlaylistInfo: YoutubePlaylistInfo) {
        tryAsync {
            Timber.d("Syncing ${youtubePlaylistInfo.name}")

            setState(_state.value.copy(syncing = true))
            updatePlaylistAItem(item = youtubePlaylistInfo,
                update = youtubePlaylistInfo.copy(isSyncing = true))

            synchronizePlaylist(youtubePlaylistInfo) { synced ->
                if (synced.isSuccessful) {
                    val data = synced.data!!

                    if (data.count == youtubePlaylistInfo.count) {
                        setState(_state.value.copy(simpleMessage = "${youtubePlaylistInfo.name} is UP-TO-DATE"))
                    }

                    //updatePlaylistAItem(youtubePlaylistInfo, data.copy(isSyncing = false))
                    loadPlaylists()
                } else {
                    updatePlaylistAItem(youtubePlaylistInfo,
                        youtubePlaylistInfo.copy(isSyncing = false))
                    setState(_state.value.copy(error = synced.error))
                }
                setState(_state.value.copy(syncing = false))
            }
        }
    }

    fun synchronizeAllPlaylists() {
        tryAsync {
            _playlists.value.forEach { youtubePlaylistInfo ->
                synchronizePlaylist(youtubePlaylistInfo)
            }
        }
    }

    private fun updatePlaylistAItem(item: YoutubePlaylistInfo, update: YoutubePlaylistInfo) {
        val itemIndex = _playlists.value.indexOf(item)
        _playlists.update {
            it.mapIndexed { index, youtubePlaylistInfo -> if (index == itemIndex) update else youtubePlaylistInfo }
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
            loadPlaylists()
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
            loadSingles()
        }
    }

    fun addPlaylist(name: String, url: String) {
        tryAsync {
            addPlaylist(name, url) {
                if (it.isSuccessful) {
                    tryAsync {
                        loadPlaylists()
                        setState(_state.value.copy(fetched = true, playlistWasCached = it.cached))
                    }
                } else {
                    setState(_state.value.copy(error = it.error))
                }
            }
        }
    }

    fun resetStateAfterAdding() {
        setState(_state.value.copy(
            playlistWasCached = false,
            fetched = false,
        ))
    }


    private fun setState(state: MainState) {
        _state.value = state
    }

    private fun tryAsync(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onError: (e: Exception) -> Unit = {},
        action: suspend () -> Unit,
    ) {
        viewModelScope.launch(dispatcher) {
            try {
                action()
            } catch (e: Exception) {
                Timber.d(e)
                // setState(_state.value.copy(error = e.message))
                onError(e)
            }
        }
    }
}