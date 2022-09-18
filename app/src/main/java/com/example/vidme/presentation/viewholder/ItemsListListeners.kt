package com.example.vidme.presentation.viewholder

import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.presentation.callback.SingleDownloadState


typealias PlaylistInfoListener = (playlist: YoutubePlaylistInfo) -> Unit

typealias SingleListener = (single: VideoInfo) -> Unit
typealias SingleDownloadListener = (single: VideoInfo, listener: SingleDownloadState) -> Unit
