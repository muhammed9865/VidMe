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
    }

    override fun onPause() {
        super.onPause()
        viewModel.setLastPosition(videoView.currentPosition)
    }

    override fun onResume() {
        super.onResume()
        videoView.seekTo(viewModel.getLastPosition())
    }

    private fun doOnStateChange() {
        viewModel.state.onEach { state ->
            state.apply {
                videoPath?.let { playVideo(it) }
                setVideoTitle(title)
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

    private fun doOnNavigateUp() {
        binding.videoToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun getMediaController(): MediaController {
        return MediaController(requireContext(), true).apply {
            setAnchorView(binding.root)

        }
    }


}