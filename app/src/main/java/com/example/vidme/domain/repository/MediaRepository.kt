package com.example.vidme.domain.repository

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.*
import com.example.vidme.domain.pojo.request.PlaylistRequest
import com.example.vidme.domain.pojo.request.VideoRequest
import java.util.concurrent.Executor

interface MediaRepository {

    suspend fun fetchVideoInfo(
        url: String,
        executor: Executor,
        onVideoInfo: (DataState<VideoInfo>) -> Unit,
    )

    suspend fun fetchYoutubePlaylistInfo(
        playlistName: String,
        url: String,
        executor: Executor,
        onPlaylistInfo: (DataState<YoutubePlaylistInfo>) -> Unit,
    )

    suspend fun synchronizeYoutubePlaylistInfo(
        playlistInfo: YoutubePlaylistInfo,
        executor: Executor,
        onPlaylistInfo: (DataState<YoutubePlaylistInfo>) -> Unit,
    )

    suspend fun downloadVideo(
        videoRequest: VideoRequest,
        executor: Executor,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    )

    suspend fun downloadPlaylist(
        playlistRequest: PlaylistRequest,
        executor: Executor,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    )

    suspend fun getStoredYoutubePlaylists(): List<YoutubePlaylistInfo>

    suspend fun getStoredVideos(): List<VideoInfo>

    suspend fun getStoredYoutubePlaylistByName(playlistName: String): YoutubePlaylistWithVideos?

    suspend fun getStoredVideo(id: String, playlistName: String = ""): VideoInfo

    suspend fun deleteVideo(videoInfo: VideoInfo): Boolean

    suspend fun deletePlaylistByName(playlistName: String): Boolean

    companion object {
        const val RETRY_AFTER_FAIL_TIMES: Long = 3
    }

}