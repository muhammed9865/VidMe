package com.example.vidme.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.vidme.databinding.ListItemPlaylistInfoBinding
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.presentation.viewholder.PlaylistInfoListener
import com.example.vidme.presentation.viewholder.PlaylistInfoViewHolder
import timber.log.Timber

class PlaylistInfoAdapter :
    ListAdapter<YoutubePlaylistInfo, PlaylistInfoViewHolder>(PlaylistInfoDiffUtil()) {

    private var syncListener: PlaylistInfoListener? = null
    private var itemClickedListener: PlaylistInfoListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistInfoViewHolder {
        val binding =
            ListItemPlaylistInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistInfoViewHolder, position: Int) {
        holder.bind(getItem(position), syncListener, itemClickedListener)

    }

    override fun submitList(list: List<YoutubePlaylistInfo>?) {
        super.submitList(list)
        Timber.d(list.toString())
    }


    fun setOnSyncPressedListener(listener: PlaylistInfoListener) {
        this.syncListener = listener
    }


    fun setOnItemClickListener(listener: PlaylistInfoListener) {
        this.itemClickedListener = listener
    }

    class PlaylistInfoDiffUtil : DiffUtil.ItemCallback<YoutubePlaylistInfo>() {
        override fun areItemsTheSame(
            oldItem: YoutubePlaylistInfo,
            newItem: YoutubePlaylistInfo,
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: YoutubePlaylistInfo,
            newItem: YoutubePlaylistInfo,
        ): Boolean {
            return oldItem == newItem
        }
    }
}

