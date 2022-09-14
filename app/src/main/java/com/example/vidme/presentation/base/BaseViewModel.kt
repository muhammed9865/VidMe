package com.example.vidme.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    protected fun tryAsync(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onError: (e: Exception) -> Unit = {},
        action: suspend () -> Unit,
    ) {
        viewModelScope.launch(dispatcher) {
            try {
                action()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}