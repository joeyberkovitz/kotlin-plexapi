package us.berkovitz.plexapi.media

import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlDefault
import nl.adaptivity.xmlutil.serialization.XmlElement
import us.berkovitz.plexapi.config.Http
import us.berkovitz.plexapi.logging.LoggingFactory

enum class PlaylistType(val string: String) {
	AUDIO("audio"),
	VIDEO("video"),
	PHOTO("photo")
}

@Serializable
@SerialName("MediaContainer")
data class PlaylistResponse(
	val size: Int,
	@XmlElement(true) val playlists: Array<Playlist>
)

@Serializable
@SerialName("Playlist")
data class Playlist(
	val addedAt: Int,
	val allowSync: Boolean?,
	val composite: String?,
	val content: String?,
	@XmlDefault("0") val duration: Int, // milliseconds
	val guid: String?,
	val icon: String?,
	val key: String, // includes `/items`
	val leafCount: Int,
	val librarySectionID: Int?,
	val librarySectionKey: String?,
	val librarySectionTitle: String?,
	val playlistType: String?,
	val radio: Boolean?,
	val ratingKey: Int?,
	val smart: Boolean?,
	val summary: String?,
	val title: String,
	val type: String,
	val updatedAt: Int
) {
	companion object {
		private val logger = LoggingFactory.loggerFor(this::class)
	}

	@Transient
	private var _server: PlexServer? = null

	private var _items: Array<MediaItem>? = null

	fun getServer(): PlexServer? = _server

	fun setServer(server: PlexServer) {
		this._server = server
	}

	suspend fun items(): Array<MediaItem> {
		if (_items == null && _server != null) {
			logger.debug("Getting playlist items for ${this.key}")
			val url = URLBuilder(_server!!.baseUrl).appendPathSegments(listOf(key), false).buildString()
			val res: MediaContainer<MediaItem> = Http.authenticatedGet(url, null, _server!!.token).body()

			_items = res.elements.toTypedArray()
			_items!!.forEach {
				it.setServer(_server!!)
			}
		}

		return _items ?: arrayOf()
	}

	fun loadedItems(): Array<MediaItem> {
		return _items ?: emptyArray()
	}

}

