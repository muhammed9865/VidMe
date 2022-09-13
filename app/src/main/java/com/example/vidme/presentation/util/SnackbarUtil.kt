package com.example.vidme.presentation.util

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

private const val animationMode = Snackbar.ANIMATION_MODE_SLIDE

private fun snackBarBuilder(view: View, message: String) =
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAnimationMode(
        animationMode)

private fun getColor(color: String): Int {
    return Color.parseColor(color)
}

fun showWarningSnackBar(view: View, message: String) {
    snackBarBuilder(view, message).setBackgroundTint(getColor("#ECF009"))
        .setTextColor(getColor("#141313"))
        .show()
}

fun showErrorSnackBar(view: View, message: String) {
    snackBarBuilder(view, message)
        .setBackgroundTint(getColor("#A50404"))
        .setTextColor(getColor("#E3E3E3"))
        .show()
}

fun showSimpleSnackBar(view: View, message: String) {
    snackBarBuilder(view, message).show()
}
