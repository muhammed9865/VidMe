package com.example.vidme.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.ActivityMainBinding
import com.example.vidme.presentation.fragment.FragmentAdapter
import com.example.vidme.presentation.util.showSimpleSnackBar
import com.example.vidme.presentation.util.showWarningSnackBar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val permissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)

    // Used to set Menu options based on each Fragment
    private var currFragmentPosition = MutableStateFlow(0)

    private val storagePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val containsAll = permissions.all { perms.containsKey(it) }
            if (!containsAll) {
                Toast.makeText(this,
                    "All permissions must be allowed to work properly",
                    Toast.LENGTH_LONG).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)
        setUpViewPager()
        setUpTabsLayout()

        currFragmentPosition.onEach {
            updateUI(currFragmentPosition = it)
        }.launchIn(lifecycleScope)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        val searchView = menu?.findItem(R.id.action_search)?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                doOnPlaylistsFragment {

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
                    showWarningSnackBar(binding.root, "I'm on Singles Fragment")
                }

                doOnPlaylistsFragment {
                    showSimpleSnackBar(binding.root, "I'm on Playlist Fragment")
                }

                true
            }
            else -> false
        }
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


    private fun storagePermissionGranted() =
        permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }

    companion object {
        private const val FRAGMENT_PLAYLISTS = 0
        private const val FRAGMENT_SINGLES = 1
    }

}

