package us.berkovitz.plexapi.myplex

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlDefault
import nl.adaptivity.xmlutil.serialization.XmlElement


@Serializable
@SerialName("User")
data class MyPlexUser(
    val id: Long,
    val title: String,
    val username: String,
    val email: String,
    val recommendationsPlaylistId: String,
    val thumb: String,
    @XmlDefault("0") val protected: Boolean,
    @XmlDefault("0") val home: Boolean,
    @XmlDefault("0") val allowTuners: Boolean,
    @XmlDefault("0") val allowSync: Boolean,
    @XmlDefault("0") val allowCameraUpload: Boolean,
    @XmlDefault("0") val allowChannels: Boolean,
    @XmlDefault("0") val allowSubtitleAdmin: Boolean,
    val filterAll: String,
    val filterMovies: String,
    val filterMusic: String,
    val filterPhotos: String,
    val filterTelevision: String,
    @XmlDefault("0") val restricted: Boolean,
    @XmlElement(true) val servers: List<MyPlexServer>?
)

@Serializable
@SerialName("Server")
data class MyPlexServer(
    val id: Long,
    val serverId: Long,
    val machineIdentifier: String, // Matched to `MyPlexResource`.clientIdentifier
    val name: String,
    val lastSeenAt: String,
    val numLibraries: Long,
    val allLibraries: Long,
    @XmlDefault("0") val owned: Boolean,
    @XmlDefault("0") val pending: Boolean,
)