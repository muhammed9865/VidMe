package com.example.vidme.presentation.fragment.common

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.presentation.adapter.CommonAdapter

interface FragmentsCommon {
    fun enableSwipeToDelete(
        adapter: CommonAdapter,
        recyclerView: RecyclerView,
        onDeleteSingle: (single: VideoInfo) -> Unit = {},
        onDeletePlaylist: (playlist: YoutubePlaylistInfo) -> Unit = {},
    )

    fun deleteSingle(
        context: Context,
        single: VideoInfo,
        title: String,
        onDelete: (single: VideoInfo) -> Unit,
    )

    fun deletePlaylist(
        context: Context,
        playlist: YoutubePlaylistInfo,
        title: String,
        onDelete: (single: YoutubePlaylistInfo) -> Unit,
    )

}