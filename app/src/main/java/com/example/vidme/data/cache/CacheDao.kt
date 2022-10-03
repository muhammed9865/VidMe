package com.example.vidme.data.cache

import androidx.room.*
import com.example.vidme.data.cache.relation.PlaylistWithVideos
import com.example.vidme.data.mapper.toCache
import com.example.vidme.data.mapper.toYoutubePlaylistInfo
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import kotlinx.coroutines.coroutineScope

@Dao
abstract class CacheDao : CacheDatabase {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveVideoInfoImpl(videoInfo: VideoInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun savePlaylistInfo(playlistInfoCache: YoutubePlaylistInfoCache)

    override suspend fun saveVideoInfo(videoInfo: VideoInfo) {
        val cached: VideoInfo? = getVideoInfo(videoInfo.id, videoInfo.playlistName)
        if (cached == null) {
            saveVideoInfoImpl(videoInfo)
            return
        }

        if (cached.storageUrl == null) {
            saveVideoInfoImpl(videoInfo)
        }

    }

    @Transaction
    override suspend fun savePlaylistInfo(playlistInfo: YoutubePlaylistInfo) {

        coroutineScope {
            savePlaylistInfo(playlistInfo.toCache())
        }
        coroutineScope {
            saveVideosInfo(playlistInfo.videos)
        }

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
        val updatedPlaylistInfo =
            cachedPlaylist.toYoutubePlaylistInfo().toCache().copy(count = videos.size)
        savePlaylistInfo(updatedPlaylistInfo)
        cachedPlaylist = cachedPlaylist.copy(playlistInfoCache = updatedPlaylistInfo)
        return cachedPlaylist.toYoutubePlaylistInfo()
    }


    override suspend fun saveVideosInfo(videosInfo: List<VideoInfo>) {
        videosInfo.forEach {
            saveVideoInfo(it)
        }
    }


    override suspend fun deleteVideosInfo(videosInfo: List<VideoInfo>) {
        videosInfo.forEach {
            deleteVideoInfo(it.id, it.playlistName)
        }
    }

    @Query("DELETE FROM VIDEOS_TABLE WHERE id = :id AND playlistName = :playlistName")
    abstract suspend fun deleteVideoInfo(id: String, playlistName: String = "")

    @Transaction
    override suspend fun deletePlaylistByName(playlistName: String) {
        val playlist = getPlaylistWithVideos(playlistName)
        deletePlaylistInfoByName(playlist.playlistInfoCache.name)
        deleteVideosInfo(playlist.videos)
    }

    @Query("DELETE FROM playlists_table WHERE name = :playlistName")
    abstract suspend fun deletePlaylistInfoByName(playlistName: String)

    @Query("SELECT * FROM PLAYLISTS_TABLE WHERE name = :playlistName")
    abstract override suspend fun getPlaylistInfoByName(playlistName: String): YoutubePlaylistInfoCache

    @Query("SELECT * FROM VIDEOS_TABLE WHERE id = :id AND playlistName = :playlistName")
    abstract override suspend fun getVideoInfo(id: String, playlistName: String): VideoInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveVideosInfoImpl(videosInfo: List<VideoInfo>)

    @Query("SELECT * FROM VIDEOS_TABLE WHERE playlistName = '' ")
    abstract override suspend fun getAllVideos(): List<VideoInfo>

    @Query("SELECT * FROM PLAYLISTS_TABLE")
    abstract override suspend fun getAllPlaylists(): List<YoutubePlaylistInfoCache>

    @Transaction
    @Query("SELECT * FROM PLAYLISTS_TABLE WHERE name = :playlistName")
    abstract override suspend fun getPlaylistWithVideos(playlistName: String): PlaylistWithVideos

}
