package com.example.vidme.domain.util

import android.net.Uri
import android.os.Environment
import com.example.vidme.VidApplication
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import java.io.File

class FileUtil {
    companion object {

        private fun getStorageFile(): File = kotlin.run {
            val context = VidApplication.instance.applicationContext
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "VidMe").apply {
                if (!exists()) {
                    mkdir()
                }
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

        fun checkIfMediaExists(name: String): Pair<String?, Boolean> {
            val storedFiles = getStorageFile().listFiles()
            storedFiles?.forEach { file ->
                if (file.nameWithoutExtension == name) {
                    Timber.d("${file.nameWithoutExtension} : $name")
                    return file.absolutePath to true
                }
            }
            return null to false

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
}