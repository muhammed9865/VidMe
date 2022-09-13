package com.example.vidme.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.vidme.R
import com.example.vidme.databinding.ActivityMainBinding
import com.example.vidme.presentation.fragment.FragmentAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val permissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)

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

    }

    private fun setUpTabsLayout() {
        TabLayoutMediator(binding.tabLayout, binding.fragmentsPager, true, true) { tab, position ->
            val (text, icon) = when (position) {
                0 -> {
                    Pair("Playlists", ContextCompat.getDrawable(this, R.drawable.ic_playlist))
                }
                else -> {
                    Pair("Singles", ContextCompat.getDrawable(this, R.drawable.ic_single))
                }
            }

            tab.text = text
            tab.icon = icon
        }.attach()
    }

    private fun setUpViewPager() {
        binding.fragmentsPager.apply {
            adapter = FragmentAdapter(supportFragmentManager, lifecycle)
        }

    }


    private fun storagePermissionGranted() =
        permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }


}

