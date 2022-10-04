package com.example.vidme.presentation.fragment.common

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.vidme.R
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.presentation.adapter.CommonAdapter
import com.example.vidme.presentation.adapter.PlaylistInfoAdapter
import com.example.vidme.presentation.adapter.SingleAdapter
import com.example.vidme.presentation.util.DialogsUtil
import com.example.vidme.presentation.util.RecyclerViewUtil
import com.example.vidme.presentation.util.RecyclerViewUtil.Companion.setSwipeToDelete

class FragmentsCommonImpl : FragmentsCommon {
    override fun enableSwipeToDelete(
        adapter: CommonAdapter,
        recyclerView: RecyclerView,
        onDeleteSingle: (single: VideoInfo) -> Unit,
        onDeletePlaylist: (playlist: YoutubePlaylistInfo) -> Unit,
    ) {
        recyclerView.setSwipeToDelete(RecyclerViewUtil.LEFT) { _, position ->
            when (adapter) {
                is SingleAdapter -> {
                    val single = adapter.currentList[position]
                    deleteSingle(recyclerView.context, single, single.title, onDeleteSingle)
                }

                is PlaylistInfoAdapter -> {
                    val playlist = adapter.currentList[position]
                    deletePlaylist(recyclerView.context, playlist, playlist.name, onDeletePlaylist)
                }
            }


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

    override fun deletePlaylist(
        context: Context,
        playlist: YoutubePlaylistInfo,
        title: String,
        onDelete: (playlist: YoutubePlaylistInfo) -> Unit,
    ) {
        with(context) {
            val dialogTitle = getString(R.string.deleting, title)
            val content = getString(R.string.confirm_deleting, title)
            DialogsUtil.showChoiceDialog(
                context,
                dialogTitle,
                content,
                onOKPressed = {
                    onDelete(playlist)
                }
            )
        }
    }
}