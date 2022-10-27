package com.example.vidme.data.repository

import com.example.vidme.data.cache.CacheDatabase
import com.example.vidme.data.downloader.Processor
import com.example.vidme.data.mapper.toDomain
import com.example.vidme.data.mapper.toYoutubePlaylistInfo
import com.example.vidme.data.pojo.info.DownloadInfo
import com.example.vidme.data.pojo.info.Info
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.example.vidme.data.request.*
import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.YoutubePlaylistWithVideos
import com.example.vidme.domain.pojo.request.PlaylistRequest
import com.example.vidme.domain.pojo.request.VideoRequest
import com.example.vidme.domain.repository.MediaRepository
import com.example.vidme.domain.util.FileUtil
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val processor: Processor,
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
        val result = collectProcessorFlow<VideoInfo>(executor, request)
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
        val result = collectProcessorFlow<YoutubePlaylistInfo>(executor, request)

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
        videoRequest: VideoRequest,
        executor: Executor,
        onDownloadInfo: (DataState<com.example.vidme.domain.pojo.DownloadInfo>) -> Unit,
    ) {
        /*
            * If the video is part of a playlist then the originalUrl is the playlist Url
            * and the video id can be downloaded if it's a Youtube video.
            * Getting the Cached VideoInfo (Data layer) to get the originalUrl of the video
         */

        val cachedVideoInfo =
            cache.getVideoInfo(id = videoRequest.videoInfo?.id ?: error("VideoInfo can't be null"),
                playlistName = videoRequest.videoInfo.playlistName ?: "")

        val audioOnly = videoRequest.isAudio()

        // Check if audio/video is already downloaded
        // @return [DownloadInfo] with updated values
        getMediaIfDownloaded(cachedVideoInfo.title)?.let { url ->
            Timber.d(url)
            val updatedVideoInfo = cachedVideoInfo.copy(
                storageUrl = url,
                isAudio = audioOnly,
                isVideo = !audioOnly)

            cache.saveVideoInfo(updatedVideoInfo)

            val downloadInfo = DownloadInfo(100f,
                0,
                -1,
                url,
                true).toDomain(videoInfo = updatedVideoInfo.toDomain()
                .copy(isDownloading = false, isDownloaded = true))

            onDownloadInfo(DataState.success(downloadInfo))
            return
        }

        val url = getUrl(cachedVideoInfo)


        val request = VideoDownloadRequest(url, videoRequest)
        val result = collectProcessorFlow<DownloadInfo>(executor = executor, request = request)

        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                Timber.d("${data.progress} : ${data.isFinished}")
                /*
                    * if download is finished, cache the new video with the storageUrl
                    * Else just send the current downloadInfo
                 */
                if (data.isFinished) {
                    val updatedVideoInfo = cachedVideoInfo.copy(
                        storageUrl = data.storageLocation,
                        isAudio = audioOnly,
                        isVideo = !audioOnly,
                    )

                    cache.saveVideoInfo(updatedVideoInfo)
                    // Mapped to the Domain layer
                    val downloadInfo = data.toDomain(videoInfo = updatedVideoInfo.toDomain()
                        .copy(isDownloading = false, isDownloaded = true))
                    onDownloadInfo(DataState.success(downloadInfo))

                } else {
                    onDownloadInfo(DataState.success(data.toDomain(videoInfo = videoRequest.videoInfo.copy(
                        isDownloaded = false,
                        isDownloading = true))))
                }

            } else {
                onDownloadInfo(DataState.failure(res.error))
            }


        }
    }

    private fun getUrl(videoInfo: VideoInfo): String {
        return if (videoInfo.playlistName.isNotEmpty()) {
            videoInfo.id
        } else videoInfo.originalUrl
    }

    private fun getMediaIfDownloaded(name: String): String? {
        val result = FileUtil.checkIfMediaExists(name)

        return result.first
    }


    private var lastPlaylistIndex = -1
    private var lastStorageLocation = ""
    private var edited = false

    override suspend fun downloadPlaylist(
        playlistRequest: PlaylistRequest,
        executor: Executor,
        onDownloadInfo: (DataState<com.example.vidme.domain.pojo.DownloadInfo>) -> Unit,
    ) {
        val cachedPlaylistInfo =
            cache.getPlaylistWithVideos(playlistRequest.playlistInfo!!.name)!!
                .toYoutubePlaylistInfo()

        val request =
            YoutubePlaylistDownloadRequest(playlistRequest)

        val result = collectProcessorFlow<DownloadInfo>(executor, request)

        // Edited will be used if a video isn't valid so the [lastPlaylistIndex] will be decreased
        result.collect { res ->
            if (res.isSuccessful) {
                val data = res.data!!
                if (data.currentVideoIndex != -1) {
                    if (lastPlaylistIndex != data.currentVideoIndex && lastStorageLocation != data.storageLocation) {
                        edited = false
                        lastPlaylistIndex = data.currentVideoIndex
                        lastStorageLocation = data.storageLocation

                        Timber.d("Updated : $lastPlaylistIndex : $lastStorageLocation")
                        // Updating the videoInfo being downloaded with the new storageLocation on device
                        val updatedVideoInfo =
                            cachedPlaylistInfo.videos[data.currentVideoIndex].copy(storageUrl = data.storageLocation,
                                isAudio = playlistRequest.isAudio(),
                                isVideo = !playlistRequest.isAudio())
                        cache.saveVideoInfo(updatedVideoInfo)

                    }

                    val downloadInfo =
                        data.toDomain(cachedPlaylistInfo.videos[lastPlaylistIndex].toDomain())
                    onDownloadInfo(DataState.success(downloadInfo))
                    Timber.i("Out -> ${data.currentVideoIndex} : ${data.storageLocation}, isFinished: ${data.isFinished}")
                }

            } else {
                onDownloadInfo(DataState.failure(res.error))
            }

        }
    }

    override suspend fun deleteVideo(videoInfo: com.example.vidme.domain.pojo.VideoInfo): Boolean {
        val video = cache.getVideoInfo(videoInfo.id, videoInfo.playlistName ?: "")
        cache.deleteVideosInfo(listOf(video))
        return FileUtil.deleteVideoByName(location = video.storageUrl).also {
            Timber.d("File deleted: $it")
        }
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
        val result = collectProcessorFlow<YoutubePlaylistInfo>(executor, request)
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

    private fun <T : Info> collectProcessorFlow(
        executor: Executor,
        request: DownloadRequest,
    ): Flow<DataState<T>> {
        return processor.process(executor, request)
    }

}