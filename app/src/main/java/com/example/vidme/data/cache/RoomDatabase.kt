package com.example.vidme.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.VideoInfo


@Database(
    entities = [VideoInfo::class, YoutubePlaylistInfoCache::class],
    version = 2,
    exportSchema = true,
)
abstract class RoomDatabase : RoomDatabase() {
    abstract val cacheDao: CacheDao

}