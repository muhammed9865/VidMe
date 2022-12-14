package com.example.vidme.presentation.fragment.single.singles_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.FragmentSinglesBinding
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.request.Request
import com.example.vidme.presentation.activity.main.MainActivity
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.adapter.SingleAdapter
import com.example.vidme.presentation.callback.SingleDownloadState
import com.example.vidme.presentation.fragment.common.FragmentsCommon
import com.example.vidme.presentation.fragment.common.FragmentsCommonImpl
import com.example.vidme.presentation.util.showSuccessSnackBar
import com.example.vidme.presentation.util.visibility
import com.example.vidme.service.audio.AudioManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class SinglesFragment : Fragment(), FragmentsCommon by FragmentsCommonImpl() {
    private var _binding: FragmentSinglesBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val mAdapter by lazy {
        SingleAdapter().apply {
            enableMargins(true)
        }
    }

    @Inject
    lateinit var audioManager: AudioManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSinglesBinding.inflate(layoutInflater)

        enableSwipeToDelete(mAdapter, binding.singlesRv, onDeleteSingle = { single ->
            mainViewModel.deleteSingle(single)
            showSuccessSnackBar(binding.root, "Deleted ${single.title}")
        }
        )

        enableFiltering()
        setAdapterListeners()

        mainViewModel.singles.onEach {
            mAdapter.submitList(it)
            binding.emptyList.visibility(it.isEmpty())
        }.launchIn(lifecycleScope)

        with(binding) {
            singlesRv.adapter = mAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addSingleBtn.setOnClickListener {
            addSingle()
        }

    }

    private fun addSingle() {
        (requireActivity() as MainActivity).navigateToSingleAdd()
    }


    private fun enableFiltering() {
        with(binding) {
            downloadedOnlyCb.setOnCheckedChangeListener { _, b ->
                mainViewModel.addSinglesFilter(SingleFilter.DownloadedOnly)
            }

            filter.setOnCheckedStateChangeListener { group, _ ->
                val filter = when (group.checkedChipId) {
                    R.id.filter_all -> SingleFilter.All
                    R.id.filter_audio -> SingleFilter.Audio
                    R.id.filter_video -> SingleFilter.Video
                    else -> error("IllegalState")
                }
                mainViewModel.addSinglesFilter(filter)
            }
        }
    }

    private fun setAdapterListeners() {
        mAdapter.setOnItemClickListener {

            if (it.isAudio) {
                mainViewModel.setCurrentPlaying(listOf(it))
                startPlayingAudio()
            }
            if (it.isVideo) {
                startPlayingVideo(it)
            }

        }

        mAdapter.setOnDownloadListener { videoInfo, listener ->
            mainViewModel.setSelectedSingle(videoInfo)
            (requireActivity() as MainActivity).navigateToDownloadFragment { request ->
                downloadSingle(videoInfo, request, listener)
                null
            }
        }
    }

    private fun downloadSingle(
        videoInfo: VideoInfo,
        request: Request,
        listener: SingleDownloadState,
    ) {
        (requireActivity() as MainActivity).downloadSingle(videoInfo, request, listener)
    }

    private fun startPlayingAudio() {
        (requireActivity() as MainActivity).navigateToAudioPlayer()
    }

    private fun startPlayingVideo(videoInfo: VideoInfo) {
        (requireActivity() as MainActivity).navigateToVideoPlayer(videoInfo)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}