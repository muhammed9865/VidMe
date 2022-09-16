package com.example.vidme.presentation.viewholder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vidme.R
import com.example.vidme.databinding.ListItemSingleInfoBinding
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.util.StringUtil
import com.example.vidme.presentation.util.loadImage
import com.example.vidme.presentation.util.visibility

class SingleViewHolder(private val binding: ListItemSingleInfoBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        single: VideoInfo,
        clickListener: SingleListener?,
        onDownloadListener: SingleListener?,
    ) = with(binding) {
        val context = itemView.context
        singleName.text = single.title
        singleThumbnail.loadImage(single.thumbnail)
        val duration = StringUtil.calculateDuration(single.duration)
        singleDuration.text = StringUtil.durationAsString(duration)

        val typeImgID = if (single.isAudio) R.drawable.ic_audio else R.drawable.ic_video
        singleTypeImg.setBackgroundDrawable(ContextCompat.getDrawable(context, typeImgID))

        manageVisibility(single)

        // on Item click listener
        root.setOnClickListener { clickListener?.invoke(single) }

        downloadBtn.setOnClickListener {
            onDownloadListener?.invoke(single)
        }

        single.downloadInfo?.let {
            onDownloading(it)
        }
    }

    private fun onDownloading(downloadInfo: DownloadInfo) {
        with(binding) {
            downloadProgressPb.progress = downloadInfo.progress.toInt()
            timeRemainingTxt.text = StringUtil.durationAsString(downloadInfo.timeRemaining.toInt())
        }
    }

    private fun manageVisibility(single: VideoInfo) = with(binding) {
        downloadingViews.visibility(single.downloadInfo != null)
        downloadBtn.visibility(!single.isDownloaded)
        singleTypeImg.visibility(single.isDownloaded)
    }
}