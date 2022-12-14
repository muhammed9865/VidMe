package com.example.vidme.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import com.example.vidme.service.audio.AudioActions
import timber.log.Timber

class AudioReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != null) {
            val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            Timber.d(intent.action)
            Timber.d(keyEvent.toString())
            when (keyEvent?.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY -> sendIntent(context, AudioActions.ACTION_RESUME)
                KeyEvent.KEYCODE_MEDIA_PAUSE -> sendIntent(context, AudioActions.ACTION_PAUSE)
                KeyEvent.KEYCODE_MEDIA_NEXT -> sendIntent(context, AudioActions.ACTION_NEXT)
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> sendIntent(context, AudioActions.ACTION_PREV)
                KeyEvent.KEYCODE_MEDIA_STOP -> sendIntent(context, AudioActions.ACTION_STOP)
            }
            if (intent.action == AudioActions.ACTION_SEEK_TO) {
                sendIntent(context, AudioActions.ACTION_SEEK_TO, intent)
            }
        }
    }

    private fun sendIntent(context: Context?, action: String, intent: Intent? = null) {
        val newIntent = intent
            ?: Intent(context, AudioService::class.java).apply {
                this.action = action
            }


        context?.startService(newIntent) ?: Timber.e("Context is null")
    }

    companion object {
        const val ACTION = "com.example.vidme.service.ACTION"
        const val ACTION_TYPE = "action_type"
    }
}