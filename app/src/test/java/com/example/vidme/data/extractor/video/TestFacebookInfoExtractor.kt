package com.example.vidme.data.extractor.video

import com.example.vidme.data.pojo.info.VideoInfo
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test


class TestFacebookInfoExtractor {


    private val lines = mapOf(
        0 to "غش الزوجية.. ولزومة إيه الحماقي ما عصام كاريكا أهوو",
        1 to "489680029198280",
        2 to "https://video.fcai19-3.fna.fbcdn.net/v/t39.25447-2/305620443_171016172143854_6917138935954340354_n.mp4?_nc_cat=1&vs=2a563b7577864af5&_nc_vs=HBksFQAYJEdOdGxOeEx1QkRIT2lac0FBQUw2bExNMm5mNWZibWRqQUFBRhUAAsgBABUAGCRHUFF4TGhJN3BaRi1TWndCQURBYlBkRkxQX1owYnJGcUFBQUYVAgLIAQBLBogScHJvZ3Jlc3NpdmVfcmVjaXBlATENc3Vic2FtcGxlX2ZwcwAQdm1hZl9lbmFibGVfbnN1YgAgbWVhc3VyZV9vcmlnaW5hbF9yZXNvbHV0aW9uX3NzaW0AKGNvbXB1dGVfc3NpbV9vbmx5X2F0X29yaWdpbmFsX3Jlc29sdXRpb24AEWRpc2FibGVfcG9zdF9wdnFzABUAJQAcAAAmlILc3KiLrAEVAigCQzMYC3Z0c19wcmV2aWV3HBdAazIcrAgxJxggZGFzaF92NF81c2VjZ29wX2hxMl9mcmFnXzJfdmlkZW8SABgYdmlkZW9zLnZ0cy5jYWxsYmFjay5wcm9kOBJWSURFT19WSUVXX1JFUVVFU1QbCogVb2VtX3RhcmdldF9lbmNvZGVfdGFnBm9lcF9oZBNvZW1fcmVxdWVzdF90aW1lX21zATAMb2VtX2NmZ19ydWxlB3VubXV0ZWQTb2VtX3JvaV9yZWFjaF9jb3VudAczNTk0MjgwEW9lbV9pc19leHBlcmltZW50AAxvZW1fdmlkZW9faWQPNDg5NjgwMDI5MTk4MjgwEm9lbV92aWRlb19hc3NldF9pZBAzMDY1MjM3MjkzNzczNzg1FW9lbV92aWRlb19yZXNvdXJjZV9pZA8zNzg0MjY0NDQ0NDc4ODIcb2VtX3NvdXJjZV92aWRlb19lbmNvZGluZ19pZBAxMjY5MTEzMzMwNTY3MTA5DnZ0c19yZXF1ZXN0X2lkACUCHAAlvgEbB4gBcwQzNzMxAmNkCjIwMjItMDktMDUDcmNiBzM1OTQyMDADYXBwBlZpZGVvcwJjdBlDT05UQUlORURfUE9TVF9BVFRBQ0hNRU5UE29yaWdpbmFsX2R1cmF0aW9uX3MIMjE3LjU5NDICdHMVcHJvZ3Jlc3NpdmVfZW5jb2RpbmdzAA%3D%3D&ccb=1-7&_nc_sid=8bf8af&efg=eyJ2ZW5jb2RlX3RhZyI6Im9lcF9oZCJ9&_nc_ohc=IPKEaPsVsPEAX9sdtI3&_nc_ht=video.fcai19-3.fna&oh=00_AT_LkMFVU1iVjJX3HfQZ9Uq2qP6WJND6Xox7HyuGbw6HyQ&oe=631F5F12&_nc_rid=370400416520313",
        3 to "https://scontent.fcai19-3.fna.fbcdn.net/v/t15.5256-10/301387252_3065253100438871_1319834634740023195_n.jpg?stp=dst-jpg_p180x540&_nc_cat=1&ccb=1-7&_nc_sid=ad6a45&_nc_ohc=JD5CkHcPULEAX_RhM3Q&_nc_ht=scontent.fcai19-3.fna&oh=00_AT_M-WPIQ42h7CunsRwexOr7xVI2l38bcow58gb6myVgrw&oe=631FF62B"

    )

    lateinit var extractor: FacebookInfoExtractor

    @Before
    fun setUp() {
        extractor = FacebookInfoExtractor()
    }




    @Test
    fun `list of string returns VideoInfo with title`() {
        val result = extractor.extract(lines[2]!!, lines) as VideoInfo
        assertThat(result.title).isEqualTo(lines[0])
    }

    @Test
    fun `list of string returns VideoInfo with id`() {
        val result = extractor.extract(lines[2]!!, lines) as VideoInfo
        assertThat(result.id).isEqualTo(lines[1])
    }

    @Test
    fun `list of string returns VideoInfo with remoteUrl`() {
        val result = extractor.extract(lines[2]!!, lines) as VideoInfo
        assertThat(result.remoteUrl).isEqualTo(lines[2])
    }

    @Test
    fun `list of string returns VideoInfo with thumbnail`() {
        val result = extractor.extract(lines[2]!!, lines) as VideoInfo
        assertThat(result.thumbnail).isEqualTo(lines[3])
    }
}