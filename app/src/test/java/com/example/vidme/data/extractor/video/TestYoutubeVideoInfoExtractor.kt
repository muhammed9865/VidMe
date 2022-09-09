package com.example.vidme.data.extractor.video

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TestYoutubeVideoInfoExtractor {

    private val lines = listOf(
        "نشيد أخي أنت حر (بدون ايقاع) - النسخة الأصلية",
        "jqHZD9EgO5s",
        "https://rr2---sn-uxaxjvhxbt2u-2nq6.googlevideo.com/videoplayback?expire=1662744559&ei=jyMbY9XsM8S7xN8P6PS8qAs&ip=102.40.106.249&id=o-ALvmzjWnrEYQb5PuPTgiekDCDxN7naWkqZVEGPtoBXEJ&itag=399&aitags=133%2C134%2C135%2C136%2C137%2C160%2C242%2C243%2C244%2C247%2C248%2C278%2C394%2C395%2C396%2C397%2C398%2C399&source=youtube&requiressl=yes&mh=Ww&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nq6%2Csn-hgn7yn76&ms=au%2Crdu&mv=m&mvi=2&pl=22&initcwndbps=306250&vprv=1&mime=video%2Fmp4&ns=yGo5jc2c_tYvBQtQWEGRu-YH&gir=yes&clen=40812558&dur=365.633&lmt=1625752903358638&mt=1662722462&fvip=4&keepalive=yes&fexp=24001373%2C24007246&c=WEB&rbqsm=fr&txp=5436434&n=m8QCtx_w7XQ4RMxgVZ&sparams=expire%2Cei%2Cip%2Cid%2Caitags%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRQIgX7qw94_yEV-nrlqDiUJ0bLrtdnJMUFcuGNKlvJiuHeACIQC6-t0UzCIOQdsqggCx3dJBF8OnxjiQv-MFPMoYbMvQJg%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgPcpXgD12-kY4rlRHCVL01v7PlYX9tcRcfgwzwCfNMOkCIG2C_K06DTP4xZxKNWS35k5d2YwY0A8K6QhIFPyv09YU" +
                "https://rr2---sn-uxaxjvhxbt2u-2nq6.googlevideo.com/videoplayback?expire=1662744559&ei=jyMbY9XsM8S7xN8P6PS8qAs&ip=102.40.106.249&id=o-ALvmzjWnrEYQb5PuPTgiekDCDxN7naWkqZVEGPtoBXEJ&itag=251&source=youtube&requiressl=yes&mh=Ww&mm=31%2C29&mn=sn-uxaxjvhxbt2u-2nq6%2Csn-hgn7yn76&ms=au%2Crdu&mv=m&mvi=2&pl=22&initcwndbps=306250&vprv=1&mime=audio%2Fwebm&ns=yGo5jc2c_tYvBQtQWEGRu-YH&gir=yes&clen=6140593&dur=365.741&lmt=1625597268598837&mt=1662722462&fvip=4&keepalive=yes&fexp=24001373%2C24007246&c=WEB&rbqsm=fr&txp=5432434&n=m8QCtx_w7XQ4RMxgVZ&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRgIhANao61DAEOWdydwPlDhNBxckevtLDzWMRXU60ThPkIUhAiEAlMAqSJ_g9fR38A4cdOrxmmFxV9b7hzaHUGDUQE5msQ0%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgPcpXgD12-kY4rlRHCVL01v7PlYX9tcRcfgwzwCfNMOkCIG2C_K06DTP4xZxKNWS35k5d2YwY0A8K6QhIFPyv09YU",
        "https://i.ytimg.com/vi_webp/jqHZD9EgO5s/maxresdefault.webp"

    )



    @Test
    fun `list of string returns VideoInfo with title`() {
        val extractor = YoutubeVideoInfoExtractor()
        val result = extractor.extract(lines)
        assertThat(result.title).isEqualTo(lines[0])
    }
    @Test
    fun `list of string returns VideoInfo with id`() {
        val extractor = YoutubeVideoInfoExtractor()
        val result = extractor.extract(lines)
        assertThat(result.id).isEqualTo(lines[1])
    }

    @Test
    fun `list of string returns VideoInfo with remoteUrl`() {
        val extractor = YoutubeVideoInfoExtractor()
        val result = extractor.extract(lines)
        assertThat(result.remoteUrl).isEqualTo(lines[2])
    }

    @Test
    fun `list of string returns VideoInfo with thumbnail`() {
        val extractor = YoutubeVideoInfoExtractor()
        val result = extractor.extract(lines)
        assertThat(result.thumbnail).isEqualTo(lines[3])
    }
}