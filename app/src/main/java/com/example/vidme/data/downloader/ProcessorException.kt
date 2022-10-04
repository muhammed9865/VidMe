package com.example.vidme.data.downloader

sealed class ProcessorException(val message: String) {
    object PrivatePlaylistException :
        ProcessorException(message = "Playlist visibility is private, try changing it to public")

    object UnknownException : ProcessorException(message = "Something went wrong")
    companion object {
        fun from(exception: Exception): ProcessorException {
            val message = exception.message

            if (message?.contains("The playlist does not exist") == true) {
                return PrivatePlaylistException
            }

            return UnknownException
        }
    }
}
