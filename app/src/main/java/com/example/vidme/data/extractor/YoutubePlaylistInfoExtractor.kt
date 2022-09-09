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

    @Inject
    protected lateinit var youtubeVideoInfoExtractor: YoutubeVideoInfoExtractor

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

    private fun chunkMap(map: Map<Int, String>) : List<Map<Int, String>> {
        val listOfLists = mutableListOf<Map<Int, String>>()
        val newMap = mutableMapOf<Int, String>()

        map.onEachIndexed { index, entry ->
            if (index % 3 == 0 && index != 0) {
                listOfLists.add(newMap)
                newMap.clear()
            } else {
                newMap[entry.key] = entry.value
            }
        }

        return listOfLists
    }
}