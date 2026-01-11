package us.berkovitz.plexapi.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

/**
 * Represents a Plex library section (e.g., Music, Movies, TV Shows).
 *
 * For music libraries, `type` will be "artist".
 */
@Serializable
@SerialName("Directory")
data class LibrarySection(
	val key: String,
	val title: String,
	val type: String,
	val uuid: String? = null,
	val agent: String? = null,
	val scanner: String? = null,
	val language: String? = null,
	val art: String? = null,
	val thumb: String? = null,
	val composite: String? = null,
	val createdAt: Long? = null,
	val updatedAt: Long? = null,
	val scannedAt: Long? = null,
	@XmlElement(true) @SerialName("Location") val locations: List<LibrarySectionLocation>? = null
) : MediaItem() {
	/**
	 * Get artists from this library section with pagination support.
	 * @param start Starting index for pagination (default 0)
	 * @param size Maximum number of items to return (default 100, use 0 for all)
	 */
	suspend fun artists(start: Int = 0, size: Int = 100): List<Artist> {
		if (_server == null) return emptyList()
		return _server!!.artists(key, start, size)
	}

	/**
	 * Get albums from this library section with pagination support.
	 * @param start Starting index for pagination (default 0)
	 * @param size Maximum number of items to return (default 100, use 0 for all)
	 */
	suspend fun albums(start: Int = 0, size: Int = 100): List<Album> {
		if (_server == null) return emptyList()
		return _server!!.albums(key, start, size)
	}

	/**
	 * Get tracks from this library section with pagination support.
	 * @param start Starting index for pagination (default 0)
	 * @param size Maximum number of items to return (default 100, use 0 for all)
	 */
	suspend fun tracks(start: Int = 0, size: Int = 100): List<Track> {
		if (_server == null) return emptyList()
		return _server!!.tracks(key, start, size)
	}

	/**
	 * Get recently added albums from this library section.
	 * @param limit Maximum number of items to return (default 50)
	 */
	suspend fun recentlyAddedAlbums(limit: Int = 50): List<Album> {
		if (_server == null) return emptyList()
		return _server!!.recentlyAddedAlbums(key, limit)
	}

	/**
	 * Get recently played tracks from this library section.
	 * @param limit Maximum number of items to return (default 50)
	 */
	suspend fun recentlyPlayedTracks(limit: Int = 50): List<Track> {
		if (_server == null) return emptyList()
		return _server!!.recentlyPlayedTracks(key, limit)
	}
}

@Serializable
@SerialName("Location")
data class LibrarySectionLocation(
	val id: Long,
	val path: String
)

/**
 * Response container for library sections endpoint.
 */
@Serializable
@SerialName("MediaContainer")
data class LibrarySectionsResponse(
	val size: Long,
	@XmlElement(true) @SerialName("Directory") val sections: List<LibrarySection>
)
