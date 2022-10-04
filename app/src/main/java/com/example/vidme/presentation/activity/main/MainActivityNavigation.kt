package com.example.vidme.presentation.activity.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.vidme.R
import com.example.vidme.presentation.fragment.playlist.playlist_add.PlaylistAddFragment
import com.example.vidme.presentation.fragment.playlist.playlist_info.PlaylistInfoFragment

object MainActivityNavigation {

    fun navigateToPlaylistInfo(fragmentManager: FragmentManager) {
        val fragment = PlaylistInfoFragment()
        fragmentManager.beginTransaction()
            .replace(R.id.full_screen_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToPlaylistAdd(fragmentManager: FragmentManager) {
        val fragment: Fragment = PlaylistAddFragment()
        fragmentManager.beginTransaction()
            .replace(R.id.fragments_below_tabs_container, fragment, "adding_playlist")
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

}