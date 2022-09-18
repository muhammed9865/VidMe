package com.example.vidme.presentation.fragment.single.singles_home

import com.example.vidme.domain.pojo.VideoInfo

sealed interface SingleFilter {
    fun filter(list: List<VideoInfo>, currentDownloadIDs: List<String>): List<VideoInfo>

    object All : SingleFilter {
        override fun filter(
            list: List<VideoInfo>,
            currentDownloadIDs: List<String>,
        ): List<VideoInfo> {
            return list
        }
    }

    object Video : SingleFilter {
        override fun filter(
            list: List<VideoInfo>,
            currentDownloadIDs: List<String>,
        ): List<VideoInfo> {
            return list.filter { it.isVideo || it.id in currentDownloadIDs }
        }
    }

    object Audio : SingleFilter {
        override fun filter(
            list: List<VideoInfo>,
            currentDownloadIDs: List<String>,
        ): List<VideoInfo> {
            return list.filter { it.isAudio || it.id in currentDownloadIDs }
        }
    }

    object DownloadedOnly : SingleFilter {
        override fun filter(
            list: List<VideoInfo>,
            currentDownloadIDs: List<String>,
        ): List<VideoInfo> {
            return list.filter { it.isDownloaded || it.id in currentDownloadIDs }
        }
    }

}