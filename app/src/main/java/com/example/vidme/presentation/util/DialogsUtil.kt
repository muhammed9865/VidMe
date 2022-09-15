package com.example.vidme.presentation.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.vidme.R

class DialogsUtil {
    companion object {
        private fun alertDialog(context: Context, icon: Drawable?, title: String, message: String) =
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)

        fun showWarningDialog(
            context: Context,
            title: String,
            content: String,
            onOKPressed: () -> Unit,
        ) {
            val iconUnEdited = ContextCompat.getDrawable(context, R.drawable.ic_warning)
            val icon = DrawableCompat.wrap(iconUnEdited!!)
            icon.setTint(context.getColor(R.color.clickable_color))
            alertDialog(context, title = title, icon = icon, message = content)
                .setPositiveButton("Alright"
                ) { d, _ ->
                    onOKPressed()
                    d.dismiss()
                }
                .create()
                .show()

        }

        fun showChoiceDialog(
            context: Context,
            title: String,
            content: String,
            onNOPressed: () -> Unit = {},
            onOKPressed: () -> Unit,
        ) {
            alertDialog(context, null, title, content)
                .setPositiveButton("Continue") { d, _ ->
                    onOKPressed()
                    d.dismiss()
                }
                .setNegativeButton("Cancel") { d, _ ->
                    onNOPressed()
                    d.dismiss()
                }
                .create()
                .show()
        }


    }
}