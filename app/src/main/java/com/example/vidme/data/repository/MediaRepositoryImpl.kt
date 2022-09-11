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
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val processor: DownloadProcessor,
    private val cache: CacheDatabase,
) :
    MediaRepository {

    override suspend fun getVideoInfo(
        url: String,
        executor: Executor,
        onVideoInfo: (DataState<VideoInfo>) -> Unit,
    ) {
        val request = VideoInfoRequest(url)
        // Before returning the flow, cache the result and return the result from database
        val result = processor.process<VideoInfo>(executor, request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                cache.saveVideoInfo(data)
            }
            onVideoInfo(res)
        }
    }

    override suspend fun getYoutubePlaylistInfo(
        playlistName: String,
        url: String,
        executor: Executor,
        onPlaylistInfo: (DataState<YoutubePlaylistInfo>) -> Unit,
    ) {
        if (!url.contains("youtube")) {
            onPlaylistInfo(DataState.failure("url $url is not a youtube playlist url"))
            return
        }
        val request = YoutubePlaylistInfoRequest(playlistName, url)

        // Before returning the flow, cache the result and return the result from database
        val result = processor.process<YoutubePlaylistInfo>(executor, request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                cache.savePlaylistInfo(data)
            }
            onPlaylistInfo(res)
        }
    }

    override suspend fun downloadVideo(
        videoInfo: VideoInfo,
        audioOnly: Boolean,
        executor: Executor,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    ) {
        val request = VideoDownloadRequest(videoInfo.originalUrl, audioOnly)
        val result = processor.process<DownloadInfo>(executor = executor, request = request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                val updatedVideoInfo = videoInfo.copy(storageUrl = data.storageLocation,
                    isAudio = audioOnly,
                    isVideo = !audioOnly)
                cache.saveVideoInfo(updatedVideoInfo)
            }
            onDownloadInfo(res)
        }
    }

    override suspend fun downloadPlaylist(
        playlistInfo: YoutubePlaylistInfo,
        audioOnly: Boolean,
        executor: Executor,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    ) {
        val request =
            YoutubePlaylistDownloadRequest(playlistInfo, playlistInfo.originalUrl, audioOnly)
        val result = processor.process<DownloadInfo>(executor, request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                if (data.currentVideoIndex != -1) {
                    val updatedVideoInfo =
                        playlistInfo.videos[data.currentVideoIndex].copy(storageUrl = data.storageLocation)

                    cache.saveVideoInfo(updatedVideoInfo)
                }
            }
            onDownloadInfo(res)
        }
    }

    override suspend fun getDownloadedPlaylists(file: File) {

    }
}