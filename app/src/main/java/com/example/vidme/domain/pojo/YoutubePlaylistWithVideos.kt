package com.example.vidme.domain.pojo

import com.example.vidme.domain.util.StringUtil

data class YoutubePlaylistWithVideos(
    val playlistInfo: YoutubePlaylistInfo,
    val videos: List<VideoInfo>,
) {
    fun getPlaylistName() = playlistInfo.name
    fun getCount() = playlistInfo.count
    fun getOriginalUrl() = playlistInfo.originalUrl
    fun getFullDuration() = durationAsString(videos.sumOf { calculateDuration(it.duration) })

    private fun calculateDuration(duration: String): Int {
        return if (StringUtil.isDigitsOnly(duration)) {
            duration.toInt()
        } else {
            var durationAsInt = 0
            val timeSplit = duration.split(":").map { it.toInt() }
            when (timeSplit.size) {
                3 -> {
                    durationAsInt += timeSplit[0] * 3600
                    durationAsInt += timeSplit[1] * 60
                    durationAsInt += timeSplit[2]
                }
                2 -> {
                    durationAsInt += timeSplit[0] * 60
                    durationAsInt += timeSplit[1]
                }
                1 -> {
                    durationAsInt += timeSplit[0]
                }
            }

            durationAsInt
        }
    }


    private fun durationAsString(duration: Int): String {
        val hours = duration / 3600
        val minutes = (duration - 3600 * hours) / 60
        val seconds = (duration - 3600 * hours - 60 * minutes)
        return buildString {
            if (hours > 0) append("$hours:")
            append("$minutes:")
            if (seconds < 10) {
                append("0$seconds")
            } else
                append(seconds)
        }
    }
}