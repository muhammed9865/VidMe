package com.example.vidme

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@HiltAndroidApp
class VidApplication  : Application(){
    val executorService: ExecutorService = Executors.newFixedThreadPool(2)

}