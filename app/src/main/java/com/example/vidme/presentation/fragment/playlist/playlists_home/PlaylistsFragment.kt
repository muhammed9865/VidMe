package com.example.vidme.presentation.fragment.playlist.playlists_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.FragmentPlaylistsBinding
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.presentation.activity.main.MainActivity
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.adapter.PlaylistInfoAdapter
import com.example.vidme.presentation.fragment.common.FragmentsCommon
import com.example.vidme.presentation.fragment.common.FragmentsCommonImpl
import com.example.vidme.presentation.util.DialogsUtil
import com.example.vidme.presentation.util.RecyclerViewUtil
import com.example.vidme.presentation.util.RecyclerViewUtil.Companion.setSwipeToDelete
import com.example.vidme.presentation.util.showSuccessSnackBar
import com.example.vidme.presentation.util.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PlaylistsFragment : Fragment(), FragmentsCommon by FragmentsCommonImpl() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val mAdapter: PlaylistInfoAdapter by lazy { PlaylistInfoAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(layoutInflater)
        setAdapterListeners()
        enableSwipeToDelete(mAdapter, binding.playlistsRv, onDeletePlaylist = {
            mainViewModel.deletePlaylist(it)
            showSuccessSnackBar(binding.root, "Deleting ${it.name}")
        })

        with(binding) {
            playlistsRv.adapter = mAdapter

            addPlaylistBtn.setOnClickListener {
                (requireActivity() as MainActivity).navigateToPlaylistAdd()
            }
        }


        // Populating playlists
        mainViewModel.playlists.onEach {
            mAdapter.submitList(it)
            binding.emptyList.visibility(it.isEmpty())
        }.launchIn(lifecycleScope)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAdapterListeners() {

        mAdapter.setOnItemClickListener {
            mainViewModel.setSelectedPlaylist(it)
            (requireActivity() as MainActivity).navigateToPlaylistInfo()


        }

        mAdapter.setOnSyncPressedListener {
            mainViewModel.synchronizePlaylist(it)
        }
    }


    private fun enableSwipeToDelete() {
        binding.playlistsRv.setSwipeToDelete(RecyclerViewUtil.LEFT) { _, position ->
            val playlist = mAdapter.currentList[position]
            deletePlaylist(playlist, playlist.name)
        }
    }

    private fun deletePlaylist(playlistInfo: YoutubePlaylistInfo, playlistName: String) {
        val title = getString(R.string.deleting, playlistName)
        val content = getString(R.string.confirm_deleting, playlistName)
        DialogsUtil.showChoiceDialog(
            requireContext(),
            title,
            content,
            onOKPressed = {
                mainViewModel.deletePlaylist(playlistInfo)
                showSuccessSnackBar(binding.root, "Deleting $playlistName")
            }
        )
    }

}