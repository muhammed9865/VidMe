package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.request.PlaylistRequest
import javax.inject.Inject

class DownloadYoutubePlaylistUseCase @Inject constructor() : BaseUseCase() {

    /*
        Checking first if playlist videos are all downloaded
        if not all downloaded, proceed to data layer and downloadPlaylist
        else, send DataState that states download is finished.
     */
    suspend operator fun invoke(
        playlistRequest: PlaylistRequest,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    ) {
        val playlist =
            repository.getStoredYoutubePlaylistByName(playlistName = playlistRequest.playlistInfo!!.name)
        val isAllVideosDownloaded = playlist?.videos?.all { it.isDownloaded }
        if (isAllVideosDownloaded == false)
            repository.downloadPlaylist(playlistRequest, executor, onDownloadInfo)
        else
            onDownloadInfo(DataState.success(DownloadInfo(100f,
                0,
                playlist!!.videos.lastIndex,
                true,
                null)))
    }

}