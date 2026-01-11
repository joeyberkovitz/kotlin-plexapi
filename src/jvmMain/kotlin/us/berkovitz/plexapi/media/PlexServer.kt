package us.berkovitz.plexapi.media

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import us.berkovitz.plexapi.config.Http
import us.berkovitz.plexapi.logging.LoggingFactory

/**
 * Media type constants for Plex API queries.
 */
enum class MediaType(val value: String) {
	ARTIST("8"),
	ALBUM("9"),
	TRACK("10")
}

class PlexServer(
	val baseUrl: String,
	val token: String // either token or accessToken
) {
	companion object {
		private val logger = LoggingFactory.loggerFor(PlexServer::class)
	}

	/**
	 * Build pagination arguments for API requests.
	 * @param start Starting index (0-based)
	 * @param size Maximum number of items (0 = no limit)
	 * @return Map of pagination parameters
	 */
	private fun paginationArgs(start: Int, size: Int): MutableMap<String, String> {
		val args = mutableMapOf<String, String>()
		if (size > 0) {
			args["X-Plex-Container-Start"] = start.toString()
			args["X-Plex-Container-Size"] = size.toString()
		}
		return args
	}

	/**
	 * Build arguments for a library section query with type and pagination.
	 * @param type The media type to filter by
	 * @param start Starting index for pagination
	 * @param size Maximum number of items
	 * @return Map of query parameters
	 */
	private fun sectionQueryArgs(type: MediaType, start: Int, size: Int): MutableMap<String, String> {
		val args = paginationArgs(start, size)
		args["type"] = type.value
		return args
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
		return res.sections.map { it.also { section -> section.setServer(this) } }
	}

	/**
	 * Find the first music library section.
	 * Music libraries have type="artist".
	 */
	suspend fun musicSection(): LibrarySection? {
		return librarySections().find { it.type == "artist" }
	}

	/**
	 * Get artists from a library section with pagination support.
	 * @param sectionId The library section key
	 * @param start Starting index for pagination (default 0)
	 * @param size Maximum number of items to return (default 100, use 0 for all)
	 */
	suspend fun artists(sectionId: String, start: Int = 0, size: Int = 100): List<Artist> {
		val args = sectionQueryArgs(MediaType.ARTIST, start, size)
		val res: MediaContainer<Artist> = get("/library/sections/$sectionId/all", args).body()
		return res.elements.map { it.also { artist -> artist.setServer(this) } }
	}

	/**
	 * Get albums from a library section with pagination support.
	 * @param sectionId The library section key
	 * @param start Starting index for pagination (default 0)
	 * @param size Maximum number of items to return (default 100, use 0 for all)
	 */
	suspend fun albums(sectionId: String, start: Int = 0, size: Int = 100): List<Album> {
		val args = sectionQueryArgs(MediaType.ALBUM, start, size)
		val res: MediaContainer<Album> = get("/library/sections/$sectionId/all", args).body()
		return res.elements.map { it.also { album -> album.setServer(this) } }
	}

	/**
	 * Get tracks from a library section with pagination support.
	 * @param sectionId The library section key
	 * @param start Starting index for pagination (default 0)
	 * @param size Maximum number of items to return (default 100, use 0 for all)
	 */
	suspend fun tracks(sectionId: String, start: Int = 0, size: Int = 100): List<Track> {
		val args = sectionQueryArgs(MediaType.TRACK, start, size)
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

	/**
	 * Get recently added tracks from the music library.
	 * @param sectionId The music library section key
	 * @param limit Maximum number of items to return (default 50)
	 */
	suspend fun recentlyAddedTracks(sectionId: String, limit: Int = 50): List<Track> {
		val args = sectionQueryArgs(MediaType.TRACK, 0, limit)
		args["sort"] = "addedAt:desc"
		val res: MediaContainer<Track> = get("/library/sections/$sectionId/all", args).body()
		return res.elements.map { it.also { track -> track.setServer(this) } }
	}

	/**
	 * Get recently added albums from the music library.
	 * @param sectionId The music library section key
	 * @param limit Maximum number of items to return (default 50)
	 */
	suspend fun recentlyAddedAlbums(sectionId: String, limit: Int = 50): List<Album> {
		val args = sectionQueryArgs(MediaType.ALBUM, 0, limit)
		args["sort"] = "addedAt:desc"
		val res: MediaContainer<Album> = get("/library/sections/$sectionId/all", args).body()
		return res.elements.map { it.also { album -> album.setServer(this) } }
	}

	/**
	 * Get recently played tracks from the music library.
	 * @param sectionId The music library section key
	 * @param limit Maximum number of items to return (default 50)
	 */
	suspend fun recentlyPlayedTracks(sectionId: String, limit: Int = 50): List<Track> {
		val args = sectionQueryArgs(MediaType.TRACK, 0, limit)
		args["sort"] = "lastViewedAt:desc"
		args["viewCount%3E"] = "0"
		val res: MediaContainer<Track> = get("/library/sections/$sectionId/all", args).body()
		return res.elements.map { it.also { track -> track.setServer(this) } }
	}

	/**
	 * Get on deck / continue listening items.
	 */
	suspend fun onDeck(): List<Track> {
		try {
			val res: MediaContainer<Track> = get("/library/onDeck").body()
			return res.elements.map { it.also { track -> track.setServer(this) } }
		} catch (e: Exception) {
			logger.warn("Failed to get onDeck: ${e.message}")
			return emptyList()
		}
	}

}
