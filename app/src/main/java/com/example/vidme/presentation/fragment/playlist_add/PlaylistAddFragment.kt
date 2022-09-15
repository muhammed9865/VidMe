package com.example.vidme.presentation.fragment.playlist_add

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import timber.log.Timber


@AndroidEntryPoint
class PlaylistAddFragment : Fragment() {
    private var _binding: FragmentAddPlaylistBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<PlaylistAddViewModel>()

    companion object {
        private const val AFTER_FINISH_DELAY = 3000L
        private const val FETCHING_STATE_SECOND_DELAY = 10000L
        private const val FETCHING_STATE_THIRD_DELAY = 30000L
    }

    // Views that will be animated  during fragment lifecycle
    private val nameViews by lazy { listOf(binding.nameLayout, binding.textView4) }
    private val fetchingViews by lazy { listOf(binding.fetchingStateTxt, binding.fetchingPb) }
    private val urlViews by lazy { listOf(binding.textView5, binding.textView6, binding.urlLayout) }
    private val addViews by lazy { listOf(binding.addBtn) }
    private val addNewView by lazy { listOf(binding.addNewCb) }

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

        binding.addNewCb.setOnCheckedChangeListener { _, b ->
            viewModel.setAddNew(b)
        }

        animateShowViews(nameViews)

        binding.playlistNameEt.onNext {
            viewModel.setPlaylistName(binding.playlistNameEt.text.toString())
            binding.urlEt.requestFocus()
        }

        binding.urlEt.onDone {
            viewModel.setUrl(binding.urlEt.text.toString())
            it.hideKeyboard()
        }

        binding.addBtn.setOnClickListener {
            viewModel.validate()
        }

    }

    private fun addPlaylist() = with(binding) {
        // Handling Views
        fetching.visibility = View.VISIBLE
        animateShowViews(fetchingViews)
        val name = playlistNameEt.text.toString()
        val url = urlEt.text.toString()

        // Actual adding
        mainViewModel.addPlaylist(name, url)

        // Changing state text over time
        lifecycleScope.launch(Dispatchers.Main) {
            delay(FETCHING_STATE_SECOND_DELAY)
            fetchingStateTxt.text = getString(R.string.fetching_state_second)
            delay(FETCHING_STATE_THIRD_DELAY)
            fetchingStateTxt.text = getString(R.string.fetching_state_third)
        }

    }

    private fun doOnStateChange() {

        mainViewModel.state.onEach { state ->
            if (state.fetched) {
                Timber.d(state.toString())
                if (state.playlistWasCached && !viewModel.addNew) {
                    vibrateView(binding.urlEt)
                    binding.urlEt.error = "Playlist with same URL is added already"
                    animateShowViews(fetchingViews, reverse = true)
                    animateShowViews(addNewView)
                    mainViewModel.resetStateAfterAdding()

                } else {
                    lifecycleScope.launch(Dispatchers.Main) {

                        binding.fetchingStateTxt.apply {
                            val color = requireContext().getColor(R.color.fetching_finished)
                            setTextColor(color)
                            text = getString(R.string.fetching_finished)
                        }
                        delay(AFTER_FINISH_DELAY)
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        mainViewModel.resetStateAfterAdding()

                    }
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
                animateShowViews(urlViews)
            }

            if (state.validUrl) {
                binding.urlEt.error = null
                animateShowViews(addViews)

            }

            if (state.success) {
                addPlaylist()
            }

        }.launchIn(lifecycleScope)
    }

    private fun animateShowViews(views: List<View>, reverse: Boolean = false) {
        val (translationX, alpha) = if (reverse) {
            -500f to 0f
        } else 0f to 1f
        lifecycleScope.launch {
            views.forEach {
                it.animate()
                    .alpha(alpha)
                    .translationX(translationX)
                    .setDuration(1000)
                    .start()
                delay(200)
            }
        }
    }

    private fun vibrateView(view: View) {
        val anim = AnimationUtils.loadAnimation(view.context, R.anim.vibrate)
        view.startAnimation(anim)
    }
}