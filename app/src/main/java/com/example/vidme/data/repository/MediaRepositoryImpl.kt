package com.example.vidme.data.repository

import com.example.vidme.data.downloader.DownloadProcessor
import com.example.vidme.data.pojo.info.DownloadInfo
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.example.vidme.data.request.VideoDownloadRequest
import com.example.vidme.data.request.VideoInfoRequest
import com.example.vidme.data.request.YoutubePlaylistDownloadRequest
import com.example.vidme.data.request.YoutubePlaylistInfoRequest
import com.example.vidme.domain.DataState
import com.example.vidme.domain.repository.MediaRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(private val processor: DownloadProcessor) :
    MediaRepository {
    override suspend fun getVideoInfo(url: String, executor: Executor): Flow<DataState<VideoInfo>> {
        val request = VideoInfoRequest(url)
        return processor.process(executor, request)
    }

    override suspend fun getYoutubePlaylistInfo(
        url: String,
        executor: Executor,
    ): Flow<DataState<YoutubePlaylistInfo>> {
        if (!url.contains("youtube")) {
            return flow {
                emit(DataState.failure("url $url is not a youtube playlist url"))
                currentCoroutineContext().cancel(null)
            }
        }

        val request = YoutubePlaylistInfoRequest(url)
        return processor.process(executor, request)

    }

    override suspend fun downloadVideo(
        videoInfo: VideoInfo,
        audioOnly: Boolean,
        executor: Executor,
    ): Flow<DataState<DownloadInfo>> {
        val request = VideoDownloadRequest(videoInfo.originalUrl, audioOnly)
        return processor.process(executor = executor, request = request)
    }

    override suspend fun downloadPlaylist(
        playlistInfo: YoutubePlaylistInfo,
        audioOnly: Boolean,
        executor: Executor,
    ): Flow<DataState<DownloadInfo>> {
        val request = YoutubePlaylistDownloadRequest(playlistInfo.originalUrl, audioOnly)
        return processor.process(executor, request)
    }

    override suspend fun getDownloadedPlaylists(file: File) {

    }
}