package com.example.vidme.presentation.util

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.onDone(callback: (view: View) -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke(this)
            return@setOnEditorActionListener true
        }
        false
    }
}


fun EditText.onNext(callback: (view: View) -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            callback.invoke(this)
            return@setOnEditorActionListener true
        }
        false
    }
}

fun EditText.setImeOption(option: Int) {
    imeOptions = option
}

fun View.hideKeyboard() {
    val ime: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    ime.hideSoftInputFromWindow(windowToken, 0)
}