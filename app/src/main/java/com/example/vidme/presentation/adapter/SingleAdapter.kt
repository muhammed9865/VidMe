package com.example.vidme.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.vidme.databinding.ListItemSingleInfoBinding
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.presentation.util.setMargins
import com.example.vidme.presentation.viewholder.SingleDownloadListener
import com.example.vidme.presentation.viewholder.SingleListener
import com.example.vidme.presentation.viewholder.SingleViewHolder

class SingleAdapter :
    ListAdapter<VideoInfo, SingleViewHolder>(SingleDiffUtil()), CommonAdapter {

    private var downloadListener: SingleDownloadListener? = null
    private var itemClickedListener: SingleListener? = null
    private var marginsEnabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
        val binding =
            ListItemSingleInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
        val view = holder.itemView
        if (position == currentList.lastIndex && marginsEnabled) {
            view.setMargins(b = 200)
        } else if (position != currentList.lastIndex && marginsEnabled) {
            val context = view.context
            val top = context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp).toInt()
            val bottom = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp).toInt()
            view.setMargins(t = top, b = bottom)
        }

        holder.bind(getItem(position), itemClickedListener, downloadListener)

    }

    fun enableMargins(enable: Boolean) {
        marginsEnabled = enable
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

