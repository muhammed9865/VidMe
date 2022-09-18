package com.example.vidme.data.downloader

import com.example.vidme.data.pojo.info.Info
import com.example.vidme.data.request.DownloadRequest
import com.example.vidme.domain.DataState
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executor

interface Processor {
    fun <T : Info> process(
        executor: Executor,
        request: DownloadRequest,
    ): Flow<DataState<T>>
}