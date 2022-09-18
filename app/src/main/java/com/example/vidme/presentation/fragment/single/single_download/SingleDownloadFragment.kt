package com.example.vidme.presentation.fragment.single.single_download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.FragmentDownloadSingleBinding
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.VideoRequest
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.util.loadImage
import com.example.vidme.presentation.util.showErrorSnackBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class SingleDownloadFragment : BottomSheetDialogFragment() {
    private val viewModel by viewModels<SingleDownloadViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private var _binding: FragmentDownloadSingleBinding? = null
    private val binding get() = _binding!!

    private var onDownloadClickListener: ((VideoRequest) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDownloadSingleBinding.inflate(layoutInflater)

        mainViewModel.selectedSingle.onEach {
            if (it != null) {
                viewModel.setVideoInfo(it)
                updateUI(it)
            }
        }.launchIn(lifecycleScope)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doOnStateChanged()
        setChoicesListeners()
        binding.downloadBtn.setOnClickListener {
            viewModel.finishSelection()
        }
    }

    private fun setChoicesListeners() = with(binding) {
        typeGroup.setOnCheckedStateChangeListener { group, _ ->
            val chip = group.findViewById<Chip>(group.checkedChipId)
            viewModel.setDownloadType(chip.text.toString())
        }

        qualityGroup.setOnCheckedStateChangeListener { group, _ ->
            val chip = group.findViewById<Chip>(group.checkedChipId)
            viewModel.setDownloadQuality(chip.text.toString())
        }
    }

    fun setOnDownloadClicked(listener: (VideoRequest) -> Unit) {
        this.onDownloadClickListener = listener
    }

    private fun doOnStateChanged() {
        viewModel.state.onEach { state ->
            state.error?.let {
                showErrorSnackBar(binding.root, it)
                Timber.e(it)
            }

            if (state.downloadClicked) {
                onDownloadClickListener?.invoke(viewModel.getVideoRequest())
                dismiss()
            }
        }.launchIn(lifecycleScope)
    }

    private fun updateUI(videoInfo: VideoInfo) = with(binding) {
        singleThumbnail.loadImage(videoInfo.thumbnail)
        val title = getString(R.string.download) + " ${videoInfo.title}"
        downloadTitle.text = title
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}