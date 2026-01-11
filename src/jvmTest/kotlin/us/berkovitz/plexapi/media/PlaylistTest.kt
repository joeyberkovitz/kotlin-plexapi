package us.berkovitz.plexapi.media

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import us.berkovitz.plexapi.config.Http
import us.berkovitz.plexapi.myplex.MyPlexAccount
import us.berkovitz.plexapi.myplex.PlexBool
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class PlaylistTest {
    @Test
    fun testListPlaylists() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = """<?xml version="1.0" encoding="UTF-8"?>
<MediaContainer size="22">
    <Playlist ratingKey="1" key="/playlists/1/items" guid="com.plexapp.agents.none://uuid-1" type="playlist" title="PLIST-TITLE" titleSort="PLIST-TITLESORT" summary="PLIST-SUMMARY" smart="1" playlistType="audio" composite="/playlists/1/composite/10" icon="playlist://image.smart" viewCount="1" lastViewedAt="1629594409" duration="1495000" leafCount="6" addedAt="1629594409" updatedAt="1761732698"></Playlist>
    <Playlist ratingKey="2" key="/playlists/2/items" guid="com.plexapp.agents.none://uuid-2" type="playlist" title="PLIST2-TITLE" summary="PLIST2-SUMMARY" smart="1" playlistType="audio" composite="/playlists/2/composite/11" icon="playlist://image.smart" viewCount="2" lastViewedAt="1683470163" duration="1917121000" leafCount="7446" addedAt="1629594409" updatedAt="1761732698"></Playlist>
</MediaContainer>
                """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/xml")
                )
            }

            Http.setEngine(mockEngine)

            val playlists = PlexServer("https://localhost", "TEST-TOKEN").playlists()

            val expectedPlaylists = arrayOf<Playlist>(
                Playlist(
                    ratingKey = 1,
                    key = "/playlists/1/items",
                    guid = "com.plexapp.agents.none://uuid-1",
                    type = "playlist",
                    title = "PLIST-TITLE",
                    titleSort = "PLIST-TITLESORT",
                    summary = "PLIST-SUMMARY",
                    smart = true,
                    playlistType = "audio",
                    composite = "/playlists/1/composite/10",
                    icon = "playlist://image.smart",
                    duration = 1495000L,
                    leafCount = 6,
                    addedAt = 1629594409,
                    updatedAt = 1761732698L,
                    allowSync = null,
                    content = null,
                    librarySectionID = null,
                    librarySectionKey = null,
                    librarySectionTitle = null,
                    radio = null,
                ),
                Playlist(
                    ratingKey = 2,
                    key = "/playlists/2/items",
                    guid = "com.plexapp.agents.none://uuid-2",
                    type = "playlist",
                    title = "PLIST2-TITLE",
                    titleSort = null,
                    summary = "PLIST2-SUMMARY",
                    smart = true,
                    playlistType = "audio",
                    composite = "/playlists/2/composite/11",
                    icon = "playlist://image.smart",
                    duration = 1917121000L,
                    leafCount = 7446,
                    addedAt = 1629594409,
                    updatedAt = 1761732698,
                    allowSync = null,
                    content = null,
                    librarySectionID = null,
                    librarySectionKey = null,
                    librarySectionTitle = null,
                    radio = null,
                )
            )

            assertContentEquals(expectedPlaylists, playlists)
        }
    }

}