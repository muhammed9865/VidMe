package com.example.vidme.data.cache

import androidx.room.*
import com.example.vidme.data.cache.relation.PlaylistWithVideos
import com.example.vidme.data.mapper.toCache
import com.example.vidme.data.mapper.toYoutubePlaylistInfo
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
        saveVideosInfo(videos)
    }

    override suspend fun updatePlaylistInfo(playlistInfo: YoutubePlaylistInfo): YoutubePlaylistInfo {
        /*
            * Saving the videos first so new ones will be saved and old will remain.
            * Getting the PlaylistInfo with the Videos
            * Updating the PlaylistInfo
            * @Return updatedPlaylist (cachedPlaylist)

         */
        saveVideosInfo(playlistInfo.videos)
        var cachedPlaylist = getPlaylistWithVideos(playlistInfo.name)
        val videos = cachedPlaylist.videos
        val updatedPlaylistInfo = cachedPlaylist.playlistInfoCache.copy(count = videos.size)
        savePlaylistInfo(updatedPlaylistInfo)
        cachedPlaylist = cachedPlaylist.copy(playlistInfoCache = updatedPlaylistInfo)
        return cachedPlaylist.toYoutubePlaylistInfo()
    }

    @Query("SELECT * FROM PLAYLISTS_TABLE WHERE name = :playlistName")
    abstract override suspend fun getPlaylistInfoByName(playlistName: String): YoutubePlaylistInfoCache

    @Query("SELECT * FROM VIDEOS_TABLE WHERE id = :id")
    abstract override suspend fun getVideoInfoByID(id: String): VideoInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun saveVideosInfo(videosInfo: List<VideoInfo>)

    @Query("SELECT * FROM VIDEOS_TABLE")
    abstract override suspend fun getAllVideos(): List<VideoInfo>

    @Query("SELECT * FROM PLAYLISTS_TABLE")
    abstract override suspend fun getAllPlaylists(): List<YoutubePlaylistInfoCache>

    @Transaction
    @Query("SELECT * FROM PLAYLISTS_TABLE WHERE name = :playlistName")
    abstract override suspend fun getPlaylistWithVideos(playlistName: String): PlaylistWithVideos

}
