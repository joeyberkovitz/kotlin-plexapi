package us.berkovitz.plexapi.myplex

import kotlinx.serialization.Serializable

@Serializable
data class MyPlexDevice(
	val id: Int,
	val name: String,
	val product: String,
	val version: String,
	val lastSeenAt: String,
)

