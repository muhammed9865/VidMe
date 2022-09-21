package com.example.vidme.service.notification

import android.app.Notification
import android.content.Context
import com.example.vidme.domain.pojo.VideoInfo

interface NotificationManager {

    fun buildNotification(context: Context): Notification
    fun setPlayingState(playing: Boolean)
    fun show(context: Context)
    fun setCurrentVideoInfo(videoInfo: VideoInfo)
    fun onDestroy()

}