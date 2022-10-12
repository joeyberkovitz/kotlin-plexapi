package us.berkovitz.plexapi.myplex

import io.ktor.client.call.*
import io.ktor.client.statement.*
import us.berkovitz.plexapi.config.Http
import us.berkovitz.plexapi.logging.LoggingFactory

class MyPlexAccount(val token: String) {
	companion object {
		private val logger = LoggingFactory.loggerFor(MyPlexAccount::class)
	}

	private suspend fun get(url: String, headers: Map<String, String>? = null): HttpResponse {
		return Http.authenticatedGet(url, headers, token)
	}

	suspend fun resources(): Array<MyPlexResource> {
		logger.debug("Getting resources")
		val res: Array<MyPlexResource> = get("https://plex.tv/api/resources?includeHttps=1&includeRelay=1").body()
		logger.debug(res.contentDeepToString())
		return res
	}

	suspend fun devices(): Array<MyPlexDevice> {
		logger.debug("Getting devices")
		val res: Array<MyPlexDevice> = get("https://plex.tv/devices.json").body()
		logger.debug(res.contentDeepToString())
		return res
	}
}
