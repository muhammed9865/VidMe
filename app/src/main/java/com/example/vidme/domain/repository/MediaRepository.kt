package com.example.vidme.domain.repository

import com.example.vidme.data.pojo.info.DownloadInfo
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.example.vidme.domain.DataState
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.concurrent.Executor

interface MediaRepository {

    suspend fun getVideoInfo(url: String, executor: Executor): Flow<DataState<VideoInfo>>

    suspend fun getYoutubePlaylistInfo(
        url: String,
        executor: Executor,
    ): Flow<DataState<YoutubePlaylistInfo>>

    suspend fun downloadVideo(
        videoInfo: VideoInfo,
        audioOnly: Boolean,
        executor: Executor,
    ): Flow<DataState<DownloadInfo>>

    suspend fun downloadPlaylist(
        playlistInfo: YoutubePlaylistInfo,
        audioOnly: Boolean,
        executor: Executor,
    ): Flow<DataState<DownloadInfo>>

    suspend fun getDownloadedPlaylists(file: File)


}