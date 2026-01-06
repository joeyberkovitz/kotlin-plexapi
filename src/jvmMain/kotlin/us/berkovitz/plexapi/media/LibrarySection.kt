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
)

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
