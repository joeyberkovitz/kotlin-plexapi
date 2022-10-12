package us.berkovitz.plexapi.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlElement

@Serializable
@SerialName("MediaContainer")
data class MediaContainer<T>(
	@SerialName("size") val size: Int,
	@XmlElement(true) val elements: Array<T>
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MediaContainer<*>

		if (size != other.size) return false
		if (!elements.contentEquals(other.elements)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = size
		result = 31 * result + elements.contentHashCode()
		return result
	}
}

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
	@SerialName("ratingKey") val ratingKey: Int,
	@SerialName("key") val key: String,
	@SerialName("parentRatingKey") val parentRatingKey: Int,
	@SerialName("grandparentRatingKey") val grandparentRatingKey: Int,
	@SerialName("guid") val guid: String,
	@SerialName("parentGuid") val parentGuid: String,
	@SerialName("grandparentGuid") val grandparentGuid: String,
	@SerialName("parentStudio") val parentStudio: String,
	@SerialName("type") val type: String,
	@SerialName("title") val title: String,
	@SerialName("titleSort") val titleSort: String,
	@SerialName("grandparentKey") val grandparentKey: String,
	@SerialName("parentKey") val parentKey: String,
	@SerialName("librarySectionTitle") val librarySectionTitle: String,
	@SerialName("librarySectionID") val librarySectionID: Int,
	@SerialName("librarySectionKey") val librarySectionKey: String,
	@SerialName("grandparentTitle") val grandparentTitle: String,
	@SerialName("parentTitle") val parentTitle: String,
	@SerialName("summary") val summary: String,
	@SerialName("index") val index: Int,
	@SerialName("parentIndex") val parentIndex: Int,
	@SerialName("ratingCount") val ratingCount: Int,
	@SerialName("viewCount") val viewCount: Int,
	@SerialName("skipCount") val skipCount: Int,
	@SerialName("lastViewedAt") val lastViewedAt: Int,
	@SerialName("parentYear") val parentYear: Int,
	@SerialName("thumb") val thumb: String,
	@SerialName("art") val art: String,
	@SerialName("parentThumb") val parentThumb: String,
	@SerialName("grandparentThumb") val grandparentThumb: String,
	@SerialName("grandparentArt") val grandparentArt: String,
	@SerialName("playlistItemID") val playlistItemID: Int,
	@SerialName("duration") val duration: Int,
	@SerialName("addedAt") val addedAt: String,
	@SerialName("updatedAt") val updatedAt: String,
	@SerialName("musicAnalysisVersion") val musicAnalysisVersion: Int,
	@SerialName("Media") @XmlElement(true) val media: Array<Media>
) : MediaItem() {
	fun getStreamUrl(): String {
		val params = mapOf(
			Pair("path", key),
			Pair("offset", "0"),
			Pair("copyts", "1"),
			Pair("mediaIndex", "0"),
			Pair("X-Plex-Platform", "Chrome"),
			Pair("directStreamAudio", "1"),
			Pair("hasMDE", "1")
		)
		val urlPath = "/:/transcode/universal/start.m3u8"

		return _server!!.urlFor(urlPath, params = params)
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
	@SerialName("id") val id: Int,
	@SerialName("duration") val duration: Int,
	@SerialName("bitrate") val bitrate: Int,
	@SerialName("audioChannels") val audioChannels: Int,
	@SerialName("audioCodec") val audioCodec: String,
	@SerialName("container") val container: String,
	@SerialName("optimizedForStreaming") val optimizedForStreaming: Int,
	@SerialName("audioProfile") val audioProfile: String,
	@SerialName("has64bitOffsets") val has64bitOffsets: Int,
	@SerialName("Part") @XmlElement(true) val parts: Array<Part>
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
	@SerialName("id") val id: Int,
	@SerialName("key") val key: String,
	@SerialName("duration") val duration: Int,
	@SerialName("file") val file: String,
	@SerialName("size") val size: Int,
	@SerialName("audioProfile") val audioProfile: String,
	@SerialName("container") val container: String,
	@SerialName("has64bitOffsets") val has64BitOffsets: Int,
	@SerialName("hasThumbnail") val hasThumbnail: Int,
	@SerialName("optimizedForStreaming") val optimizedForStreaming: Int,
)
