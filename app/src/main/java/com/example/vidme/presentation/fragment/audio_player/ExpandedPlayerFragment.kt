package com.example.vidme.presentation.fragment.audio_player

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.FragmentExpandedAudioPlayerBinding
import com.example.vidme.presentation.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ExpandedPlayerFragment : AudioPlayerFragment() {
    private var _binding: FragmentExpandedAudioPlayerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExpandedAudioPlayerBinding.inflate(layoutInflater)

        val transition = Slide(Gravity.BOTTOM)
        enterTransition = transition
        exitTransition = transition

        sharedElementEnterTransition = ChangeBounds()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onSliderProgressUpdate(binding.audioProgressSeekbar)
        onStateChanged()
        handleButtonsActions()


    }

    private fun onStateChanged() {
        with(binding) {
            viewModel.state.onEach { state ->
                state.audioTitle?.let { title ->
                    if (title != audioTitle.text)
                        audioTitle.text = title
                }
                state.audioDuration?.let { duration ->
                    audioDuration.text = duration
                }
                state.audioThumbnail?.let { thumbnail ->
                    if (thumbnail != currThumbnailUrl) {
                        expandedImage.loadImage(thumbnail)
                        audioThumbnail.loadImage(thumbnail)
                        currThumbnailUrl = thumbnail

                    }
                }
                state.audioProgress.let { progress -> audioCurrentProgress.text = progress }
                state.audioProgressPercent.let { percent -> audioProgressSeekbar.value = percent }
                changePlayButtonImage(state.isPlaying)

            }.launchIn(lifecycleScope)
        }
    }

    private fun changePlayButtonImage(isPlaying: Boolean) {
        val imgRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        binding.btnPlayPause.setImageResource(imgRes)
    }

    private fun handleButtonsActions() = with(binding) {
        btnPlayPause.setOnClickListener { viewModel.pauseResume() }
        btnNext.setOnClickListener { viewModel.nextAudio() }
        btnPrev.setOnClickListener { viewModel.previousAudio() }
        btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }


    override fun onDestroyView() {
        try {
            super.onDestroyView()
            _binding = null
        } catch (e: Exception) {
        }
    }
}