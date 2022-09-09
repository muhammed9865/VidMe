package com.example.vidme.domain

import com.example.vidme.data.pojo.info.DownloadInfo
import com.example.vidme.data.pojo.info.PlaylistInfo
import com.example.vidme.data.pojo.info.VideoInfo
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.concurrent.Executor

interface VideosRepository {

    suspend fun getVideoInfo(url: String, executor: Executor) : Flow<VideoInfo>

    suspend fun getPlaylistInfo(url: String, executor: Executor) : Flow<PlaylistInfo>

    suspend fun downloadVideo(url: String, executor: Executor) : Flow<DownloadInfo>

    suspend fun downloadPlaylist(url: String, executor: Executor)

    suspend fun getDownloadedPlaylists(file: File)



}