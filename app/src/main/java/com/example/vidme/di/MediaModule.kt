package com.example.vidme.di

import com.example.vidme.service.audio.AudioManager
import com.example.vidme.service.audio.AudioManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideAudioPlayer(): AudioManager = AudioManagerImpl()
}