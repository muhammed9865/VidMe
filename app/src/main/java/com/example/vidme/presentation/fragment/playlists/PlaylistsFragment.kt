package com.example.vidme.presentation.fragment.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.vidme.databinding.FragmentPlaylistsBinding
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.adapter.PlaylistInfoAdapter
import com.example.vidme.presentation.fragment.playlist_add.PlaylistAddFragment
import com.example.vidme.presentation.util.RecyclerViewUtil
import com.example.vidme.presentation.util.RecyclerViewUtil.Companion.setSwipeToDelete
import com.example.vidme.presentation.util.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PlaylistsFragment : Fragment() {

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
        enableSwipeToDelete()

        with(binding) {
            playlistsRv.adapter = mAdapter

            addPlaylistBtn.setOnClickListener {
                val fragment: Fragment = PlaylistAddFragment()
                parentFragmentManager.beginTransaction()
                    .replace(binding.root.id, fragment, "adding_playlist")
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()

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
            /* TODO Implement YoutubePlaylistInfo Screen and Navigate to it */
        }

        mAdapter.setOnSyncPressedListener {
            mainViewModel.synchronizePlaylist(it)
        }
    }

    private fun enableSwipeToDelete() {
        binding.playlistsRv.setSwipeToDelete(RecyclerViewUtil.LEFT) { _, position ->

        }
    }

}