package com.example.vidme

import android.app.Application
import android.os.StrictMode
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@HiltAndroidApp
class VidApplication  : Application() {

    override fun onCreate() {
        // This should detect any disk reads/write, any network calls on the UI Thread.
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()
        )

        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())

        // Enabling YoutubeDL in another thread to fasten up the open up time.
        val initYoutubeDL = CoroutineScope(Dispatchers.Default).launch {
            try {
                YoutubeDL.getInstance().init(applicationContext)
                YoutubeDL.getInstance().updateYoutubeDL(applicationContext)
            } catch (e: YoutubeDLException) {
                Timber.e(e.message)
            }
        }

        initYoutubeDL.invokeOnCompletion {
            if (it != null)
                Timber.d(it.message)
            initYoutubeDL.cancel()
        }
    }

    companion object {
        val executorService: ExecutorService = Executors.newFixedThreadPool(4)
        lateinit var instance: VidApplication
    }
}