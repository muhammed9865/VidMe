package com.example.vidme.presentation.util

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.vidme.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

fun View.visibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .error(R.drawable.ic_no_image)
        .into(this)
}

fun translateViews(
    coroutineScope: CoroutineScope,
    views: List<View>,
    reverse: Boolean = false,
    delayOnEach: Long = 200,
) {
    val (translationX, translationY, alpha) = if (reverse) {
        listOf(-500f, 500f, 0f)
    } else listOf(0f, 0f, 1f)
    coroutineScope.launch(Dispatchers.Main) {
        views.forEach {
            it.animate()
                .alpha(alpha)
                .translationX(translationX)
                .translationY(translationY)
                .setDuration(500)
                .start()
            delay(delayOnEach)
        }
    }
}

fun vibrateView(view: View) {
    val anim = AnimationUtils.loadAnimation(view.context, R.anim.vibrate)
    view.startAnimation(anim)
}