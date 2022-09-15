package com.example.vidme.presentation.fragment.playlist_add

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.Slide
import com.example.vidme.R
import com.example.vidme.databinding.FragmentAddPlaylistBinding
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.util.hideKeyboard
import com.example.vidme.presentation.util.onDone
import com.example.vidme.presentation.util.onNext
import com.example.vidme.presentation.util.setImeOption
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PlaylistAddFragment : Fragment() {
    private var _binding: FragmentAddPlaylistBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<PlaylistAddViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddPlaylistBinding.inflate(layoutInflater)

        val animation = Slide(Gravity.START)
        enterTransition = animation
        exitTransition = animation

        with(binding) {
            playlistNameEt.setImeOption(EditorInfo.IME_ACTION_NEXT)
            urlEt.setImeOption(EditorInfo.IME_ACTION_DONE)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doOnStateChange()

        val nameViews = listOf(binding.nameLayout, binding.textView4)

        animateViews(nameViews)

        binding.playlistNameEt.onNext {
            viewModel.setPlaylistName(binding.playlistNameEt.text.toString())
            binding.urlEt.requestFocus()
        }

        binding.urlEt.onDone {
            viewModel.setUrl(binding.urlEt.text.toString())
            it.hideKeyboard()
        }

        binding.addBtn.setOnClickListener {
            addPlaylist()
        }


    }

    private fun addPlaylist() = with(binding) {
        // Handling Views
        fetching.visibility = View.VISIBLE
        val fetchingViews = listOf(fetchingStateTxt, fetchingPb)
        animateViews(fetchingViews)
        val name = playlistNameEt.text.toString()
        val url = urlEt.text.toString()


        mainViewModel.addPlaylist(name, url)
        lifecycleScope.launch(Dispatchers.Main) {
            delay(5000)
            fetchingStateTxt.text = getString(R.string.fetching_state_second)
            delay(15000)
            fetchingStateTxt.text = getString(R.string.fetching_state_third)
        }

    }

    private fun doOnStateChange() {

        val urlViews = listOf(binding.textView5, binding.textView6, binding.urlLayout)
        val addViews = listOf(binding.addBtn)

        mainViewModel.state.onEach {
            if (it.fetched) {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.fetchingStateTxt.apply {
                        val color = requireContext().getColor(R.color.fetching_finished)
                        setTextColor(color)
                        text = getString(R.string.fetching_finished)
                    }
                    delay(1000)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.state.onEach { state ->
            state.playlistNameError?.let {
                binding.playlistNameEt.error = it
            }

            state.urlError?.let {
                binding.urlEt.error = it
            }

            if (state.validPlaylistName) {
                binding.playlistNameEt.error = null
                animateViews(urlViews)
            }

            if (state.validUrl) {
                binding.urlEt.error = null
                animateViews(addViews)
            }

        }.launchIn(lifecycleScope)
    }

    private fun animateViews(views: List<View>) {
        lifecycleScope.launch {
            views.forEach {
                it.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(1000)
                    .start()
                delay(200)
            }
        }
    }
}