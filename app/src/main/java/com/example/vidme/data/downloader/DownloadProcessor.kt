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
import timber.log.Timber
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class DownloadProcessor @Inject constructor(
    private val ytInstance: YoutubeDL,
    private val logger: DownloadLogger,
) {

    private var lastProgress = -1f
        set(value) {
            if (value != -1f) field = value
        }

    @Suppress("unchecked_cast", "unused", "LocalVariableName")
    fun <T : Info> process(
        executor: Executor,
        request: DownloadRequest,
    ): Flow<DataState<T>> {
        return callbackFlow {
            executor.execute {
                logger.log(mapOf(0 to "DownloadProcessor: Starting Process..."))
                try {
                    val _request = request.getRequest()
                    // Handling the case that actually downloading the request
                    val isDownloading = request.getExtractor() is DownloadInfoExtractor
                    val extractor: InfoExtractor = request.getExtractor()
                    val lines = mutableMapOf<Int, String>()
                    var count = 0
                    Timber.d(_request.buildCommand().toString())

                    ytInstance.execute(_request) { progress: Float, timeRemaining: Long, line: String ->
                        lines[count] = line
                        count++
                        if (isDownloading) {
                            val result = (extractor as DownloadInfoExtractor).extract(
                                progress,
                                timeRemaining,
                                lines,
                            )
                            Timber.d("$progress : $lastProgress")
                            lastProgress = progress

                            // Casting to DataState<T> to escape a compiler error.
                            trySend(
                                DataState.success(data = result.copy(isFinished = progress == -1f && lastProgress != -1f)) as DataState<T>
                            )
                        }
                    }


                    // logger.log(lines)

                    if (!isDownloading) {
                        val result =
                            extractor.extract(outputLines = lines, originalUrl = request.url)

                        trySendBlocking(
                            DataState.success(data = result) as DataState<T>
                        )
                    }
                    close()

                } catch (e: YoutubeDLException) {
                    trySendBlocking(
                        DataState.failure("Something went wrong")
                    )
                    e.printStackTrace()
                    cancel()
                } catch (e: CancellationException) {

                }
            }
            awaitClose {

            }
        }
    }


}