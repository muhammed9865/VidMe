package com.example.vidme.presentation.util

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.example.vidme.R
import com.google.android.material.snackbar.Snackbar

private const val animationMode = Snackbar.ANIMATION_MODE_SLIDE

private fun snackBarBuilder(view: View, message: String) =
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAnimationMode(
        animationMode)

private fun getColor(color: String): Int {
    return Color.parseColor(color)
}

fun showWarningSnackBar(view: View, message: String) {
    val icon = ContextCompat.getDrawable(view.context, R.drawable.ic_warning)!!
    snackBarBuilder(view, message).setBackgroundTint(getColor("#ECF009"))
        .setTextColor(getColor("#141313"))
        .setIcon(icon, getColor("#101010"))
        .show()
}

fun showErrorSnackBar(view: View, message: String) {
    val icon = ContextCompat.getDrawable(view.context, R.drawable.ic_error_outline)!!
    snackBarBuilder(view, message)
        .setBackgroundTint(getColor("#A50404"))
        .setTextColor(getColor("#E3E3E3"))
        .setIcon(icon, getColor("#E3E3E3"))
        .show()
}

fun showSimpleSnackBar(view: View, message: String) {
    snackBarBuilder(view, message).show()
}

fun showSuccessSnackBar(view: View, message: String) {
    snackBarBuilder(view, message)
        .setBackgroundTint(getColor("#FF4dc25b"))
        .show()
}

private fun Snackbar.setIcon(drawable: Drawable, @ColorInt colorTint: Int): Snackbar {
    return this.apply {
        setAction(" ") {}
        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        textView.text = ""

        drawable.setTint(colorTint)
        drawable.setTintMode(PorterDuff.Mode.SRC_ATOP)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }
}
