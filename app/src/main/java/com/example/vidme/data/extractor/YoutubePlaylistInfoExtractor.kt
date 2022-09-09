package com.example.vidme.data.extractor

import com.example.vidme.data.extractor.video.YoutubeInfoExtractor
import com.example.vidme.data.pojo.info.Info
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import javax.inject.Inject

class YoutubePlaylistInfoExtractor  @Inject constructor(
    private val youtubeVideoInfoExtractor: YoutubeInfoExtractor
) : InfoExtractor {

    /*
        @param linesMap: key: index of line, value: line
     */
    override fun extract(originalUrl: String, linesMap: Map<Int, String>): Info {
        val playlistInfo = YoutubePlaylistInfo(originalUrl = originalUrl)
        val lines = chunkMap(linesMap)

        lines.forEach {
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