package com.example.vidme.domain.repository

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import java.util.concurrent.Executor

interface MediaRepository {

    suspend fun getVideoInfo(
        url: String,
        executor: Executor,
        onVideoInfo: (DataState<VideoInfo>) -> Unit,
    )

    suspend fun getYoutubePlaylistInfo(
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
        videoInfo: VideoInfo,
        audioOnly: Boolean,
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


}