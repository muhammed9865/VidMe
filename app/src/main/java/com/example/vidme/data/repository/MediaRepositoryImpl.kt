package com.example.vidme.data.repository

import com.example.vidme.data.pojo.info.DownloadInfo
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.example.vidme.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(): MediaRepository {
    override suspend fun getVideoInfo(url: String, executor: Executor): Flow<VideoInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaylistInfo(url: String, executor: Executor): Flow<YoutubePlaylistInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadVideo(url: String, executor: Executor): Flow<DownloadInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadPlaylist(url: String, executor: Executor) {
        TODO("Not yet implemented")
    }

    override suspend fun getDownloadedPlaylists(file: File) {
        TODO("Not yet implemented")
    }
}