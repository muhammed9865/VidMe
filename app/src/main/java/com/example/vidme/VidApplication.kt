package com.example.vidme

import android.app.Application
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VidApplication  : Application(){
    val executorService: ExecutorService = Executors.newFixedThreadPool(2)

}