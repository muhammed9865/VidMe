package com.example.vidme.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.vidme.databinding.ListItemSingleInfoBinding
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.presentation.viewholder.SingleDownloadListener
import com.example.vidme.presentation.viewholder.SingleListener
import com.example.vidme.presentation.viewholder.SingleViewHolder

class SingleAdapter :
    ListAdapter<VideoInfo, SingleViewHolder>(SingleDiffUtil()) {

    private var downloadListener: SingleDownloadListener? = null
    private var itemClickedListener: SingleListener? = null
    private var marginsEnabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
        val binding =
            ListItemSingleInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
        if (position == currentList.lastIndex && marginsEnabled) {
            setMargins(holder.itemView, b = 200)
        }

        holder.bind(getItem(position), itemClickedListener, downloadListener)

    }

    fun enableMargins(enable: Boolean) {
        marginsEnabled = enable
    }


    private fun setMargins(
        view: View,
        s: Int = view.marginStart,
        t: Int = view.marginTop,
        e: Int = view.marginEnd,
        b: Int = view.marginBottom,
    ) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(s, t, e, b)
        view.layoutParams = params

    }


    fun setOnDownloadListener(listener: SingleDownloadListener) {
        this.downloadListener = listener
    }


    fun setOnItemClickListener(listener: SingleListener) {
        this.itemClickedListener = listener
    }

    override fun submitList(list: List<VideoInfo>?) {
        super.submitList(list)
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

