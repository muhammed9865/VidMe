package com.example.vidme.presentation.activity.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.ActivityMainBinding
import com.example.vidme.presentation.fragment.FragmentAdapter
import com.example.vidme.presentation.fragment.common.PopupMenu
import com.example.vidme.presentation.util.showErrorSnackBar
import com.example.vidme.presentation.util.showSimpleSnackBar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel by viewModels<MainViewModel>()

    // Used to set Menu options based on each Fragment
    private var currFragmentPosition = MutableStateFlow(0)

    private val popupMenu: PopupMenu by lazy { PopupMenu(findViewById(R.id.action_options)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)
        setUpViewPager()
        setUpTabsLayout()
        doOnStateChange()

        currFragmentPosition.onEach {
            updateUI(currFragmentPosition = it)
        }.launchIn(lifecycleScope)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        // Initializing the TopBar search button to be a searchView
        val searchView = menu?.findItem(R.id.action_search)?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                doOnPlaylistsFragment {
                    mainViewModel.searchPlaylists(newText)
                }

                doOnSinglesFragment {

                }
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_options -> {
                doOnSinglesFragment {

                }

                doOnPlaylistsFragment {
                    popupMenu.setMenuRes(R.menu.playlist_screen_options)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        if (menuItem.itemId == R.id.action_sync_all) {
                            mainViewModel.synchronizeAllPlaylists()
                            showSimpleSnackBar(binding.root, "Testing")
                            true
                        } else
                            false
                    }
                    popupMenu.show()
                }

                true
            }
            else -> false
        }
    }

    private fun doOnStateChange() {
        mainViewModel.state.onEach { state ->
            state.simpleMessage?.let {
                showSimpleSnackBar(binding.root, it)
            }

            state.error?.let {
                showErrorSnackBar(binding.root, it)
            }

            if (state.syncing) {
                showSimpleSnackBar(binding.root, "Syncing..")
            }
        }.launchIn(lifecycleScope)
    }

    private fun doOnSinglesFragment(action: () -> Unit) {
        if (currFragmentPosition.value == FRAGMENT_SINGLES) {
            action()
        }
    }

    private fun doOnPlaylistsFragment(action: () -> Unit) {
        if (currFragmentPosition.value == FRAGMENT_PLAYLISTS) {
            action()
        }
    }

    private fun setUpTabsLayout() {
        TabLayoutMediator(binding.tabLayout, binding.fragmentsPager, true, true) { tab, position ->
            val (text, icon) = when (position) {
                FRAGMENT_PLAYLISTS -> {
                    Pair(getString(R.string.playlists),
                        ContextCompat.getDrawable(this, R.drawable.ic_playlist))
                }

                FRAGMENT_SINGLES -> {
                    Pair(getString(R.string.singles),
                        ContextCompat.getDrawable(this, R.drawable.ic_single))
                }
                else -> throw IllegalStateException("Unknown fragment")
            }
            tab.text = text
            tab.icon = icon
        }.attach()
    }

    private fun setUpViewPager() {
        binding.fragmentsPager.apply {
            adapter = FragmentAdapter(supportFragmentManager, lifecycle) { position ->
                currFragmentPosition.value = position
            }
        }
    }

    private fun updateUI(currFragmentPosition: Int) {
        when (currFragmentPosition) {
            FRAGMENT_PLAYLISTS -> { /* Update UI to Playlists Fragment*/
            }
            FRAGMENT_SINGLES -> { /* Update UI to Singles Fragment*/
            }
        }
    }


    companion object {
        private const val FRAGMENT_PLAYLISTS = 0
        private const val FRAGMENT_SINGLES = 1
    }

}

