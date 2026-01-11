package us.berkovitz.plexapi.media

import kotlinx.serialization.Serializable

/**
 * Generic tag element used for genres, countries, styles, moods, directors, etc.
 */
@Serializable
data class Tag(
	val tag: String,
	val id: Long? = null
)
