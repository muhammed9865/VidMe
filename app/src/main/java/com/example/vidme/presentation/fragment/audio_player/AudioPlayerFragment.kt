package com.example.vidme.presentation.fragment.audio_player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.FragmentAudioPlayerBinding
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.util.loadImage
import com.example.vidme.service.AudioService
import com.example.vidme.service.audio.AudioActions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AudioPlayerFragment : BottomSheetDialogFragment(), ServiceConnection {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<AudioPlayerViewModel>()

    private var currThumbnailUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(layoutInflater)



        isCancelable = false


        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        bindService()
        // to enable title marquee if exceeds constraints
        binding.audioTitle.isSelected = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSliderProgressUpdate()
        onStateChanged()
        handleButtonsActions()

        mainViewModel.singlePlaying.filterNotNull().onEach {
            startPlayingAudio(videoInfo = it)

        }.launchIn(lifecycleScope)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.isPlaying = false
        _binding = null
        unbindService()
    }


    private fun navigateUp() {
        mainViewModel.isPlaying = false
        dismiss()

    }

    private fun bindService() {
        val intent = Intent(requireContext(), AudioService::class.java)
        requireActivity().bindService(intent, this, Context.BIND_ADJUST_WITH_ACTIVITY)
    }

    private fun unbindService() {
        requireActivity().unbindService(this)
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

    private fun handleButtonsActions() = with(binding) {
        btnPlayPause.setOnClickListener { viewModel.pauseResume() }
        btnNext.setOnClickListener { viewModel.nextAudio() }
        btnPrev.setOnClickListener { viewModel.previousAudio() }
        btnClose.setOnClickListener {
            viewModel.close()
            navigateUp()
        }
    }

    private fun changePlayButtonImage(isPlaying: Boolean) {
        val imgRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        binding.btnPlayPause.setImageResource(imgRes)
    }


    private fun onSliderProgressUpdate() {
        binding.audioProgressSeekbar.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.setProgressPercent(slider.value)
            }
        })
    }

    private fun startPlayingAudio(videoInfo: VideoInfo) {
        val intent = Intent(requireContext(), AudioService::class.java).apply {
            action = AudioActions.ACTION_PLAY
        }
        intent.putExtra(AudioService.EXTRA_VIDEO_INFO, videoInfo)
        requireActivity().startService(intent)
    }


    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as AudioService.ServiceBinder
        viewModel.setService(binder.getService())
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        viewModel.setService(null)
    }

}