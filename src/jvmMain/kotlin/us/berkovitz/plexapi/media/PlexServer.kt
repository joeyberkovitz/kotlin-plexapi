package us.berkovitz.plexapi.media

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import us.berkovitz.plexapi.config.Http
import us.berkovitz.plexapi.logging.LoggingFactory

class PlexServer(
	val baseUrl: String,
	val token: String
) {
	companion object {
		private val logger = LoggingFactory.loggerFor(PlexServer::class)
	}

	private suspend fun get(url: String, args: Map<String, String>, headers: Map<String, String>? = null):
			HttpResponse {
		val urlBuilder = URLBuilder(baseUrl).appendPathSegments(listOf(url), false)
		for (arg in args) {
			urlBuilder.parameters[arg.key] = arg.value
		}
		return Http.authenticatedGet(urlBuilder.buildString(), headers, token)
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
		val mediaContainer: PlaylistResponse = get("/playlists", args = args).body()

		for (playlist in mediaContainer.playlists) {
			playlist.setServer(this)
		}

		return mediaContainer.playlists
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
