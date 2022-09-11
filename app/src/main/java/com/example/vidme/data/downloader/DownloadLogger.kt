package com.example.vidme.data.downloader

import timber.log.Timber
import javax.inject.Inject


class DownloadLogger @Inject constructor() {
    fun log(lines: Map<Int, String>) {
        lines.forEach { i, s ->
            Timber.d("Download $i: $s")
        }
    }
}