package com.example.vidme.data.extractor

import com.example.vidme.data.extractor.video.YoutubeVideoInfoExtractor
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import javax.inject.Inject

class YoutubePlaylistInfoExtractor  @Inject constructor(
    private val youtubeVideoInfoExtractor: YoutubeVideoInfoExtractor
){

    /*
        @param linesMap: key: index of line, value: line
     */
    fun extract(linesMap: Map<Int, String>): YoutubePlaylistInfo {
        val playlistInfo = YoutubePlaylistInfo()
        val lines = chunkMap(linesMap)

        lines.forEach {
            val videoInfo = youtubeVideoInfoExtractor.extract(it)
            playlistInfo.addVideo(videoInfo)
        }


        return playlistInfo
    }

    private fun chunkMap(map: Map<Int, String>) : List<List<String>> {
        val listOfLists = mutableListOf<List<String>>()

        // 4 is the number of variables in @VideoInfo
        map.entries.chunked(4).forEach {
            val lines = mutableListOf<String>()
            it.forEach { line ->
                lines.add(line.value)
            }
            listOfLists.add(lines)
        }

        return listOfLists
    }
}