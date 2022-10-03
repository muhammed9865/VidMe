package com.example.vidme.domain.util

object StringUtil {
    fun containsDuration(line: String): Boolean {
        return line.matches(Regex("(?:(\\d{1,3}):)?(\\d{1,2}):(\\d{2})")) || (isDigitsOnly(line) && line.isNotEmpty())
    }

    fun isDigitsOnly(line: String): Boolean {
        return line.all { it.isDigit() }
    }

    fun durationAsString(duration: Int, appendUnit: Boolean = true): String {
        try {
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
                if (appendUnit)
                    append(unit)
            }
        } catch (e: NumberFormatException) {
            return "0:00"
        }
    }

    fun durationFromMilli(millis: Int): String {
        return durationAsString(millis / 1000, appendUnit = false)
    }

    fun calculateDuration(duration: String): Int {
        return if (isDigitsOnly(duration)) {
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


}
