package com.example.vidme.data.downloader

import com.example.vidme.data.extractor.DownloadInfoExtractor
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.pojo.info.DownloadInfo
import com.example.vidme.data.pojo.info.Info
import com.example.vidme.data.request.DownloadRequest
import com.example.vidme.domain.DataState
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext", "unchecked_cast")
class DownloadProcessor @Inject constructor(
    private val ytInstance: YoutubeDL,
    private val logger: DownloadLogger,
) : Processor {

    private var executor: Executor? = null
    private var request: DownloadRequest? = null
    private var timeoutCount = 0
    private val retryCount = 2

    @Suppress("LocalVariableName")
    override fun <T : Info> process(
        executor: Executor,
        request: DownloadRequest,
    ): Flow<DataState<T>> {
        this.executor = executor
        this.request = request
        return callbackFlow {
            executor.execute {
                logger.log("DownloadProcessor: Starting Process...")
                try {
                    val _request = request.getRequest()
                    logger.log(_request.buildCommand().toString())

                    // Handling the case that actually downloading the request
                    val isDownloading = request.isDownloading()
                    val extractor: InfoExtractor = request.getExtractor()
                    val lines = mutableMapOf<Int, String>()
                    var linesCount = 0

                    var result = DownloadInfo()

                    /*
                        * execute lambda function isn't a real callback
                        * Means it will exit the lambda scope when the execute finishes and returns all the output
                        * Code after the scope will be blocked till the execute finishes
                     */

                    ytInstance.execute(_request) { progress: Float, timeRemaining: Long, line: String ->
                        lines[linesCount] = line
                        linesCount++
                        if (isDownloading)
                            result = onDownloading(extractor, progress, timeRemaining, lines, this)
                    }

                    onFinish(result, isDownloading, extractor, lines, this)

                    close()

                } catch (e: YoutubeDLException) {
                    timeoutCount++
                    retryProcess(executor, request, this)
                    e.printStackTrace()

                } catch (e: CancellationException) {

                }
            }
            awaitClose {

            }
        }
    }

    private fun <T : Info> retryProcess(
        executor: Executor,
        request: DownloadRequest,
        scope: ProducerScope<DataState<T>>,
    ) = with(scope) {
        Timber.i("Retrying Process: $timeoutCount/$retryCount")
        if (timeoutCount < retryCount) {
            process<T>(executor, request).onEach {
                trySend(it)
            }.launchIn(this)
        } else {
            trySendBlocking(
                DataState.failure("Something went wrong")
            )
            cancel()
        }
    }

    private fun <T : Info> onFinish(
        result: DownloadInfo,
        isDownloading: Boolean,
        extractor: InfoExtractor,
        lines: Map<Int, String>,
        scope: ProducerScope<DataState<T>>,
    ) = with(scope) {
        logger.log("Processor: Out of execute scope")
        logger.log(lines)

        if (isDownloading)
            trySendBlocking(
                DataState.success(result.copy(isFinished = true)) as DataState<T>
            )

        if (!isDownloading) {
            val downloadResult =
                extractor.extract(outputLines = lines,
                    originalUrl = request?.url
                        ?: throw NullPointerException("DownloadRequest cannot be null"))

            trySendBlocking(
                DataState.success(data = downloadResult) as DataState<T>
            )
        }
    }

    private fun <T : Info> onDownloading(
        extractor: InfoExtractor,
        progress: Float,
        timeRemaining: Long,
        lines: Map<Int, String>,
        scope: ProducerScope<DataState<T>>,
    ): DownloadInfo {

        val result = (extractor as DownloadInfoExtractor).extract(
            progress,
            timeRemaining,
            lines,
        )

        // Casting to DataState<T> to escape a compiler error.
        scope.trySendBlocking(
            DataState.success(data = result) as DataState<T>
        )
        return result
    }

}