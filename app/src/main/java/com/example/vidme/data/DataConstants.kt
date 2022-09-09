package com.example.vidme.data

import android.net.Uri
import android.os.Environment
import java.io.File

object DataConstants {
    fun getStorageFile(): File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VidMe")
    fun getStorageUri() : Uri = Uri.parse(getStorageFile().absolutePath)
}