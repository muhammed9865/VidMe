package com.example.vidme.data.cache

import com.example.vidme.data.cache.relation.PlaylistWithVideos
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo

interface CacheDatabase {
    suspend fun saveVideoInfo(videoInfo: VideoInfo)

    suspend fun saveVideosInfo(videosInfo: List<VideoInfo>)

    suspend fun savePlaylistInfo(playlistInfoCache: YoutubePlaylistInfoCache)

    suspend fun savePlaylistInfo(playlistInfo: YoutubePlaylistInfo)

    suspend fun updatePlaylistInfo(
        playlistInfo: YoutubePlaylistInfo,
    ): YoutubePlaylistInfo

    suspend fun getAllVideos(): List<VideoInfo>

    suspend fun getAllPlaylists(): List<YoutubePlaylistInfoCache>

    suspend fun getPlaylistWithVideos(playlistName: String): PlaylistWithVideos

}