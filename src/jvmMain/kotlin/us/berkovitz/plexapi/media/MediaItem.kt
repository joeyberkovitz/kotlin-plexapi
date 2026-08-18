package us.berkovitz.plexapi.media

import io.ktor.client.call.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlDefault
import nl.adaptivity.xmlutil.serialization.XmlElement
import us.berkovitz.plexapi.config.Http
import java.util.UUID

@Serializable
@SerialName("MediaContainer")
data class MediaContainer<T>(
	@SerialName("size") val size: Long,

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
	val ratingKey: Long,
	val key: String,
	val parentRatingKey: Long?,
	val grandparentRatingKey: Long?,
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
	val librarySectionID: Long?,
	val librarySectionKey: String?,
	val grandparentTitle: String?,
	val parentTitle: String?,
	val originalTitle: String?,
	val summary: String?,
	val index: Long?,
	val parentIndex: Long?,
	val ratingCount: Long?,
	@XmlDefault("0") val viewCount: Long,
	@XmlDefault("0") val skipCount: Long,
	val lastViewedAt: Long?,
	val parentYear: Int?,
	val thumb: String?,
	val art: String?,
	val parentThumb: String?,
	val grandparentThumb: String?,
	val grandparentArt: String?,
	val playlistItemID: Long?,
	@XmlDefault("0") val duration: Long,
	val addedAt: String?,
	val updatedAt: String?,
	val musicAnalysisVersion: Long?,
	@SerialName("Media") @XmlElement(true) val media: List<Media>?
) : MediaItem() {
	companion object {
		suspend fun fromId(id: Long, server: PlexServer): Track? {
			val url = server.urlFor("/library/metadata/$id")
			val res: MediaContainer<Track> = Http.authenticatedGet(url, null, server.token).body()
			if (res.elements.size != 1)
				return null
			return res.elements[0]
		}
	}

	fun getStreamUrl(): String {
		val urlPath = media?.first()?.parts?.first()?.key ?: ""
		return _server!!.urlFor(urlPath)
	}

	fun getTranscodeStreamUrl(bitrate: Int): String {
		val params = mapOf(
			Pair("transcodeSessionId", UUID.randomUUID().toString()),
			Pair("path", key),
			Pair("mediaIndex", "0"),
			Pair("partIndex", "0"),
			Pair("directPlay", "0"),
			Pair("directStreamAudio", "0"),
			Pair("musicBitrate", bitrate.toString()),
			Pair("protocol", "dash"),
			Pair("X-Plex-Client-Profile-Name", "Android"),
			Pair("X-Plex-Client-Profile-Extra", "add-transcode-target(type=musicProfile&context=streaming&protocol=dash&container=webm&audioCodec=opus)")
		)
		return _server!!.urlFor("/music/:/transcode/universal/start.mpd", true, params)
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
	val id: Long,
	@XmlDefault("0") val duration: Long,
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
		return id.hashCode()
	}
}

@Serializable
@SerialName("Part")
data class Part(
	val id: Long,
	val key: String,
	@XmlDefault("0") val duration: Long,
	val file: String,
	@XmlDefault("0") val size: Long,
	val audioProfile: String?,
	val container: String?,
	@XmlDefault("0") val has64BitOffsets: Int,
	@XmlDefault("0") val hasThumbnail: Int,
	@XmlDefault("0") val optimizedForStreaming: Int,
)
