package com.example.vidme.domain.pojo

import android.os.Parcel
import android.os.Parcelable

data class VideoInfo(
    val id: String,
    val title: String,
    val url: String,
    val thumbnail: String,
    val duration: String,
    val isAudio: Boolean,
    val isVideo: Boolean,
    val isDownloaded: Boolean,
    val playlistName: String?,
    val isDownloading: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "0:00",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeString(thumbnail)
        parcel.writeString(duration)
        parcel.writeByte(if (isAudio) 1 else 0)
        parcel.writeByte(if (isVideo) 1 else 0)
        parcel.writeByte(if (isDownloaded) 1 else 0)
        parcel.writeString(playlistName)
        parcel.writeByte(if (isDownloading) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoInfo> {
        override fun createFromParcel(parcel: Parcel): VideoInfo {
            return VideoInfo(parcel)
        }

        override fun newArray(size: Int): Array<VideoInfo?> {
            return arrayOfNulls(size)
        }
    }

}