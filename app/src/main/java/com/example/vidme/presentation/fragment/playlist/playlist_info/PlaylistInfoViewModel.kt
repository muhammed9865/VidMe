package com.example.vidme.presentation.fragment.playlist.playlist_info

import androidx.lifecycle.ViewModel
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.domain.pojo.YoutubePlaylistWithVideos
import com.example.vidme.presentation.callback.PlaylistDownloadState
import com.example.vidme.service.audio.AudioManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaylistInfoViewModel @Inject constructor(audioManager: AudioManager) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistInfoState())
    val state get() = _state.asStateFlow()

    lateinit var selectedPlaylistWithVideos: YoutubePlaylistWithVideos

    private var selectedSingleID = ""

    init {
        audioManager.setOnAudioDataListener(187) {
            if (it.single.playlistName == selectedPlaylistWithVideos.getPlaylistName()) {
                if (selectedSingleID != it.single.id)
                    setSelectedSingle(it.single)
            }
        }
    }

    fun setSelectedPlaylist(playlist: YoutubePlaylistWithVideos) {
        selectedPlaylistWithVideos = playlist
        val firstVideo = playlist.videos[0]
        selectedSingleID = firstVideo.id
        updateState(playlist)
        setSelectedSingle(firstVideo)
    }

    fun setSelectedSingle(single: VideoInfo) {
        try {
            selectedSingleID = single.id
            val updatedSingles = _state.value.singles.map {
                if (it.id == single.id) it.copy(isSelected = true) else it.copy(isSelected = false)
            }
            updateState(selectedPlaylistWithVideos.copy(videos = updatedSingles))
        } catch (e: Exception) {
        }
    }

    fun getPlayableSingles(): List<VideoInfo> =
        state.value.singles.filter { it.isVideo or it.isAudio }

    fun onDownloadingPlaylist(): PlaylistDownloadState {

        return object : PlaylistDownloadState {

            override fun onDownloading(
                playlistInfo: YoutubePlaylistInfo,
                index: Int,
                progress: Float,
                timeRemaining: String,
            ) {

                _state.update {
                    it.copy(showDownloading = true,
                        videoBeingDownloaded = getVideos()[index],
                        downloadProgress = progress.toInt(),
                        downloadTimeRemaining = timeRemaining)
                }
            }

            override fun onFinished() {
                Timber.d("PlaylistInfoViewModel -> Finished")
                _state.update {
                    it.copy(showDownloading = false,
                        downloadTimeRemaining = null,
                        videoBeingDownloaded = null,
                        downloadProgress = null)
                }
            }

            override fun onError(error: String) {
                _state.update {
                    it.copy(error = error, showDownloading = false)
                }
            }
        }
    }

    fun getVideos() = selectedPlaylistWithVideos.videos


    private fun getCurrentSingle() =
        selectedPlaylistWithVideos.videos.first { it.id == selectedSingleID }

    private fun updateState(playlistWithVideos: YoutubePlaylistWithVideos) {
        val info = playlistWithVideos.playlistInfo
        val videos = playlistWithVideos.videos
        _state.update {
            it.copy(playlistTitle = info.name,
                playedSingleThumbnail = getCurrentSingle().thumbnail,
                singleCount = videos.size,
                lastSynced = info.lastSynced,
                singles = videos,
                playlistFullDuration = playlistWithVideos.getFullDuration()
            )
        }
    }
}