package com.example.vidme.service.audio

import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.vidme.service.AudioReceiver
import com.example.vidme.service.AudioService
import timber.log.Timber

class AudioCallback(private val context: Context) :
    MediaSessionCompat.Callback() {

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        Timber.d(pos.toString())
        val intent = Intent(context, AudioService::class.java)
        intent.action = AudioActions.ACTION_SEEK_TO
        intent.putExtra(AudioExtras.EXTRA_SEEK_TO_POSITION, pos.toInt())
        context.startService(intent)
    }

    override fun onPause() {
        super.onPause()
        val action = AudioActions.ACTION_PAUSE
        val intent = Intent(context, AudioReceiver::class.java)
        intent.action = AudioReceiver.ACTION
        intent.putExtra(AudioReceiver.ACTION_TYPE, action)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

}