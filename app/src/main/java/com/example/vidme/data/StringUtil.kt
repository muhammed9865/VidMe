package com.example.vidme.data

object StringUtil {
    fun containsDuration(line: String): Boolean {
        return line.matches(Regex("""(?:(\d{1,3}):)?(\d{1,2}):(\d{2})"""))
    }

    fun isDigitsOnly(line: String): Boolean {
        return line.all { it.isDigit() }
    }
}
// (?:(\d{1,3}):)?(\d{1,2}):(\d{2})