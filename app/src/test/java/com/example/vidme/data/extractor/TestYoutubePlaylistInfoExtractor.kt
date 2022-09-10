package com.example.vidme.data.extractor

import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class TestYoutubePlaylistInfoExtractor {

    // lines to be repeated in the map
    private val _lines = listOf(
        "نشيد أخي أنت حر (بدون ايقاع) - النسخة الأصلية",
        "jqHZD9EgO5s",
        "https://rr2---sn-uxaxjvhxbt2u-2nq6.googlevideo.com/videoplayback?expire=1662750428&ei=fDobY66SB6iqxgLd2peIDw&ip=102.40.106.249&id=o-AL1XffvRurz_ncv_2neG-oqt4-GtXtDJS6s8o9-k7B4R&itag=18&source=youtube&requiressl=yes&mh=Ww&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nq6%2Csn-hgn7rnee&ms=au%2Crdu&mv=m&mvi=2&pl=22&initcwndbps=325000&vprv=1&mime=video%2Fmp4&ns=RkkYe61Ls-gOnVbsYyOxXxMH&gir=yes&clen=15813728&ratebypass=yes&dur=365.760&lmt=1625596947494516&mt=1662728466&fvip=2&fexp=24001373%2C24007246&beids=24277092&c=WEB&rbqsm=fr&txp=5438434&n=GRiioJVfWovwTa6mRM&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRgIhAPd9wc0BinZYmJYj6wJajyRX7BBBV1VpPbLH4fGtyCrRAiEAnhbii_Fmhl9Aw-BeNIMNIXkyiM_CipTstxCitV7aBUw%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgeSflW9WpXp5NjbAz1-AbNZv7l5PrZbQIUj5XMWWqXr4CIAs2EWvsI2yZUMbTlZrPih9hkMfzQ5xVgHTuuQOFAJDc",
        "https://i.ytimg.com/vi_webp/jqHZD9EgO5s/maxresdefault.webp"
    )

    lateinit var lines: Map<Int, String>

    lateinit var extractor: YoutubePlaylistInfoExtractor

    @Before
    fun setUp() {
        extractor = YoutubePlaylistInfoExtractor()

        lines = mutableMapOf<Int, String>().apply {
            var count = 0
            repeat(12) {
                this[it] = _lines[count]
                count = if (count == 3) {
                    0
                } else ++count
            }
        }

    }

    @Test
    fun `extractor returns playlist info with count`() {
        val result = extractor.extract(lines[2]!!, lines) as YoutubePlaylistInfo
        assertThat(result.count).isEqualTo(3)
    }

    @Test
    fun `extractor returns playlist info with videos not empty`() {
        val result = extractor.extract(lines[2]!!, lines) as YoutubePlaylistInfo
        assertThat(result.videos).isNotEmpty()

    }

    @Test
    fun `extractor returns playlist with videos that contains id`() {
        val result = extractor.extract(lines[2]!!, lines) as YoutubePlaylistInfo
        val allHasIDs = result.videos.all { it.id.isNotEmpty() }
        assertThat(allHasIDs).isTrue()
    }

    @Test
    fun `extractor returns playlist with videos that contains title`() {
        val result = extractor.extract(lines[2]!!, lines) as YoutubePlaylistInfo
        val allHasIDs = result.videos.all { it.title.isNotEmpty() }
        assertThat(allHasIDs).isTrue()
    }

    @Test
    fun `extractor returns playlist info with videos has correct size expected 3`() {
        val result = extractor.extract(lines[2]!!, lines) as YoutubePlaylistInfo
        assertThat(result.videos).hasSize(3)
    }
}