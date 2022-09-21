package com.example.vidme

import android.app.Application
import com.yausername.youtubedl_android.YoutubeDL
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@HiltAndroidApp
class VidApplication  : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        YoutubeDL.getInstance().init(applicationContext)
    }

    companion object {
        val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    }
}