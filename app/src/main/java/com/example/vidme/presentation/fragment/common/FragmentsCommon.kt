package com.example.vidme.presentation.fragment.common

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.presentation.adapter.SingleAdapter

interface FragmentsCommon {
    fun enableSwipeToDelete(
        adapter: SingleAdapter,
        recyclerView: RecyclerView,
        onDelete: (single: VideoInfo) -> Unit,
    )

    fun deleteSingle(
        context: Context,
        single: VideoInfo,
        title: String,
        onDelete: (single: VideoInfo) -> Unit,
    )

}