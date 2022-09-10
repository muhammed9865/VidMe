package com.example.vidme.data

import android.net.Uri
import android.os.Environment
import java.io.File

object FileUtil {
    private fun getStorageFile(): File =
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "VidMe").apply {
            if (!exists()) {
                mkdir()
            }
        }

    fun getStorageUri(): Uri = Uri.parse(getStorageFile().absolutePath)

    fun getStorageFileForPlaylist(playlistName: String): File {
        return File(getStorageFile(), playlistName).apply {
            if (!exists()) {
                mkdir()
            }
        }
    }
}