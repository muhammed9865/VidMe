package com.example.vidme.presentation.fragment.singles

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
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.adapter.SingleAdapter
import com.example.vidme.presentation.util.DialogsUtil
import com.example.vidme.presentation.util.RecyclerViewUtil
import com.example.vidme.presentation.util.RecyclerViewUtil.Companion.setSwipeToDelete
import com.example.vidme.presentation.util.showSimpleSnackBar
import com.example.vidme.presentation.util.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SinglesFragment : Fragment() {
    private var _binding: FragmentSinglesBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val mAdapter by lazy { SingleAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSinglesBinding.inflate(layoutInflater)
        enableSwipeToDelete()
        enableFiltering()

        mainViewModel.singles.onEach {
            mAdapter.submitList(it)
            binding.emptyList.visibility(it.isEmpty())
        }.launchIn(lifecycleScope)

        with(binding) {
            singlesRv.adapter = mAdapter
        }
        return binding.root
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
                showSimpleSnackBar(binding.root, "Deleting $videoTitle")
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}