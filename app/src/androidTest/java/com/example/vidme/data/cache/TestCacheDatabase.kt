package com.example.vidme.data.cache

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TestCacheDatabase {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var cache: CacheDatabase
    lateinit var room: RoomDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        room =
            Room.inMemoryDatabaseBuilder(context, RoomDatabase::class.java)
                .allowMainThreadQueries()
                .setTransactionExecutor(Executors.newSingleThreadExecutor())
                .build()
        cache = room.cacheDao
    }

    @After
    fun tearDown() {
        room.close()
    }

    @Test
    fun saving_videoInfo_actually_saves() = runBlocking {
        val videoInfo = VideoInfo(id = "gXBdi0rSnUE",
            title = "زوجها رجل شحيح ويدعوها للفراش وهي ترفض | الشيخ مصطفى العدوي",
            originalUrl = "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0",
            remoteUrl = "https://rr3---sn-uxaxjvhxbt2u-2nql.googlevideo.com/videoplayback?expire=1662869362&ei=EgsdY46ZBYmsvdIPksWP8A0&ip=156.199.80.248&id=o-AG2KNV3wtHqavbOZyNGL3ZrE7VtneLGzagxlmdiUgYaB&itag=22&source=youtube&requiressl=yes&mh=9y&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nql%2Csn-4g5edn6k&ms=au%2Crdu&mv=m&mvi=3&pl=20&initcwndbps=290000&vprv=1&mime=video%2Fmp4&cnr=14&ratebypass=yes&dur=38.614&lmt=1662830380440624&mt=1662847256&fvip=2&fexp=24001373%2C24007246&c=ANDROID&rbqsm=fr&txp=5318224&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAM0buvZcBbPaquwFw6bINBo3kxGGxvpQEbwHVrG6J-WHAiBh4MT_HA9qySYtrHvasdAPxyuK3jR2_GrwQRaDejWfog%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgP7fo5uu4r5vnPH0QSuEDD8WHYfilXiTxWH4XP2_FtzsCIEzJax_4IVm_8PCO9L6_T5Hf8k9eZui5CC2nHodQa2Fk",
            thumbnail = "https://i.ytimg.com/vi_webp/ToV6BDGhl4w/sddefault.webp",
            isVideo = false,
            isAudio = false,
            storageUrl = null,
            playlistName = null)

        cache.saveVideoInfo(videoInfo)

        val lastSavedId = cache.getAllVideos().last().id
        assertThat(lastSavedId).isEqualTo(videoInfo.id)
    }

    @Test
    fun saving_playlistInfo_actually_saves_playlistInfo() = runBlocking {
        val url = "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0"

        val playlistInfo = YoutubePlaylistInfo("Testing",
            3,
            listOf(
                VideoInfo(id = "gXBdi0rSnUE",
                    title = "زوجها رجل شحيح ويدعوها للفراش وهي ترفض | الشيخ مصطفى العدوي",
                    originalUrl = "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0",
                    remoteUrl = "https://rr3---sn-uxaxjvhxbt2u-2nql.googlevideo.com/videoplayback?expire=1662869362&ei=EgsdY46ZBYmsvdIPksWP8A0&ip=156.199.80.248&id=o-AG2KNV3wtHqavbOZyNGL3ZrE7VtneLGzagxlmdiUgYaB&itag=22&source=youtube&requiressl=yes&mh=9y&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nql%2Csn-4g5edn6k&ms=au%2Crdu&mv=m&mvi=3&pl=20&initcwndbps=290000&vprv=1&mime=video%2Fmp4&cnr=14&ratebypass=yes&dur=38.614&lmt=1662830380440624&mt=1662847256&fvip=2&fexp=24001373%2C24007246&c=ANDROID&rbqsm=fr&txp=5318224&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAM0buvZcBbPaquwFw6bINBo3kxGGxvpQEbwHVrG6J-WHAiBh4MT_HA9qySYtrHvasdAPxyuK3jR2_GrwQRaDejWfog%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgP7fo5uu4r5vnPH0QSuEDD8WHYfilXiTxWH4XP2_FtzsCIEzJax_4IVm_8PCO9L6_T5Hf8k9eZui5CC2nHodQa2Fk",
                    thumbnail = "https://i.ytimg.com/vi_webp/ToV6BDGhl4w/sddefault.webp",
                    isVideo = false,
                    isAudio = false,
                    storageUrl = null,
                    playlistName = "Testing"),
                VideoInfo(id = "ToV6BDGhl4w",
                    title = "حديث بارك الله في بنها وعسلها ! الشيخ مصطفى العدوي",
                    originalUrl = "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0",
                    remoteUrl = "https://rr3---sn-uxaxjvhxbt2u-2nql.googlevideo.com/videoplayback?expire=1662869362&ei=EgsdY46ZBYmsvdIPksWP8A0&ip=156.199.80.248&id=o-AG2KNV3wtHqavbOZyNGL3ZrE7VtneLGzagxlmdiUgYaB&itag=22&source=youtube&requiressl=yes&mh=9y&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nql%2Csn-4g5edn6k&ms=au%2Crdu&mv=m&mvi=3&pl=20&initcwndbps=290000&vprv=1&mime=video%2Fmp4&cnr=14&ratebypass=yes&dur=38.614&lmt=1662830380440624&mt=1662847256&fvip=2&fexp=24001373%2C24007246&c=ANDROID&rbqsm=fr&txp=5318224&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAM0buvZcBbPaquwFw6bINBo3kxGGxvpQEbwHVrG6J-WHAiBh4MT_HA9qySYtrHvasdAPxyuK3jR2_GrwQRaDejWfog%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgP7fo5uu4r5vnPH0QSuEDD8WHYfilXiTxWH4XP2_FtzsCIEzJax_4IVm_8PCO9L6_T5Hf8k9eZui5CC2nHodQa2Fk",
                    thumbnail = "https://i.ytimg.com/vi_webp/ToV6BDGhl4w/sddefault.webp",
                    isVideo = false,
                    isAudio = false,
                    storageUrl = null,
                    playlistName = "Testing"),
                VideoInfo(id = "YknOyrGazG4",
                    title = "عايز حاجة اعملها اقابل بها النبي صلى الله عليه وسلم على الحوض | الشيخ مصطفى العدوي",
                    originalUrl = "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0",
                    remoteUrl = "https://rr3---sn-uxaxjvhxbt2u-2nql.googlevideo.com/videoplayback?expire=1662869362&ei=EgsdY46ZBYmsvdIPksWP8A0&ip=156.199.80.248&id=o-AG2KNV3wtHqavbOZyNGL3ZrE7VtneLGzagxlmdiUgYaB&itag=22&source=youtube&requiressl=yes&mh=9y&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nql%2Csn-4g5edn6k&ms=au%2Crdu&mv=m&mvi=3&pl=20&initcwndbps=290000&vprv=1&mime=video%2Fmp4&cnr=14&ratebypass=yes&dur=38.614&lmt=1662830380440624&mt=1662847256&fvip=2&fexp=24001373%2C24007246&c=ANDROID&rbqsm=fr&txp=5318224&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAM0buvZcBbPaquwFw6bINBo3kxGGxvpQEbwHVrG6J-WHAiBh4MT_HA9qySYtrHvasdAPxyuK3jR2_GrwQRaDejWfog%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgP7fo5uu4r5vnPH0QSuEDD8WHYfilXiTxWH4XP2_FtzsCIEzJax_4IVm_8PCO9L6_T5Hf8k9eZui5CC2nHodQa2Fk",
                    thumbnail = "https://i.ytimg.com/vi_webp/ToV6BDGhl4w/sddefault.webp",
                    isVideo = false,
                    isAudio = false,
                    storageUrl = null,
                    playlistName = "Testing"),
            ),
            url
        )

        cache.savePlaylistInfo(playlistInfo)

        Log.d("SaveStatus", "Saved")

        val lastPlaylist = cache.getAllPlaylists().last()
        assertThat(lastPlaylist.name).isEqualTo(playlistInfo.name)
    }

    @Test
    fun saving_playlistInfo_actually_saves_videos(): Unit = runBlocking {
        val url = "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0"

        val playlistInfo = YoutubePlaylistInfo("Testing",
            3,
            listOf(
                VideoInfo(id = "gXBdi0rSnUE",
                    title = "زوجها رجل شحيح ويدعوها للفراش وهي ترفض | الشيخ مصطفى العدوي",
                    originalUrl = "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0",
                    remoteUrl = "https://rr3---sn-uxaxjvhxbt2u-2nql.googlevideo.com/videoplayback?expire=1662869362&ei=EgsdY46ZBYmsvdIPksWP8A0&ip=156.199.80.248&id=o-AG2KNV3wtHqavbOZyNGL3ZrE7VtneLGzagxlmdiUgYaB&itag=22&source=youtube&requiressl=yes&mh=9y&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nql%2Csn-4g5edn6k&ms=au%2Crdu&mv=m&mvi=3&pl=20&initcwndbps=290000&vprv=1&mime=video%2Fmp4&cnr=14&ratebypass=yes&dur=38.614&lmt=1662830380440624&mt=1662847256&fvip=2&fexp=24001373%2C24007246&c=ANDROID&rbqsm=fr&txp=5318224&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAM0buvZcBbPaquwFw6bINBo3kxGGxvpQEbwHVrG6J-WHAiBh4MT_HA9qySYtrHvasdAPxyuK3jR2_GrwQRaDejWfog%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgP7fo5uu4r5vnPH0QSuEDD8WHYfilXiTxWH4XP2_FtzsCIEzJax_4IVm_8PCO9L6_T5Hf8k9eZui5CC2nHodQa2Fk",
                    thumbnail = "https://i.ytimg.com/vi_webp/ToV6BDGhl4w/sddefault.webp",
                    isVideo = false,
                    isAudio = false,
                    storageUrl = null,
                    playlistName = "Testing"),
                VideoInfo(id = "ToV6BDGhl4w",
                    title = "حديث بارك الله في بنها وعسلها ! الشيخ مصطفى العدوي",
                    originalUrl = "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0",
                    remoteUrl = "https://rr3---sn-uxaxjvhxbt2u-2nql.googlevideo.com/videoplayback?expire=1662869362&ei=EgsdY46ZBYmsvdIPksWP8A0&ip=156.199.80.248&id=o-AG2KNV3wtHqavbOZyNGL3ZrE7VtneLGzagxlmdiUgYaB&itag=22&source=youtube&requiressl=yes&mh=9y&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nql%2Csn-4g5edn6k&ms=au%2Crdu&mv=m&mvi=3&pl=20&initcwndbps=290000&vprv=1&mime=video%2Fmp4&cnr=14&ratebypass=yes&dur=38.614&lmt=1662830380440624&mt=1662847256&fvip=2&fexp=24001373%2C24007246&c=ANDROID&rbqsm=fr&txp=5318224&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAM0buvZcBbPaquwFw6bINBo3kxGGxvpQEbwHVrG6J-WHAiBh4MT_HA9qySYtrHvasdAPxyuK3jR2_GrwQRaDejWfog%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgP7fo5uu4r5vnPH0QSuEDD8WHYfilXiTxWH4XP2_FtzsCIEzJax_4IVm_8PCO9L6_T5Hf8k9eZui5CC2nHodQa2Fk",
                    thumbnail = "https://i.ytimg.com/vi_webp/ToV6BDGhl4w/sddefault.webp",
                    isVideo = false,
                    isAudio = false,
                    storageUrl = null,
                    playlistName = "Testing"),
                VideoInfo(id = "YknOyrGazG4",
                    title = "عايز حاجة اعملها اقابل بها النبي صلى الله عليه وسلم على الحوض | الشيخ مصطفى العدوي",
                    originalUrl = "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0",
                    remoteUrl = "https://rr3---sn-uxaxjvhxbt2u-2nql.googlevideo.com/videoplayback?expire=1662869362&ei=EgsdY46ZBYmsvdIPksWP8A0&ip=156.199.80.248&id=o-AG2KNV3wtHqavbOZyNGL3ZrE7VtneLGzagxlmdiUgYaB&itag=22&source=youtube&requiressl=yes&mh=9y&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nql%2Csn-4g5edn6k&ms=au%2Crdu&mv=m&mvi=3&pl=20&initcwndbps=290000&vprv=1&mime=video%2Fmp4&cnr=14&ratebypass=yes&dur=38.614&lmt=1662830380440624&mt=1662847256&fvip=2&fexp=24001373%2C24007246&c=ANDROID&rbqsm=fr&txp=5318224&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAM0buvZcBbPaquwFw6bINBo3kxGGxvpQEbwHVrG6J-WHAiBh4MT_HA9qySYtrHvasdAPxyuK3jR2_GrwQRaDejWfog%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgP7fo5uu4r5vnPH0QSuEDD8WHYfilXiTxWH4XP2_FtzsCIEzJax_4IVm_8PCO9L6_T5Hf8k9eZui5CC2nHodQa2Fk",
                    thumbnail = "https://i.ytimg.com/vi_webp/ToV6BDGhl4w/sddefault.webp",
                    isVideo = false,
                    isAudio = false,
                    storageUrl = null,
                    playlistName = "Testing"),
            ),
            url
        )

        cache.savePlaylistInfo(playlistInfo)

        val playlistVideos = cache.getPlaylistWithVideos(playlistInfo.name).videos
        assertThat(playlistVideos).containsExactlyElementsIn(playlistInfo.videos)
    }


    @Test
    fun saving_playlistInfo_cache_actually_saves() = runBlocking {
        val playlistInfoCache = YoutubePlaylistInfoCache("Testing",
            3,
            "https://www.youtube.com/playlist?list=PLoInKH8JmRQBGd9ZWTOmx5218u8ha18N0")
        cache.savePlaylistInfo(playlistInfoCache)
        val lastSavedPlaylist = cache.getAllPlaylists().last()
        assertThat(lastSavedPlaylist.name).isEqualTo(playlistInfoCache.name)
    }


}