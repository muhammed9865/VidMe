package com.example.vidme.domain.repository

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.*
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
        playlistInfo: YoutubePlaylistInfo,
        audioOnly: Boolean,
        executor: Executor,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    )

    suspend fun getStoredYoutubePlaylists(): List<YoutubePlaylistInfo>

    suspend fun getStoredVideos(): List<VideoInfo>

    suspend fun getStoredYoutubePlaylistByName(playlistName: String): YoutubePlaylistWithVideos?

    suspend fun getStoredVideo(id: String, playlistName: String = ""): VideoInfo

    suspend fun deleteVideo(videoInfo: VideoInfo): Boolean

    suspend fun deletePlaylistByName(playlistName: String): Boolean


}