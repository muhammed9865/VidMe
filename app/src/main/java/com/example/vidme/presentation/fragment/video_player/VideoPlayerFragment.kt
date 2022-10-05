package com.example.vidme.presentation.fragment.video_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.vidme.databinding.FragmentVideoPlayerBinding
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.util.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class VideoPlayerFragment : Fragment() {
    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<VideoPlayerViewModel>()

    private val videoView get() = binding.videoView
    private var mediaController: VideoController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(layoutInflater)

        mainViewModel.currentPlaying.onEach {
            val video = it.first { info -> info.isVideo }
            viewModel.setVideo(video)
        }.launchIn(lifecycleScope)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doOnStateChange()
        doOnNavigateUp()
        observeControlsVisibility()
    }

    override fun onPause() {
        super.onPause()
        val isPlaying = videoView.isPlaying
        viewModel.setPlaybackState(videoView.currentPosition, isPlaying)
    }

    override fun onResume() {
        super.onResume()
        videoView.seekTo(viewModel.getLastPosition())
        if (viewModel.isPlaying())
            videoView.start()
    }

    private fun doOnStateChange() {
        viewModel.state.onEach { state ->
            state.apply {
                videoPath?.let { playVideo(it) }
                setVideoTitle(title)
                binding.videoToolbar.visibility(showControls)
            }
        }.launchIn(lifecycleScope)
    }

    private fun playVideo(url: String) {
        with(videoView) {
            setVideoPath(url)
            start()
            setMediaController(getMediaController())
        }
    }

    private fun setVideoTitle(title: String) {
        if (title.isNotEmpty()) {
            binding.videoToolbar.title = title
        }
    }

    private fun observeControlsVisibility() {
        mediaController?.controlsVisibility?.onEach {
            viewModel.setControlsVisibility(it)
        }?.launchIn(lifecycleScope)
    }

    private fun doOnNavigateUp() {
        binding.videoToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun getMediaController(): MediaController {
        return mediaController ?: VideoController(requireContext(), true).apply {
            setAnchorView(binding.root)
        }.also {
            mediaController = it
        }
    }


}