package com.example.vidme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.vidme.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
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


    }

    private fun storagePermissionGranted() =
        permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }


    private fun getDownloadFile(): File =
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "yt-dl-and").run {
            if (!this.exists()) {
                mkdir()
            }
            this
        }


}
