package com.example.vidme.di

import android.content.Context
import androidx.room.Room
import com.example.vidme.data.cache.CacheDao
import com.example.vidme.data.cache.CacheDatabase
import com.example.vidme.data.cache.RoomDatabase
import com.example.vidme.data.repository.MediaRepositoryImpl
import com.example.vidme.domain.repository.MediaRepository
import com.yausername.youtubedl_android.YoutubeDL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun bindRepository(mediaRepository: MediaRepositoryImpl): MediaRepository = mediaRepository

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RoomDatabase {
        return Room.databaseBuilder(context, RoomDatabase::class.java, "media")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCacheDao(database: RoomDatabase): CacheDao = database.cacheDao


    @Provides
    @Singleton
    fun provideCacheDatabase(cacheDao: CacheDao): CacheDatabase = cacheDao

    @Provides
    @Singleton
    fun provideYTDL(): YoutubeDL = YoutubeDL.getInstance()
}