package com.example.vidme.presentation.viewholder

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.RecyclerView
import com.example.vidme.R
import com.example.vidme.databinding.ListItemPlaylistInfoBinding
import com.example.vidme.domain.pojo.YoutubePlaylistInfo

class PlaylistInfoViewHolder(private val binding: ListItemPlaylistInfoBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        playlistInfo: YoutubePlaylistInfo,
        syncListener: PlaylistInfoListener?,
        itemClickedListener: PlaylistInfoListener?,
    ) = with(binding) {
        val context = itemView.context
        playlistName.text = playlistInfo.name
        val count = context.getString(R.string.files_count, playlistInfo.count)
        filesCount.text = count
        val lastSynced = context.getString(R.string.last_synced, playlistInfo.lastSynced)
        lastSyncedTxt.text = lastSynced

        if (!playlistInfo.isSyncing) {
            syncBtn.clearAnimation()
        } else {
            syncBtn.isEnabled = false
            animateSyncBtn(syncBtn)
        }


        syncBtn.setOnClickListener {
            animateSyncBtn(it)
            playlistInfo.isSyncing = true
            syncBtn.isEnabled = false
            syncListener?.invoke(playlistInfo)
        }

        playlistItem.setOnClickListener {
            itemClickedListener?.invoke(playlistInfo)
        }
    }

    private fun animateSyncBtn(btn: View) {
        val animation = RotateAnimation(0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f)
        animation.repeatCount = Animation.INFINITE
        animation.duration = 1000
        btn.startAnimation(animation)
    }
}