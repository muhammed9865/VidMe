package com.example.vidme.presentation.viewholder

import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo


typealias PlaylistInfoListener = (playlist: YoutubePlaylistInfo) -> Unit

typealias SingleListener = (single: VideoInfo) -> Unit
