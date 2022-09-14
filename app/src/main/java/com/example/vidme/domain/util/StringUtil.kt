package com.example.vidme.domain.util

object StringUtil {
    fun containsDuration(line: String): Boolean {
        return line.matches(Regex("(?:(\\d{1,3}):)?(\\d{1,2}):(\\d{2})")) || (isDigitsOnly(line) && line.length == 2)
    }

    fun isDigitsOnly(line: String): Boolean {
        return line.all { it.isDigit() }
    }

    fun durationAsString(duration: Int): String {
        val hours = duration / 3600
        val minutes = (duration - 3600 * hours) / 60
        val seconds = (duration - 3600 * hours - 60 * minutes)
        val unit = if (hours > 0) "h" else if (minutes > 0) "m" else "s"
        return buildString {
            if (hours > 0) append("$hours:")
            append("$minutes:")
            if (seconds < 10) {
                append("0$seconds")
            } else
                append(seconds)
            append(unit)
        }
    }


}
