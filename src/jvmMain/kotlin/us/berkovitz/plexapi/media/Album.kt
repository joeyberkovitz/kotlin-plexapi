package us.berkovitz.plexapi.media

import io.ktor.client.call.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlDefault
import nl.adaptivity.xmlutil.serialization.XmlElement
import us.berkovitz.plexapi.config.Http

/**
 * Represents a music album in the Plex library.
 */
@Serializable
@SerialName("Directory")
data class Album(
	val ratingKey: Long,
	val key: String,
	val parentRatingKey: Long? = null,
	val guid: String? = null,
	val parentGuid: String? = null,
	val studio: String? = null,
	val type: String = "album",
	val title: String,
	val titleSort: String? = null,
	val parentKey: String? = null,
	val parentTitle: String? = null,
	val summary: String? = null,
	val index: Int? = null,
	val rating: Double? = null,
	val year: Int? = null,
	@XmlDefault("0") val leafCount: Int = 0,
	@XmlDefault("0") val viewedLeafCount: Int = 0,
	val thumb: String? = null,
	val art: String? = null,
	val parentThumb: String? = null,
	val originallyAvailableAt: String? = null,
	val addedAt: Long? = null,
	val updatedAt: Long? = null,
	val loudnessAnalysisVersion: Int? = null,
	@XmlElement(true) @SerialName("Genre") val genres: List<Tag>? = null,
	@XmlElement(true) @SerialName("Style") val styles: List<Tag>? = null,
	@XmlElement(true) @SerialName("Mood") val moods: List<Tag>? = null,
	@XmlElement(true) @SerialName("Director") val directors: List<Tag>? = null
) : MediaItem() {

	companion object {
		/**
		 * Fetch an album by its rating key.
		 */
		suspend fun fromId(id: Long, server: PlexServer): Album? {
			val url = server.urlFor("/library/metadata/$id")
			val res: MediaContainer<Album> = Http.authenticatedGet(url, null, server.token).body()
			if (res.elements.isEmpty()) return null
			return res.elements[0].also {
				it.setServer(server)
			}
		}
	}

	/**
	 * Get all tracks in this album.
	 */
	suspend fun tracks(): List<Track> {
		if (_server == null) return emptyList()
		val url = _server!!.urlFor("/library/metadata/$ratingKey/children")
		val res: MediaContainer<Track> = Http.authenticatedGet(url, null, _server!!.token).body()
		return res.elements.map {
			it.also { track -> track.setServer(_server!!) }
		}
	}

	/**
	 * Get the parent artist for this album.
	 */
	suspend fun artist(): Artist? {
		if (_server == null || parentRatingKey == null) return null
		return Artist.fromId(parentRatingKey, _server!!)
	}
}
