package com.example.vidme.presentation.fragment.audio_player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.transition.ChangeBounds
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
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
import timber.log.Timber

@AndroidEntryPoint
open class AudioPlayerFragment : BottomSheetDialogFragment(), ServiceConnection {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    protected val mainViewModel by activityViewModels<MainViewModel>()
    protected val viewModel by activityViewModels<AudioPlayerViewModel>()

    protected var currThumbnailUrl = ""

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

        // Expanding Player on specific views click
        binding.apply {
            root.setOnClickListener { expandPlayer() }
            audioThumbnail.setOnClickListener { expandPlayer() }
        }


        sharedElementReturnTransition = ChangeBounds()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSliderProgressUpdate(binding.audioProgressSeekbar)
        onStateChanged()
        handleButtonsActions()
        observeCurrentPlaying()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.setIsPlaying(false)
        _binding = null
        unbindService()
    }


    protected fun navigateUp() {
        mainViewModel.setIsPlaying(false)
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


    protected fun onSliderProgressUpdate(slider: Slider) {
        slider.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.setProgressPercent(slider.value)
            }
        })
    }

    private fun observeCurrentPlaying() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.currentPlayingAudio.filterNotNull().collect {
                Timber.d(it.toString())
                startPlayingAudio(singles = it)
            }
        }
    }

    private fun expandPlayer() {
        val fragment = ExpandedPlayerFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.expanded_player_container, fragment)
            .addToBackStack("expanded_player")
//            .addSharedElement(binding.audioThumbnail, "video_thumbnail_expanded_transition")
            .commit()

    }

    private fun startPlayingAudio(singles: List<VideoInfo>) {
        if (singles.isNotEmpty()) {
            val extra =
                if (singles.size > 1) AudioService.EXTRA_PLAYLIST else AudioService.EXTRA_VIDEO_INFO

            val intent = Intent(requireContext(), AudioService::class.java).apply {
                action = AudioActions.ACTION_PLAY
            }

            if (extra == AudioService.EXTRA_VIDEO_INFO) {
                intent.putExtra(extra, singles[0])
            } else {
                val asArrayList = arrayListOf<VideoInfo>()
                asArrayList.addAll(singles)
                intent.putParcelableArrayListExtra(extra, asArrayList)
            }

            requireActivity().startService(intent)
        }
    }


    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as AudioService.ServiceBinder
        viewModel.setService(binder.getService())
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        viewModel.setService(null)
    }

}