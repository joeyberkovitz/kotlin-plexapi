package us.berkovitz.plexapi.myplex

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

@Serializable
@SerialName("Connection")
data class ResourceConnection(
	val protocol: String,
	val address: String,
	val port: String,
	val uri: String,
	val local: Int
)

@Serializable
@SerialName("Device")
data class MyPlexResource(
	val name: String,
	val product: String,
	val productVersion: String,
	val platform: String?,
	val platformVersion: String?,
	val device: String?,
	val clientIdentifier: String?,
	val createdAt: String?,
	val lastSeenAt: String,
	val provides: String?,
	val owned: String?,
	val accessToken: String?,
	val httpsRequired: Int?,
	val synced: String?,
	val relay: Int?,
	val publicAddressMatches: String?,
	val publicAddress: String?,
	val presence: String?,
	@XmlElement(true) val connections: Array<ResourceConnection>?,
)

@Serializable
@SerialName("MediaContainer")
data class ResourceResponse(
	val size: Int,
	@XmlElement(true) val resources: Array<MyPlexResource>
)
