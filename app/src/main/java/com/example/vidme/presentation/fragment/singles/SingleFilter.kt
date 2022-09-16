package com.example.vidme.presentation.fragment.singles

import com.example.vidme.domain.pojo.VideoInfo

sealed interface SingleFilter {
    fun filter(list: List<VideoInfo>): List<VideoInfo>

    object All : SingleFilter {
        override fun filter(list: List<VideoInfo>): List<VideoInfo> {
            return list
        }
    }

    object Video : SingleFilter {
        override fun filter(list: List<VideoInfo>): List<VideoInfo> {
            return list.filter { it.isVideo }
        }
    }

    object Audio : SingleFilter {
        override fun filter(list: List<VideoInfo>): List<VideoInfo> {
            return list.filter { it.isAudio }
        }
    }

    object DownloadedOnly : SingleFilter {
        override fun filter(list: List<VideoInfo>): List<VideoInfo> {
            return list.filter { it.isDownloaded }
        }
    }

}