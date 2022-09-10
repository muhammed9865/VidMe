package com.example.vidme.data.extractor

import com.example.vidme.data.extractor.video.YoutubeVideoInfoExtractor
import com.example.vidme.data.pojo.info.Info
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class YoutubePlaylistInfoExtractor @Inject constructor(
) : InfoExtractor {

    private val youtubeVideoInfoExtractor: YoutubeVideoInfoExtractor by lazy {
        YoutubeVideoInfoExtractor()
    }

    /*
        @param linesMap: key: index of line, value: line
     */
    override fun extract(originalUrl: String, lines: Map<Int, String>): Info {
        val playlistInfo = YoutubePlaylistInfo(originalUrl = originalUrl)
        val chunkedLines = chunkMap(lines)



        chunkedLines.forEach {
            val videoInfo = youtubeVideoInfoExtractor.extract(originalUrl, it) as VideoInfo
            playlistInfo.addVideo(videoInfo)
        }

        return playlistInfo
    }

    private fun chunkMap(map: Map<Int, String>): List<Map<Int, String>> {
        val listOfLists = mutableListOf<Map<Int, String>>()

        // TODO FIX BUG RETURNED VALUE IS NOT A LIST OF Maps BUT JUST A LIST OF THE LAST MAP
        // FIX RETURN EVERY 4 KEYS OF THE MAP IN A NEW MAP
        val keysChunked = map.keys.chunked(4)
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