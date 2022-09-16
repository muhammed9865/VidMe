package com.example.vidme.data.repository

import com.example.vidme.data.cache.CacheDatabase
import com.example.vidme.data.downloader.DownloadProcessor
import com.example.vidme.data.mapper.toDomain
import com.example.vidme.data.mapper.toYoutubePlaylistInfo
import com.example.vidme.data.pojo.info.DownloadInfo
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.example.vidme.data.request.*
import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.YoutubePlaylistWithVideos
import com.example.vidme.domain.repository.MediaRepository
import com.example.vidme.domain.util.FileUtil
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val processor: DownloadProcessor,
    private val cache: CacheDatabase,
) :
    MediaRepository {

    override suspend fun fetchVideoInfo(
        url: String,
        executor: Executor,
        onVideoInfo: (DataState<com.example.vidme.domain.pojo.VideoInfo>) -> Unit,
    ) {

        // Checking if the video is already saved in Database
        try {
            val cachedVideo = cache.getAllVideos().find { it.originalUrl == url }
            cachedVideo?.let { foundVideo ->
                onVideoInfo(DataState.success(foundVideo.toDomain(), cached = true))
                return
            }
        } catch (e: Exception) {

        }


        val request = VideoInfoRequest(url)
        // Before returning the flow, cache the result and return the result from database
        val result = processor.process<VideoInfo>(executor, request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                cache.saveVideoInfo(data)
                val videoInfo = cache.getVideoInfo(data.id).toDomain()
                onVideoInfo(DataState.success(videoInfo))
            } else {
                onVideoInfo(DataState.failure(res.error))
            }

        }
    }

    override suspend fun fetchYoutubePlaylistInfo(
        playlistName: String,
        url: String,
        executor: Executor,
        onPlaylistInfo: (DataState<com.example.vidme.domain.pojo.YoutubePlaylistInfo>) -> Unit,
    ) {

        // Checking if there is a playlist saved with the name given or the url
        try {
            val cachedPlaylist =
                cache.getAllPlaylists().find { it.name == playlistName || it.originalUrl == url }
            cachedPlaylist?.let { foundPlaylist ->
                onPlaylistInfo(DataState.success(foundPlaylist.toDomain(), cached = true))
                return
            }

        } catch (e: Exception) {

        }


        if (!url.contains("youtube")) {
            onPlaylistInfo(DataState.failure("url $url is not a youtube playlist url"))
            return
        }

        val request = YoutubePlaylistInfoRequest(playlistName, url)

        // cache the result and return the result from database
        val result = processor.process<YoutubePlaylistInfo>(executor, request)

        result.collect { res ->
            if (res.isSuccessful) {
                val playlistInfo = res.data!!
                cache.savePlaylistInfo(playlistInfo)
                val cachedPlaylistInfo =
                    cache.getPlaylistWithVideos(playlistName)!!.playlistInfoCache.toDomain()

                onPlaylistInfo(DataState.success(cachedPlaylistInfo))
            } else {
                onPlaylistInfo(DataState.failure(res.error))
            }

        }
    }

    override suspend fun downloadVideo(
        videoInfo: com.example.vidme.domain.pojo.VideoInfo,
        audioOnly: Boolean,
        executor: Executor,
        onDownloadInfo: (DataState<com.example.vidme.domain.pojo.DownloadInfo>) -> Unit,
    ) {
        /*
            * If the video is part of a playlist then the originalUrl is the playlist Url
            * and the video id can be downloaded if it's a Youtube video.
            * Getting the Cached VideoInfo (Data layer) to get the originalUrl of the video
         */

        val cachedVideoInfo =
            cache.getVideoInfo(videoInfo.id, playlistName = videoInfo.playlistName ?: "")
        val url =
            if (cachedVideoInfo.originalUrl.contains("youtube")) cachedVideoInfo.id else cachedVideoInfo.originalUrl

        val request = VideoDownloadRequest(url, audioOnly)
        val result = processor.process<DownloadInfo>(executor = executor, request = request)

        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                Timber.d("${data.progress} : ${data.isFinished}")
                if (data.isFinished) {
                    val updatedVideoInfo = cachedVideoInfo.copy(
                        storageUrl = data.storageLocation,
                        isAudio = audioOnly,
                        isVideo = !audioOnly)

                    cache.saveVideoInfo(updatedVideoInfo)
                    // Mapped to the Domain layer
                    val downloadInfo = data.toDomain(videoInfo = updatedVideoInfo.toDomain())
                    onDownloadInfo(DataState.success(downloadInfo))

                }
                onDownloadInfo(DataState.success(data.toDomain(videoInfo = videoInfo)))

            } else {
                onDownloadInfo(DataState.failure(res.error))
            }


        }
    }

    override suspend fun downloadPlaylist(
        playlistInfo: com.example.vidme.domain.pojo.YoutubePlaylistInfo,
        audioOnly: Boolean,
        executor: Executor,
        onDownloadInfo: (DataState<com.example.vidme.domain.pojo.DownloadInfo>) -> Unit,
    ) {
        val cachedPlaylistInfo =
            cache.getPlaylistWithVideos(playlistInfo.name)!!.toYoutubePlaylistInfo()

        val request =
            YoutubePlaylistDownloadRequest(cachedPlaylistInfo, playlistInfo.originalUrl, audioOnly)

        val result = processor.process<DownloadInfo>(executor, request)

        result.collect { res ->

            if (res.isSuccessful) {
                val data = res.data!!
                if (data.currentVideoIndex != -1) {
                    // Mapping the info to domain layer
                    val downloadInfo = data.toDomain()
                    onDownloadInfo(DataState.success(downloadInfo))

                    // Updating the videoInfo being downloaded with the new storageLocation on device
                    val updatedVideoInfo =
                        cachedPlaylistInfo.videos[data.currentVideoIndex].copy(storageUrl = data.storageLocation,
                            isAudio = audioOnly,
                            isVideo = !audioOnly)

                    cache.saveVideoInfo(updatedVideoInfo)
                }
            } else {
                onDownloadInfo(DataState.failure(res.error))
            }

        }
    }

    override suspend fun deleteVideo(videoInfo: com.example.vidme.domain.pojo.VideoInfo): Boolean {
        val video = cache.getVideoInfo(videoInfo.id, videoInfo.playlistName ?: "")
        cache.deleteVideosInfo(listOf(video))
        return FileUtil.deleteVideoByName(videoName = videoInfo.title)
    }

    override suspend fun deletePlaylistByName(playlistName: String): Boolean {
        cache.deletePlaylistByName(playlistName)
        return FileUtil.deletePlaylistByName(playlistName)
    }

    override suspend fun getStoredYoutubePlaylists(): List<com.example.vidme.domain.pojo.YoutubePlaylistInfo> {
        return cache.getAllPlaylists().map { it.toDomain() }
    }

    override suspend fun getStoredYoutubePlaylistByName(playlistName: String): YoutubePlaylistWithVideos? {
        return cache.getPlaylistWithVideos(playlistName)?.toDomain()
    }

    override suspend fun getStoredVideo(
        id: String,
        playlistName: String,
    ): com.example.vidme.domain.pojo.VideoInfo {
        return cache.getVideoInfo(id, playlistName).toDomain()
    }

    override suspend fun getStoredVideos(): List<com.example.vidme.domain.pojo.VideoInfo> {
        return cache.getAllVideos().map { it.toDomain() }
    }

    override suspend fun synchronizeYoutubePlaylistInfo(
        playlistInfo: com.example.vidme.domain.pojo.YoutubePlaylistInfo,
        executor: Executor,
        onPlaylistInfo: (DataState<com.example.vidme.domain.pojo.YoutubePlaylistInfo>) -> Unit,
    ) {
        val cachedPlaylistInfo =
            cache.getPlaylistWithVideos(playlistInfo.name)!!.toYoutubePlaylistInfo()
        val request = SynchronizePlaylistRequest(cachedPlaylistInfo)

        // Before returning the flow, cache the result and return the result from database
        val result = processor.process<YoutubePlaylistInfo>(executor, request)
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!

                cache.updatePlaylistInfo(data)
                val updatedPlaylistInfo =
                    cache.getPlaylistWithVideos(playlistInfo.name)!!.playlistInfoCache.toDomain()
                onPlaylistInfo(DataState.success(updatedPlaylistInfo))

            } else {
                onPlaylistInfo(DataState.failure(res.error))
            }
        }
    }

}