package us.berkovitz.plexapi.media

import io.ktor.client.call.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlDefault
import nl.adaptivity.xmlutil.serialization.XmlElement
import us.berkovitz.plexapi.config.Http

@Serializable
@SerialName("MediaContainer")
data class MediaContainer<T>(
	@SerialName("size") val size: Int,

	@XmlElement(true) val elements: List<T>
)

@Serializable
abstract class MediaItem {
	@Transient
	var _server: PlexServer? = null

	fun setServer(server: PlexServer) {
		this._server = server
	}
}

@Serializable
@SerialName("Track")
data class Track(
	val ratingKey: Int,
	val key: String,
	val parentRatingKey: Int?,
	val grandparentRatingKey: Int?,
	val guid: String,
	val parentGuid: String?,
	val grandparentGuid: String?,
	val parentStudio: String?,
	val type: String,
	val title: String,
	val titleSort: String?,
	val grandparentKey: String?,
	val parentKey: String?,
	val librarySectionTitle: String?,
	val librarySectionID: Int?,
	val librarySectionKey: String?,
	val grandparentTitle: String?,
	val parentTitle: String?,
	val summary: String?,
	val index: Int?,
	val parentIndex: Int?,
	val ratingCount: Int?,
	@XmlDefault("0") val viewCount: Int,
	@XmlDefault("0") val skipCount: Int,
	val lastViewedAt: Int?,
	val parentYear: Int?,
	val thumb: String?,
	val art: String?,
	val parentThumb: String?,
	val grandparentThumb: String?,
	val grandparentArt: String?,
	val playlistItemID: Int?,
	@XmlDefault("0") val duration: Int,
	val addedAt: String?,
	val updatedAt: String?,
	val musicAnalysisVersion: Int?,
	@SerialName("Media") @XmlElement(true) val media: List<Media>?
) : MediaItem() {
	companion object {
		suspend fun fromId(id: Int, server: PlexServer): Track? {
			val url = server.urlFor("/library/metadata/$id")
			val res: MediaContainer<Track> = Http.authenticatedGet(url, null, server.token).body()
			if (res.elements.size != 1)
				return null
			return res.elements[0]
		}
	}

	fun getStreamUrl(): String {
		/*val params = mapOf(
			Pair("path", key),
			Pair("offset", "0"),
			Pair("copyts", "1"),
			Pair("mediaIndex", "0"),
			Pair("X-Plex-Platform", "Chrome"),
			Pair("directStreamAudio", "1"),
			Pair("hasMDE", "1")
		)
		val urlPath = "/audio/:/transcode/universal/start.m3u8"*/
		val urlPath = media?.first()?.parts?.first()?.key ?: ""

		return _server!!.urlFor(urlPath)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Track

		if (key != other.key) return false

		return true
	}

	override fun hashCode(): Int {
		return key.hashCode()
	}
}

@Serializable
@SerialName("Media")
data class Media(
	val id: Int,
	@XmlDefault("0") val duration: Int,
	@XmlDefault("0") val bitrate: Int,
	@XmlDefault("0") val audioChannels: Int,
	val audioCodec: String?,
	val container: String?,
	@XmlDefault("0") val optimizedForStreaming: Int,
	val audioProfile: String?,
	@XmlDefault("0") val has64bitOffsets: Int,
	@XmlElement(true) val parts: List<Part>?
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Media

		if (id != other.id) return false

		return true
	}

	override fun hashCode(): Int {
		val result = id
		return result
	}
}

@Serializable
@SerialName("Part")
data class Part(
	val id: Int,
	val key: String,
	@XmlDefault("0") val duration: Int,
	val file: String,
	@XmlDefault("0") val size: Int,
	val audioProfile: String?,
	val container: String?,
	@XmlDefault("0") val has64BitOffsets: Int,
	@XmlDefault("0") val hasThumbnail: Int,
	@XmlDefault("0") val optimizedForStreaming: Int,
)
