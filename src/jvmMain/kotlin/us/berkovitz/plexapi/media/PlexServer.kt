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

}
