package com.example.vidme.presentation.activity.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.vidme.R
import com.example.vidme.presentation.fragment.audio_player.AudioPlayerFragment
import com.example.vidme.presentation.fragment.playlist.playlist_add.PlaylistAddFragment
import com.example.vidme.presentation.fragment.playlist.playlist_info.PlaylistInfoFragment
import com.example.vidme.presentation.fragment.single.single_add.VideoAddFragment
import com.example.vidme.presentation.fragment.single.single_download.SingleDownloadFragment
import com.example.vidme.presentation.fragment.video_player.VideoPlayerFragment

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

    fun navigateToAudioPlayer(fragmentManager: FragmentManager) {
        val fragment = AudioPlayerFragment()
        fragmentManager.beginTransaction()
            .replace(R.id.music_layout, fragment)
            .setReorderingAllowed(true)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

    }

    /*
        @return fragment because @link SingleDownloadFragment has methods that's used outside of it
     */
    fun navigateToDownload(fragmentManager: FragmentManager): SingleDownloadFragment {
        val fragment = SingleDownloadFragment()
        fragment.show(fragmentManager, null)
        return fragment
    }

    fun navigateToSingleAdd(fragmentManager: FragmentManager) {
        VideoAddFragment().show(fragmentManager, null)
    }

    fun navigateToVideoPlayer(fragmentManager: FragmentManager) {
        val fragment = VideoPlayerFragment()
        fragmentManager.beginTransaction()
            .replace(R.id.full_screen_container, fragment, "adding_playlist")
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

}