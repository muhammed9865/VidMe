package com.example.vidme.domain.util

import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
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

    suspend fun deletePlaylistByName(playlistName: String): Boolean {
        return coroutineScope {
            try {
                val file = getStorageFileForPlaylist(playlistName)
                file.deleteRecursively()

            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun deleteVideoByName(location: String?): Boolean {
        return coroutineScope {
            try {
                val file = location?.let { File(it) }
                file?.delete() ?: false
            } catch (e: Exception) {
                Timber.d(e)
                false
            }
        }
    }

}