package com.example.vidme.data.extractor

import com.example.vidme.data.extractor.video.YoutubeVideoInfoExtractor
import com.example.vidme.data.pojo.info.Info
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class YoutubePlaylistInfoExtractor @Inject constructor(
    private val playlistName: String,
) : InfoExtractor {

    private val youtubeVideoInfoExtractor: YoutubeVideoInfoExtractor by lazy {
        YoutubeVideoInfoExtractor()
    }

    /*
        @param linesMap: key: index of line, value: line
     */
    override fun extract(originalUrl: String, outputLines: Map<Int, String>): Info {
        val playlistInfo = YoutubePlaylistInfo(originalUrl = originalUrl, name = playlistName)
        val chunkedOutputLines = chunkMap(outputLines)



        chunkedOutputLines.forEach { linesChunk ->
            var videoInfo = youtubeVideoInfoExtractor.extract(originalUrl, linesChunk) as VideoInfo
            videoInfo = videoInfo.copy(playlistName = playlistName)
            playlistInfo.addVideo(videoInfo)
        }

        return playlistInfo
    }

    private fun chunkMap(map: Map<Int, String>): List<Map<Int, String>> {
        val listOfLists = mutableListOf<Map<Int, String>>()

        // 5 is the number of Variables fetched from outputLines
        val keysChunked = map.keys.chunked(5)
        keysChunked.forEach { keys ->
            val newMap = mutableMapOf<Int, String>()
            keys.forEach { key ->
                newMap[key] = map[key]!!
            }
            listOfLists.add(newMap)
        }


        return listOfLists
    }
}