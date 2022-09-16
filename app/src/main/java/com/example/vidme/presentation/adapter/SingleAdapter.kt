package com.example.vidme.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.vidme.databinding.ListItemSingleInfoBinding
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.presentation.viewholder.SingleListener
import com.example.vidme.presentation.viewholder.SingleViewHolder

class SingleAdapter :
    ListAdapter<VideoInfo, SingleViewHolder>(SingleDiffUtil()) {

    private var syncListener: SingleListener? = null
    private var itemClickedListener: SingleListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
        val binding =
            ListItemSingleInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
        holder.bind(getItem(position), syncListener, itemClickedListener)

    }


    fun setOnDownloadListener(listener: SingleListener) {
        this.syncListener = listener
    }


    fun setOnItemClickListener(listener: SingleListener) {
        this.itemClickedListener = listener
    }

    class SingleDiffUtil : DiffUtil.ItemCallback<VideoInfo>() {
        override fun areItemsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
            return oldItem == newItem
        }
    }
}

