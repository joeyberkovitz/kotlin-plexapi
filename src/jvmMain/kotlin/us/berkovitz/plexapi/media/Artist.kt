package us.berkovitz.plexapi.media

import io.ktor.client.call.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import us.berkovitz.plexapi.config.Http

/**
 * Represents a music artist in the Plex library.
 */
@Serializable
@SerialName("Directory")
data class Artist(
	val ratingKey: Long,
	val key: String,
	val guid: String? = null,
	val type: String = "artist",
	val title: String,
	val titleSort: String? = null,
	val summary: String? = null,
	val index: Int? = null,
	val thumb: String? = null,
	val art: String? = null,
	val addedAt: Long? = null,
	val updatedAt: Long? = null,
	@XmlElement(true) @SerialName("Genre") val genres: List<Tag>? = null,
	@XmlElement(true) @SerialName("Country") val countries: List<Tag>? = null,
	@XmlElement(true) @SerialName("Style") val styles: List<Tag>? = null,
	@XmlElement(true) @SerialName("Mood") val moods: List<Tag>? = null
) : MediaItem() {

	companion object {
		/**
		 * Fetch an artist by its rating key.
		 */
		suspend fun fromId(id: Long, server: PlexServer): Artist? {
			val url = server.urlFor("/library/metadata/$id")
			val res: MediaContainer<Artist> = Http.authenticatedGet(url, null, server.token).body()
			if (res.elements.isEmpty()) return null
			return res.elements[0].also {
				it.setServer(server)
			}
		}
	}

	/**
	 * Get all albums by this artist.
	 */
	suspend fun albums(): List<Album> {
		if (_server == null) return emptyList()
		val url = _server!!.urlFor("/library/metadata/$ratingKey/children")
		val res: MediaContainer<Album> = Http.authenticatedGet(url, null, _server!!.token).body()
		return res.elements.map {
			it.also { album -> album.setServer(_server!!) }
		}
	}

	/**
	 * Get all tracks by this artist (across all albums).
	 */
	suspend fun tracks(): List<Track> {
		if (_server == null) return emptyList()
		val url = _server!!.urlFor("/library/metadata/$ratingKey/allLeaves")
		val res: MediaContainer<Track> = Http.authenticatedGet(url, null, _server!!.token).body()
		return res.elements.map {
			it.also { track -> track.setServer(_server!!) }
		}
	}
}
