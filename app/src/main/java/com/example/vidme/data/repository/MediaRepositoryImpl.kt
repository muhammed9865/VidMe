package com.example.vidme.data.repository

import com.example.vidme.data.cache.CacheDatabase
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

class MediaRepositoryImpl @Inject constructor(
    private val processor: DownloadProcessor,
    private val cache: CacheDatabase,
) :
    MediaRepository {

    override suspend fun getVideoInfo(url: String, executor: Executor): Flow<DataState<VideoInfo>> {
        val request = VideoInfoRequest(url)
        // Before returning the flow, cache the result and return the result from database
        val result = processor.process<VideoInfo>(executor, request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                cache.saveVideoInfo(data)
            }
        }
        return result
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

        // Before returning the flow, cache the result and return the result from database
        val result = processor.process<YoutubePlaylistInfo>(executor, request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                cache.savePlaylistInfo(data)
            }
        }
        return result

    }

    override suspend fun downloadVideo(
        videoInfo: VideoInfo,
        audioOnly: Boolean,
        executor: Executor,
    ): Flow<DataState<DownloadInfo>> {
        val request = VideoDownloadRequest(videoInfo.originalUrl, audioOnly)
        // TODO Collect the flow and update the videoInfo storageUrl to DownloadInfo storageLocation
        val result = processor.process<DownloadInfo>(executor = executor, request = request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                val updatedVideoInfo = videoInfo.copy(storageUrl = data.storageLocation)
                cache.saveVideoInfo(updatedVideoInfo)
            }
        }
        return result
    }

    override suspend fun downloadPlaylist(
        playlistInfo: YoutubePlaylistInfo,
        audioOnly: Boolean,
        executor: Executor,
    ): Flow<DataState<DownloadInfo>> {
        val request =
            YoutubePlaylistDownloadRequest(playlistInfo.name, playlistInfo.originalUrl, audioOnly)
        // TODO Collect the flow and update each videoInfo storageUrl based on DownloadInfo currentVideoIndex
        val result = processor.process<DownloadInfo>(executor, request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                val updatedVideoInfo =
                    playlistInfo.videos[data.currentVideoIndex].copy(storageUrl = data.storageLocation)
                cache.saveVideoInfo(updatedVideoInfo)
            }
        }
        return result
    }

    override suspend fun getDownloadedPlaylists(file: File) {

    }
}