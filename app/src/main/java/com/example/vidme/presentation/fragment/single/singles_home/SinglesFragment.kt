package com.example.vidme.presentation.fragment.single.singles_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.FragmentSinglesBinding
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.request.VideoRequest
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.adapter.SingleAdapter
import com.example.vidme.presentation.fragment.audio_player.AudioPlayerFragment
import com.example.vidme.presentation.fragment.single.single_add.VideoAddFragment
import com.example.vidme.presentation.fragment.single.single_download.SingleDownloadFragment
import com.example.vidme.presentation.util.*
import com.example.vidme.presentation.util.RecyclerViewUtil.Companion.setSwipeToDelete
import com.example.vidme.service.audio.AudioManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class SinglesFragment : Fragment() {
    private var _binding: FragmentSinglesBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val mAdapter by lazy { SingleAdapter() }

    @Inject
    lateinit var audioManager: AudioManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSinglesBinding.inflate(layoutInflater)
        enableSwipeToDelete()
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
        VideoAddFragment().show(parentFragmentManager, null)
    }

    private fun enableSwipeToDelete() {
        binding.singlesRv.setSwipeToDelete(RecyclerViewUtil.LEFT) { _, position ->
            val single = mAdapter.currentList[position]
            deleteSingle(single, single.title)
        }
    }

    private fun deleteSingle(single: VideoInfo, videoTitle: String) {
        val title = getString(R.string.deleting, videoTitle)
        val content = getString(R.string.confirm_deleting, videoTitle)
        DialogsUtil.showChoiceDialog(
            requireContext(),
            title,
            content,
            onOKPressed = {
                mainViewModel.deleteSingle(single)
                showSimpleSnackBar(binding.root, "Deleted $videoTitle")
            }
        )
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
                // TODO Implement going to PlayVideoFragment
            }

        }

        mAdapter.setOnDownloadListener { v, listener ->
            mainViewModel.setSelectedSingle(v)

            val fragment = SingleDownloadFragment()
            fragment.show(parentFragmentManager, null)

            fragment.setOnDownloadClicked { videoRequest ->
                mainViewModel.downloadSingle((videoRequest as VideoRequest).copy(videoInfo = v),
                    listener)
                showWarningSnackBar(binding.root, "Starting the download...")
                mainViewModel.setSelectedSingle(null)
            }
        }
    }

    private fun startPlayingAudio() {
        if (!mainViewModel.isPlaying.value) {
            val fragment = AudioPlayerFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.music_layout, fragment)
                .setReorderingAllowed(true)
                .setTransition(TRANSIT_FRAGMENT_OPEN)
                .commit()
            mainViewModel.setIsPlaying(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}