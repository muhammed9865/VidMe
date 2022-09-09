package com.example.vidme.data.downloader

import com.example.vidme.data.extractor.DownloadInfoExtractor
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.pojo.info.Info
import com.example.vidme.data.request.DownloadRequest
import com.example.vidme.domain.DataState
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class DownloadProcessor @Inject constructor(private val ytInstance: YoutubeDL) {

    @Suppress("unchecked_cast", "unused", "LocalVariableName")
    suspend fun <T : Info> process(
        url: String,
        videoId: String,
        audioOnly: Boolean = false,
        request: DownloadRequest,
    ): Flow<DataState<T>> {
        return callbackFlow {
            try {

                val _request = request.getRequest(url, audioOnly)
                // Handling the case that actually downloading the request
                val isDownloading = request.getExtractor() is DownloadInfoExtractor
                val extractor: InfoExtractor = request.getExtractor()
                val lines = mutableMapOf<Int, String>()
                var count = 0

                ytInstance.execute(_request) { progress: Float, timeRemaining: Long, line: String ->
                    lines[count] = line
                    count++
                    if (isDownloading) {
                        val result = (extractor as DownloadInfoExtractor).extract(
                            videoId,
                            progress,
                            timeRemaining,
                            lines
                        )
                        // Casting to DataState<T> to escape a compiler error.
                        trySendBlocking(
                            DataState.success(data = result) as DataState<T>
                        )
                    }
                }
                if (!isDownloading) {
                    val result = extractor.extract(lines)
                    trySendBlocking(
                        DataState.success(data = result) as DataState<T>
                    )
                }
                close()

            } catch (e: YoutubeDLException) {
                trySendBlocking(
                    DataState.failure(error = e.message)
                )
                cancel(e.message.toString())
            }
            awaitClose {

            }
        }
    }


}