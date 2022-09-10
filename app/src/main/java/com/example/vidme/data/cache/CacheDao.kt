package com.example.vidme.data.cache

import androidx.room.*
import com.example.vidme.data.cache.relation.PlaylistWithVideos
import com.example.vidme.data.mapper.toCache
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo

@Dao
abstract class CacheDao : CacheDatabase {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun saveVideoInfo(videoInfo: VideoInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun savePlaylistInfo(playlistInfoCache: YoutubePlaylistInfoCache)

    @Transaction
    override suspend fun savePlaylistInfo(playlistInfo: YoutubePlaylistInfo) {
        val playlistCache = playlistInfo.toCache()
        val videos = playlistInfo.videos
        savePlaylistInfo(playlistCache)
        videos.forEach {
            saveVideoInfo(it)
        }
    }

    @Query("SELECT * FROM VIDEOS_TABLE")
    abstract override suspend fun getAllVideos(): List<VideoInfo>

    @Query("SELECT * FROM PLAYLISTS_TABLE")
    abstract override suspend fun getAllPlaylists(): List<YoutubePlaylistInfoCache>

    @Transaction
    @Query("SELECT * FROM PLAYLISTS_TABLE WHERE name = :playlistName")
    abstract override suspend fun getPlaylistWithVideos(playlistName: String): PlaylistWithVideos

}