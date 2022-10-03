package com.example.vidme.domain.pojo.request

abstract class Request(
    open val type: String = TYPE_AUDIO,
    open val quality: String = QUALITY_BEST,
) {
    fun buildType(): String {
        return buildString {
            append(quality)
            if (type == TYPE_AUDIO)
                append(type)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Request> update(func: (request: T) -> T): T {
        return func(this as T)
    }


    fun isAudio() = type == TYPE_AUDIO

    companion object {
        const val TYPE_AUDIO = "audio"
        const val TYPE_VIDEO = "video"
        const val QUALITY_BEST = "best"
        const val QUALITY_WORST = "worst"

    }
}