package com.example.vidme.presentation.fragment.playlist.playlist_info

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vidme.R
import com.example.vidme.databinding.FragmentPlaylistInfoBinding
import com.example.vidme.domain.pojo.request.PlaylistRequest
import com.example.vidme.domain.pojo.request.VideoRequest
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.adapter.SingleAdapter
import com.example.vidme.presentation.fragment.audio_player.AudioPlayerFragment
import com.example.vidme.presentation.fragment.common.PopupMenu
import com.example.vidme.presentation.fragment.single.single_download.SingleDownloadFragment
import com.example.vidme.presentation.util.loadImage
import com.example.vidme.presentation.util.showWarningSnackBar
import com.example.vidme.presentation.util.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistInfoFragment
    : Fragment() {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<PlaylistInfoViewModel>()

    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy {
        SingleAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(layoutInflater)

        val transition = Slide(Gravity.BOTTOM)
        enterTransition = transition
        exitTransition = transition

        with(binding) {
            playlistVideosRv.adapter = mAdapter
            playlistVideosRv.layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.selectedPlaylist.filterNotNull().onEach {
                    viewModel.setSelectedPlaylist(it)
                }.launchIn(this)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapterListeners()
        onStateChanged()
        handleButtonClicks()
        onToolbarOptionsClicked()
    }


    private fun handleButtonClicks() = with(binding) {
        playAll.setOnClickListener {
            val playlist = viewModel.getPlayableSingles()
            if (playlist.isNotEmpty()) {
                mainViewModel.setCurrentPlaying(playlist)
                startPlayingAudio()
            }
        }
    }

    private fun onToolbarOptionsClicked() {
        binding.playlistToolbar.setOnMenuItemClickListener { option ->
            when (option.itemId) {
                R.id.action_info_actions -> {
                    val actions = PopupMenu(requireActivity().findViewById(option.itemId))
                    actions.setMenuRes(R.menu.playlist_info_options)
                    actions.show()
                    onInfoActions(actions)
                    true
                }

                R.id.action_info_navigate_up -> {
                    navigateUp()
                    true
                }

                else -> false
            }
        }
    }

    private fun onInfoActions(options: PopupMenu) {
        options.setOnMenuItemClickListener { option ->
            when (option.itemId) {
                R.id.option_download_all -> {
                    downloadPlaylist()
                    true
                }
                R.id.option_sync -> {
                    mainViewModel.synchronizePlaylist(viewModel.selectedPlaylistWithVideos.playlistInfo)
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateUp() {
        mainViewModel.setSelectedPlaylist(null)
        requireActivity().onBackPressed()
    }

    private fun downloadPlaylist() {
        val requestFragment = SingleDownloadFragment()
        requestFragment.show(parentFragmentManager, null)
        requestFragment.setOnDownloadClicked {
            val playlistRequest = PlaylistRequest(
                viewModel.selectedPlaylistWithVideos.playlistInfo,
                type = it.type,
                quality = it.quality
            )
            mainViewModel.downloadPlaylist(playlistRequest, viewModel.onDownloadingPlaylist())
        }
    }


    private fun onStateChanged() {
        viewModel.state.onEach {

            mAdapter.submitList(it.singles)
            with(binding) {
                singleThumbnailPlaylist.loadImage(it.playedSingleThumbnail)
                playlistSinglesCount.text = getString(R.string.files_count, it.singleCount)
                playlistDuration.text = it.playlistFullDuration
                playlistLastSynced.text = getString(R.string.last_synced, it.lastSynced)
                collapseToolbar.title = it.playlistTitle

                downloadingViews.visibility(it.showDownloading)
                if (it.showDownloading) {
                    downloadIndexTxt.text = getString(R.string.current_video_downloading,
                        it.videoBeingDownloaded!!.title)
                    it.downloadProgress?.let { progress -> downloadProgress.progress = progress }
                    it.downloadTimeRemaining?.let { time ->
                        downloadTimeRemaining.text = getString(R.string.time_remaining, time)
                    }
                }

            }

        }.launchIn(lifecycleScope)
    }

    private fun setAdapterListeners() {
        mAdapter.setOnItemClickListener {

            if (it.isAudio) {
                viewModel.setSelectedSingle(it)
                startPlayingAudio()
                mainViewModel.setCurrentPlaying(listOf(it))
            }
            if (it.isVideo) {
                viewModel.setSelectedSingle(it)
                // TODO Implement going to PlayVideoFragment
            }

        }

        mAdapter.setOnDownloadListener { v, listener ->
            mainViewModel.setSelectedSingle(v)

            val fragment = SingleDownloadFragment()
            fragment.show(parentFragmentManager, null)

            fragment.setOnDownloadClicked { videoRequest ->
                mainViewModel.downloadSingle(videoRequest as VideoRequest, listener)
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
                .commit()
            mainViewModel.setIsPlaying(true)
        }
    }


}