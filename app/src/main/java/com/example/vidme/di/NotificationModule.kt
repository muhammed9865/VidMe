package com.example.vidme.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vidme.R
import com.example.vidme.service.audio.AudioManager
import com.example.vidme.service.notification.NotificationManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManagerCompat {
        val nm = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioPlayerChannel = NotificationChannel(AUDIO_PLAYER_CHANNEL_ID,
                AUDIO_PLAYER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW)
            audioPlayerChannel.apply {
                setSound(null, null)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                lightColor = context.getColor(R.color.clickable_color)
            }
            nm.createNotificationChannel(audioPlayerChannel)
        }

        return nm
    }

    @Provides
    @Singleton
    fun provideNotificationManagerImpl(
        notificationManagerCompat: NotificationManagerCompat,
        audioManager: AudioManager,
    ): com.example.vidme.service.notification.NotificationManager {
        return NotificationManagerImpl(notificationManagerCompat, audioManager)
    }

    const val AUDIO_PLAYER_CHANNEL_ID = "audio_channel"
    private const val AUDIO_PLAYER_CHANNEL_NAME = "Playback Service"
}