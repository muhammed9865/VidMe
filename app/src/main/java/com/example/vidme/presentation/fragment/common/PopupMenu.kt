package com.example.vidme.presentation.fragment.common

import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu

class PopupMenu(view: View) : PopupMenu(view.context, view) {

    fun setMenuRes(@MenuRes menu: Int) {
        getMenu().clear()
        menuInflater.inflate(menu, getMenu())

    }


}