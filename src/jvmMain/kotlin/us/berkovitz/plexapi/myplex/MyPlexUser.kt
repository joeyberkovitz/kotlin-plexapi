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
    @XmlDefault("0") val protected: PlexBool,
    @XmlDefault("0") val home: PlexBool,
    @XmlDefault("0") val allowTuners: PlexBool,
    @XmlDefault("0") val allowSync: PlexBool,
    @XmlDefault("0") val allowCameraUpload: PlexBool,
    @XmlDefault("0") val allowChannels: PlexBool,
    @XmlDefault("0") val allowSubtitleAdmin: PlexBool,
    val filterAll: String,
    val filterMovies: String,
    val filterMusic: String,
    val filterPhotos: String,
    val filterTelevision: String,
    @XmlDefault("0") val restricted: PlexBool,
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
    @XmlDefault("0") val owned: PlexBool,
    @XmlDefault("0") val pending: PlexBool,
)