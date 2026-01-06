package us.berkovitz.plexapi.media

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import us.berkovitz.plexapi.config.Http
import us.berkovitz.plexapi.logging.LoggingFactory

class PlexServer(
	val baseUrl: String,
	val token: String // either token or accessToken
) {
	companion object {
		private val logger = LoggingFactory.loggerFor(PlexServer::class)
	}

	suspend fun testConnection(): Boolean {
		try {
			val res = get("/", timeout = 5000)
			logger.info("Connection to ${baseUrl} status: ${res.status}: ${res.status.isSuccess()}")
			return res.status.isSuccess()
		} catch (exc: Exception) {
			logger.info("Connection to ${baseUrl} failed: ${exc.message} ${exc.printStackTrace()}")
			return false
		}
	}

	private suspend fun get(
		url: String, args: Map<String, String> = emptyMap(), headers: Map<String, String>? = null,
		timeout: Long = 0
	):
			HttpResponse {
		val urlBuilder = URLBuilder(baseUrl).appendPathSegments(listOf(url), false)
		for (arg in args) {
			urlBuilder.parameters[arg.key] = arg.value
		}
		return Http.authenticatedGet(urlBuilder.buildString(), headers, token, timeout = timeout)
	}

	suspend fun playlists(
		playlistType: PlaylistType? = null, sectionId: String? = null,
		title: String? = null, sort: String? = null
	): Array<Playlist> {
		val args = HashMap<String, String>()
		if (playlistType != null) {
			args["playlistType"] = playlistType.string
		}
		if (sectionId != null) {
			args["sectionID"] = sectionId
		}
		if (title != null) {
			args["title"] = title
		}
		if (sort != null) {
			args["sort"] = sort
		}
		val mediaContainer: MediaContainer<Playlist> = get("/playlists", args = args).body()

		val retArr = mediaContainer.elements.toTypedArray()
		for (playlist in retArr) {
			playlist.setServer(this)
		}

		return retArr
	}

	fun urlFor(path: String, includeToken: Boolean = true, params: Map<String, String> = mapOf()): String {
		val urlOut = URLBuilder(baseUrl).appendPathSegments(path)
		if (includeToken && token.isNotEmpty()) {
			urlOut.parameters["X-Plex-Token"] = token
		}
		params.forEach { (k, v) ->
			urlOut.parameters[k] = v
		}

		return urlOut.buildString()
	}

	/**
	 * Get all library sections from this server.
	 */
	suspend fun librarySections(): List<LibrarySection> {
		val res: LibrarySectionsResponse = get("/library/sections").body()
		return res.sections
	}

	/**
	 * Find the first music library section.
	 * Music libraries have type="artist".
	 */
	suspend fun musicSection(): LibrarySection? {
		return librarySections().find { it.type == "artist" }
	}

	/**
	 * Get all artists from a library section.
	 * @param sectionId The library section key
	 */
	suspend fun artists(sectionId: String): List<Artist> {
		val args = mapOf("type" to "8") // type 8 = artist
		val res: MediaContainer<Artist> = get("/library/sections/$sectionId/all", args).body()
		return res.elements.map { it.also { artist -> artist.setServer(this) } }
	}

	/**
	 * Get all albums from a library section.
	 * @param sectionId The library section key
	 */
	suspend fun albums(sectionId: String): List<Album> {
		val args = mapOf("type" to "9") // type 9 = album
		val res: MediaContainer<Album> = get("/library/sections/$sectionId/all", args).body()
		return res.elements.map { it.also { album -> album.setServer(this) } }
	}

	/**
	 * Get all tracks from a library section.
	 * @param sectionId The library section key
	 */
	suspend fun tracks(sectionId: String): List<Track> {
		val args = mapOf("type" to "10") // type 10 = track
		val res: MediaContainer<Track> = get("/library/sections/$sectionId/all", args).body()
		return res.elements.map { it.also { track -> track.setServer(this) } }
	}

	/**
	 * Get albums for a specific artist.
	 * @param artistRatingKey The artist's rating key
	 */
	suspend fun artistAlbums(artistRatingKey: Long): List<Album> {
		val res: MediaContainer<Album> = get("/library/metadata/$artistRatingKey/children").body()
		return res.elements.map { it.also { album -> album.setServer(this) } }
	}

	/**
	 * Get tracks for a specific album.
	 * @param albumRatingKey The album's rating key
	 */
	suspend fun albumTracks(albumRatingKey: Long): List<Track> {
		val res: MediaContainer<Track> = get("/library/metadata/$albumRatingKey/children").body()
		return res.elements.map { it.also { track -> track.setServer(this) } }
	}

}
