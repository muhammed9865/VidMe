package com.example.vidme.presentation.viewholder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vidme.R
import com.example.vidme.databinding.ListItemSingleInfoBinding
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.util.StringUtil
import com.example.vidme.presentation.callback.SingleDownloadState
import com.example.vidme.presentation.util.loadImage
import com.example.vidme.presentation.util.visibility
import timber.log.Timber

class SingleViewHolder(private val binding: ListItemSingleInfoBinding) :
    RecyclerView.ViewHolder(binding.root), SingleDownloadState {

    private lateinit var currentSingleID: String

    // The single that will be sent on any button click
    // Another instance of so it can be updated during the download
    private lateinit var currSingle: VideoInfo
    fun bind(
        single: VideoInfo,
        clickListener: SingleListener?,
        onDownloadListener: SingleDownloadListener?,
    ) = with(binding) {
        currentSingleID = single.id

        currSingle = single

        singleName.text = single.title
        singleThumbnail.loadImage(single.thumbnail)
        val duration = StringUtil.calculateDuration(single.duration)
        singleDuration.text = StringUtil.durationAsString(duration)



        manageVisibility(single)

        // on Item click listener
        root.setOnClickListener { clickListener?.invoke(currSingle) }

        downloadBtn.setOnClickListener {
            onDownloadListener?.invoke(currSingle, this@SingleViewHolder)
        }

    }

    private fun onDownload(downloadInfo: DownloadInfo) {
        with(binding) {
            downloadProgressPb.progress = downloadInfo.progress.toInt()
            val timeText = itemView.context.getString(R.string.time_remaining,
                StringUtil.durationAsString(downloadInfo.timeRemaining.toInt()))
            timeRemainingTxt.text = timeText

        }
    }

    private fun manageVisibility(single: VideoInfo) = with(binding) {
        val typeImgID = if (single.isAudio) R.drawable.ic_audio else R.drawable.ic_video
        singleTypeImg.setImageDrawable(ContextCompat.getDrawable(root.context, typeImgID))
        downloadBtn.visibility(!single.isDownloaded)
        singleTypeImg.visibility(single.isDownloaded)

        root.strokeWidth = if (single.isSelected) {
            5
        } else 0

    }

    override fun onDownloading(downloadInfo: DownloadInfo) {
        if (downloadInfo.videoInfo?.id == currentSingleID) {
            currSingle = downloadInfo.videoInfo

            binding.downloadingViews.visibility(!downloadInfo.isFinished)
            onDownload(downloadInfo)
            binding.downloadBtn.isEnabled = false
        } else {
            binding.downloadingViews.visibility(false)
            binding.downloadBtn.isEnabled = true
        }
    }

    override fun onFinished(videoInfo: VideoInfo) {
        Timber.d(videoInfo.toString())
        manageVisibility(videoInfo)
        currSingle = videoInfo
        binding.downloadingViews.visibility(false)
        binding.downloadBtn.isEnabled = true
    }

    override fun onFailure(videoInfo: VideoInfo) {
        binding.downloadingViews.visibility(false)
        binding.downloadBtn.isEnabled = true
    }
}