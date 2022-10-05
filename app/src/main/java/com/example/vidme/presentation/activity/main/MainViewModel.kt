package com.example.vidme.presentation.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.domain.pojo.YoutubePlaylistWithVideos
import com.example.vidme.domain.pojo.request.PlaylistRequest
import com.example.vidme.domain.pojo.request.VideoRequest
import com.example.vidme.domain.usecase.*
import com.example.vidme.domain.util.StringUtil
import com.example.vidme.presentation.callback.PlaylistDownloadState
import com.example.vidme.presentation.callback.SingleDownloadState
import com.example.vidme.presentation.fragment.single.singles_home.SingleFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@Suppress("unused")
class MainViewModel @Inject constructor(
    private val getPlaylists: GetStoredPlaylistsUseCase,
    private val getPlaylistWithVideos: GetStoredPlaylistUseCase,
    private val getSingles: GetStoredVideosUseCase,
    private val synchronizePlaylist: SynchronizePlaylistUseCase,
    private val downloadVideo: DownloadVideoUseCase,
    private val downloadPlaylist: DownloadYoutubePlaylistUseCase,
    private val deleteVideo: DeleteVideoUseCase,
    private val deletePlaylist: DeletePlaylistUseCase,
    private val addPlaylist: FetchYoutubePlaylistInfoUseCase,
    private val addVideo: FetchVideoInfoUseCase,
) : ViewModel() {


    private val _playlists = MutableStateFlow<List<YoutubePlaylistInfo>>(mutableListOf())
    val playlists: StateFlow<List<YoutubePlaylistInfo>> = _playlists

    private val _selectedPlaylist = MutableStateFlow<YoutubePlaylistWithVideos?>(null)
    val selectedPlaylist get() = _selectedPlaylist

    private val _singles = MutableStateFlow<List<VideoInfo>>(emptyList())
    val singles = _singles.asStateFlow()

    private val _selectedSingleForDownload = MutableStateFlow<VideoInfo?>(null)
    val selectedSingleForDownload get() = _selectedSingleForDownload

    private val _currentPlayingAudio = MutableStateFlow<List<VideoInfo>>(emptyList())
    val currentPlayingAudio get() = _currentPlayingAudio.asStateFlow()

    private val _currentPlayingVideo = MutableStateFlow<VideoInfo?>(null)
    val currentPlayingVideo get() = _currentPlayingVideo.asStateFlow()

    var isPlayingAudio = MutableStateFlow(false)

    private var currentSinglesDownloadingIDs: MutableList<String> = mutableListOf()


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
                cachedSingles = getSingles().filter { it.id !in currentSinglesDownloadingIDs }
                cachedSingles
            }
        }
    }

    fun setSelectedPlaylist(playlist: YoutubePlaylistInfo?) {
        tryAsync {
            _selectedPlaylist.update {
                playlist?.let { getPlaylistWithVideos(it.name) }
            }
        }
    }

    fun setCurrentPlaying(single: List<VideoInfo>) {
        _currentPlayingAudio.update {
            single

        }

    }

    fun setSelectedSingle(single: VideoInfo?) {
        _selectedSingleForDownload.update {
            single
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
        Timber.d(singleFilters.values.toString())
        filterSingles()
    }


    private fun filterSingles() {
        var newValues = cachedSingles
        singleFilters.values.filterNotNull().forEach { fil ->
            _singles.update {
                newValues = fil.filter(newValues, currentSinglesDownloadingIDs)
                newValues
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
                    if (_selectedPlaylist.value == null)
                        loadPlaylists()
                    else {
                        updateSelectedPlaylist()
                    }
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


    fun downloadSingle(
        videoRequest: VideoRequest,
        downloadCallback: SingleDownloadState,
    ) {
        tryAsync {
            videoRequest.videoInfo?.id?.let { currentSinglesDownloadingIDs.add(it) }
            setState(_state.value.copy(downloading = true))
            downloadVideo(videoRequest) {
                tryAsync(Dispatchers.Main) {
                    if (it.isSuccessful) {
                        val data = it.data!!
                        if (data.isFinished) {
                            downloadCallback.onFinished(data.videoInfo!!)
                            currentSinglesDownloadingIDs.remove(videoRequest.videoInfo!!.id)
                            // Will only update the playlist if there's actually a playlist on foreground
                            updateSelectedPlaylist()
                            setState(_state.value.copy(downloading = false))
                        } else {
                            downloadCallback.onDownloading(data)
                        }
                    } else {
                        downloadCallback.onFailure(videoRequest.videoInfo!!.copy(isDownloaded = false,
                            isDownloading = false))
                        currentSinglesDownloadingIDs.remove(videoRequest.videoInfo.id)
                        setState(_state.value.copy(error = "Couldn't download single"))
                    }
                }
            }
        }
    }

    fun downloadPlaylist(
        playlistRequest: PlaylistRequest,
        downloadCallback: PlaylistDownloadState,
    ) {
        tryAsync {
            downloadPlaylist(playlistRequest) {
                if (it.isSuccessful) {
                    val data = it.data!!
                    val playlistInfo = playlistRequest.playlistInfo!!

                    if (data.isFinished) {
                        downloadCallback.onFinished()
                        updateSelectedPlaylist()
                    } else {
                        downloadCallback.onDownloading(playlistInfo,
                            data.currentVideoIndex,
                            data.progress,
                            StringUtil.durationAsString(data.timeRemaining.toInt(),
                                appendUnit = true))
                    }
                    debugDownloadInfo("MainViewModel ->", data)
                } else {
                    downloadCallback.onError("Couldn't download playlist, check your internet connection")
                    updateSelectedPlaylist()
                }
            }
        }
    }


    private fun updateSelectedPlaylist() {
        _selectedPlaylist.value?.let {
            setSelectedPlaylist(it.playlistInfo)
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
                        loadSingles()
                        setState(_state.value.copy(fetchedPlaylist = true,
                            playlistWasCached = it.cached))
                    }
                } else {
                    setState(_state.value.copy(error = it.error))
                }
            }
        }
    }

    fun addSingle(url: String) {
        tryAsync {
            addVideo(url) {
                if (it.isSuccessful) {
                    loadSingles()
                    setState(_state.value.copy(fetchedVideo = true))
                } else {
                    setState(_state.value.copy(error = it.error))
                }
            }
        }
    }

    fun resetStateAfterAdding() {
        setState(_state.value.copy(
            playlistWasCached = false,
            fetchedPlaylist = false,
            fetchedVideo = false,
            error = null,

            ))
    }

    fun setIsPlaying(isPlaying: Boolean) {
        this.isPlayingAudio.update {
            isPlaying
        }
    }

    private fun updatePlaylistAItem(item: YoutubePlaylistInfo, update: YoutubePlaylistInfo) {
        val itemIndex = _playlists.value.indexOf(item)
        _playlists.update {
            it.mapIndexed { index, youtubePlaylistInfo -> if (index == itemIndex) update else youtubePlaylistInfo }
        }
    }

    private fun updateSingleAtItem(item: VideoInfo, update: VideoInfo) {
        val itemIndex = _singles.value.indexOf(item)
        _singles.update {
            it.mapIndexed { index, youtubePlaylistInfo -> if (index == itemIndex) update else youtubePlaylistInfo }
        }
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

    companion object {
        fun debugDownloadInfo(tag: String, downloadInfo: DownloadInfo) {
            Timber.d("$tag -> progress: ${downloadInfo.progress}, timeRemaining: ${downloadInfo.timeRemaining}, index: ${downloadInfo.currentVideoIndex}, isFinished: ${downloadInfo.isFinished}")
        }
    }
}