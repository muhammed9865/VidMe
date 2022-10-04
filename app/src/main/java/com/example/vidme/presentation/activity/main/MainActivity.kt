package com.example.vidme.presentation.activity.main

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.vidme.R
import com.example.vidme.databinding.ActivityMainBinding
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.request.Request
import com.example.vidme.domain.pojo.request.VideoRequest
import com.example.vidme.presentation.callback.SingleDownloadState
import com.example.vidme.presentation.fragment.FragmentAdapter
import com.example.vidme.presentation.fragment.audio_player.AudioPlayerFragment
import com.example.vidme.presentation.fragment.common.PopupMenu
import com.example.vidme.presentation.util.DialogsUtil
import com.example.vidme.presentation.util.showErrorSnackBar
import com.example.vidme.presentation.util.showSimpleSnackBar
import com.example.vidme.presentation.util.showWarningSnackBar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel by viewModels<MainViewModel>()

    // Used to set Menu options based on each Fragment
    private var currFragmentPosition = MutableStateFlow(0)

    private val popupMenu: PopupMenu by lazy { PopupMenu(findViewById(R.id.action_options)) }

    private val storagePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!isRequiredPermissionsGranted()) {
                askToEnablePermissions()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)
        setUpViewPager()
        setUpTabsLayout()
        doOnStateChange()
        startPlayingAudio()

        if (!isRequiredPermissionsGranted()) {
            storagePermissions.launch(permissions.toTypedArray())
        }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.i("New intent: ${intent?.action}")
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
                    mainViewModel.searchSingles(newText)
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
        lifecycleScope.launchWhenStarted {
            mainViewModel.state.collect { state ->
                state.simpleMessage?.let {
                    showSimpleSnackBar(binding.root, it)
                }

                state.error?.let {
                    showErrorSnackBar(binding.root, it)
                }

                if (state.syncing) {
                    showSimpleSnackBar(binding.root, "Syncing..")
                }
            }
        }
    }

    private fun startPlayingAudio() {
        val intent = intent
        if (intent.action != null && intent.action == INTENT_FROM_NOTIFICATION_ACTION) {
            val fragment = AudioPlayerFragment()
            if (!mainViewModel.isPlaying.value) {
                if (!supportFragmentManager.fragments.contains(fragment))
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.music_layout, fragment)
                        .setReorderingAllowed(true)
                        .commit()
                mainViewModel.setIsPlaying(true)
            } else {
                supportFragmentManager.beginTransaction()
                    .show(fragment)
                    .setReorderingAllowed(true)
                    .commit()
                mainViewModel.setIsPlaying(true)
            }
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

    private fun isRequiredPermissionsGranted(): Boolean {
        return permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun askToEnablePermissions() {
        DialogsUtil.showWarningDialog(this,
            "Require Permissions",
            getString(R.string.permissions_required)) {
            goToAppSettings()
        }
    }

    private fun goToAppSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    fun navigateToPlaylistAdd() {
        MainActivityNavigation.navigateToPlaylistAdd(supportFragmentManager)
    }

    fun navigateToPlaylistInfo() {
        MainActivityNavigation.navigateToPlaylistInfo(supportFragmentManager)
    }

    fun navigateToAudioPlayer() {
        if (!mainViewModel.isPlaying.value) {
            MainActivityNavigation.navigateToAudioPlayer(supportFragmentManager)
            mainViewModel.setIsPlaying(true)
        }
    }

    fun navigateToDownloadFragment(onDownloadClick: (request: Request) -> String?) {

        val fragment = MainActivityNavigation.navigateToDownload(supportFragmentManager)
        fragment.setOnDownloadClicked { request ->
            val messageToShow = onDownloadClick(request)
            showWarningSnackBar(binding.root, messageToShow ?: "Starting the download...")
            mainViewModel.setSelectedSingle(null)
        }
    }

    fun navigateToSingleAdd() {
        MainActivityNavigation.navigateToSingleAdd(supportFragmentManager)
    }

    fun downloadSingle(
        videoInfo: VideoInfo,
        request: Request,
        listener: SingleDownloadState,
    ) {
        mainViewModel.downloadSingle((request as VideoRequest).copy(videoInfo = videoInfo),
            listener)
    }


    companion object {
        private const val FRAGMENT_PLAYLISTS = 0
        private const val FRAGMENT_SINGLES = 1
        private val permissions = listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        const val INTENT_FROM_NOTIFICATION_ACTION = "com.example.vidme.MainActivity.Notification"
    }

}

