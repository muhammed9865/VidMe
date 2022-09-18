package com.example.vidme.presentation.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vidme.presentation.fragment.playlist.playlists_home.PlaylistsFragment
import com.example.vidme.presentation.fragment.single.singles_home.SinglesFragment

class FragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val onNewPosition: (Int) -> Unit,
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        onNewPosition(position)
        return when (position) {
            0 -> {
                PlaylistsFragment()
            }
            1 -> SinglesFragment()
            else -> throw IllegalStateException("Fragment out of range")
        }
    }

    override fun getItemId(position: Int): Long {
        onNewPosition(position)
        return super.getItemId(position)
    }
}