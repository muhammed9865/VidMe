package com.example.vidme.presentation.fragment.common

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.vidme.R
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.presentation.adapter.SingleAdapter
import com.example.vidme.presentation.util.DialogsUtil
import com.example.vidme.presentation.util.RecyclerViewUtil
import com.example.vidme.presentation.util.RecyclerViewUtil.Companion.setSwipeToDelete

class FragmentsCommonImpl : FragmentsCommon {
    override fun enableSwipeToDelete(
        adapter: SingleAdapter,
        recyclerView: RecyclerView,
        onDelete: (single: VideoInfo) -> Unit,
    ) {
        recyclerView.setSwipeToDelete(RecyclerViewUtil.LEFT) { _, position ->
            val single = adapter.currentList[position]
            deleteSingle(recyclerView.context, single, single.title, onDelete)
        }
    }

    override fun deleteSingle(
        context: Context,
        single: VideoInfo,
        title: String,
        onDelete: (single: VideoInfo) -> Unit,
    ) {
        with(context) {
            val dialogTitle = getString(R.string.deleting, title)
            val content = getString(R.string.confirm_deleting, title)
            DialogsUtil.showChoiceDialog(
                context,
                dialogTitle,
                content,
                onOKPressed = {
                    onDelete(single)
                }
            )
        }
    }
}