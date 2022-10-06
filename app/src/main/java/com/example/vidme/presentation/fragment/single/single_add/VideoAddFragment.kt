package com.example.vidme.presentation.fragment.single.single_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.FragmentAddSingleBinding
import com.example.vidme.presentation.activity.main.MainViewModel
import com.example.vidme.presentation.util.hideKeyboard
import com.example.vidme.presentation.util.onDone
import com.example.vidme.presentation.util.setImeOption
import com.example.vidme.presentation.util.translateViews
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VideoAddFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentAddSingleBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<VideoAddViewModel>()

    companion object {
        private const val AFTER_FINISH_DELAY = 2000L
        private const val FETCHING_STATE_SECOND_DELAY = 10000L
        private const val FETCHING_STATE_THIRD_DELAY = 30000L
    }

    // Views that will be animated  during fragment lifecycle
    private val fetchingViews by lazy { listOf(binding.fetchingStateTxt, binding.fetchingPb) }
    private val urlViews by lazy {
        listOf(binding.textView5,
            binding.urlLayout,
            binding.enterUrlDoneBtn)
    }
    private val addViews by lazy { listOf(binding.addBtn) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddSingleBinding.inflate(layoutInflater)


        with(binding) {
            urlEt.setImeOption(EditorInfo.IME_ACTION_DONE)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doOnStateChange()
        translateViews(lifecycleScope, urlViews)
        doOnTextChanged()

        binding.urlEt.onDone {
            onSubmitUrl(it)
        }

        binding.enterUrlDoneBtn.setOnClickListener {
            onSubmitUrl(it)
        }

        binding.addBtn.setOnClickListener {
            viewModel.finishAdding()
        }

    }

    private fun addSingle() = with(binding) {
        // Handling Views
        fetching.visibility = View.VISIBLE
        translateViews(lifecycleScope, fetchingViews)

        val url = urlEt.text.toString()

        // Actual adding
        mainViewModel.addSingle(url)

        // Changing state text over time
        lifecycleScope.launch(Dispatchers.Main) {
            delay(FETCHING_STATE_SECOND_DELAY)
            fetchingStateTxt.text = getString(R.string.fetching_state_second)
            delay(FETCHING_STATE_THIRD_DELAY)
            fetchingStateTxt.text = getString(R.string.fetching_state_third_playlist)
        }

    }

    private fun doOnStateChange() {

        mainViewModel.state.onEach { state ->
            if (state.fetchedVideo) {

                lifecycleScope.launch(Dispatchers.Main) {

                    binding.fetchingStateTxt.apply {
                        val color = requireContext().getColor(R.color.fetching_finished)
                        setTextColor(color)
                        text = getString(R.string.fetching_single_finished)
                    }
                    delay(AFTER_FINISH_DELAY)
                    navigateUp()
                    mainViewModel.resetStateAfterAdding()

                }

            }

            state.error?.let {
                isCancelable = true
                binding.fetchingStateTxt.apply {
                    text = getString(R.string.unknown_error)
                }
                lifecycleScope.apply {
                    delay(1000)
                    translateViews(this, fetchingViews, true)
                }
                mainViewModel.resetStateAfterAdding()
                viewModel.resetState()
            }
        }.launchIn(lifecycleScope)

        viewModel.state.onEach { state ->


            state.urlError?.let {
                binding.urlEt.error = it
            }


            if (state.validUrl) {
                binding.urlEt.error = null
                translateViews(lifecycleScope, addViews, false, 100)

            }

            if (state.addClicked) {
                isCancelable = false
                addSingle()
            }

        }.launchIn(lifecycleScope)
    }

    // EditText (url) text change
    private fun doOnTextChanged() {
        binding.urlEt.doOnTextChanged { text, _, _, _ ->
            binding.enterUrlDoneBtn.isEnabled = text?.isNotEmpty() == true
        }
    }

    private fun onSubmitUrl(view: View) {
        viewModel.setUrl(binding.urlEt.text.toString())
        view.hideKeyboard()
    }


    private fun navigateUp() {
        dismiss()
    }
}