package us.berkovitz.plexapi.myplex

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import us.berkovitz.plexapi.config.Http
import us.berkovitz.plexapi.logging.LoggingFactory
import us.berkovitz.plexapi.media.MediaContainer

@Serializable
@SerialName("user")
data class User(
	val email: String,
	val id: Long,
	val uuid: String,
	val authToken: String
)

class MyPlexAccount(val token: String) {
	companion object {
		private val logger = LoggingFactory.loggerFor(MyPlexAccount::class)
		private const val SIGN_IN_URL = "https://plex.tv/users/sign_in.xml"

		suspend fun login(username: String, password: String): String {
			val res: User = Http.getClient().post(SIGN_IN_URL) {
				headers {
					Http.DEF_HEADERS.map {
						append(it.key, it.value)
					}
				}
				basicAuth(username, password)
			}.body()
			return res.authToken
		}
	}

	private suspend fun get(url: String, headers: Map<String, String>? = null): HttpResponse {
		return Http.authenticatedGet(url, headers, token)
	}

	suspend fun resources(): Array<MyPlexResource> {
		logger.debug("Getting resources")
		val res: MediaContainer<MyPlexResource> =
			get("https://plex.tv/api/resources?includeHttps=1&includeRelay=1").body()
		logger.debug(res.elements.toTypedArray().contentDeepToString())
		return res.elements.toTypedArray()
	}

	suspend fun devices(): Array<MyPlexDevice> {
		logger.debug("Getting devices")
		val res: MediaContainer<MyPlexDevice> = get("https://plex.tv/devices.json").body()
		logger.debug(res.elements.toTypedArray().contentDeepToString())
		return res.elements.toTypedArray()
	}
}
