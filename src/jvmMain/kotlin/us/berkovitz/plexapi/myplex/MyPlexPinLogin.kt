package us.berkovitz.plexapi.myplex

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import us.berkovitz.plexapi.config.Http
import us.berkovitz.plexapi.logging.LoggingFactory

@Serializable
data class PinResponse(val id: Long, val code: String)

@Serializable
data class ErrorResponse(val code: Long, val message: String)

@Serializable
data class PinCheckResponse(
	val authToken: String?, // equivalent to password
	val clientIdentifier: String?, // equivalent to username
	val errors: List<ErrorResponse>?
)

class MyPlexPinLogin {
	companion object {
		private val logger = LoggingFactory.loggerFor(MyPlexPinLogin::class)

		private const val POLLINTERVAL = 1000L
		private const val PIN_URL = "https://plex.tv/api/v2/pins.json"
		private const val PIN_CHECK_URL = "https://plex.tv/api/v2/pins/%d.json"
	}

	var loginTimeout = 0L

	var pinChangeCb: (String) -> Unit = {}

	suspend fun pinLogin(): PinCheckResponse {
		if (loginTimeout > 0) {
			return withTimeout(loginTimeout) {
				return@withTimeout doPinLogin()
			}
		}
		return doPinLogin()
	}

	private suspend fun doPinLogin(): PinCheckResponse {
		var pin = generatePin()
		delay(POLLINTERVAL)
		while (true) {
			try {
				val checkPinRes = checkPin(pin.id)
				if (!checkPinRes.authToken.isNullOrEmpty()) {
					return checkPinRes
				} else if (!checkPinRes.errors.isNullOrEmpty()) {
					pin = generatePin()
				}
			} catch (cancel: CancellationException) {
				throw cancel
			} catch (ignored: Exception) {
				pin = generatePin()
			}
			delay(POLLINTERVAL)
		}
	}

	private suspend fun generatePin(): PinResponse {
		val res: PinResponse = Http.getClient().post(PIN_URL) {
			headers {
				Http.DEF_HEADERS.map {
					append(it.key, it.value)
				}
			}
		}.body()

		pinChangeCb.invoke(res.code)

		return res
	}

	private suspend fun checkPin(pinId: Long): PinCheckResponse {
		return Http.getClient().get(PIN_CHECK_URL.format(pinId)) {
			headers {
				Http.DEF_HEADERS.map {
					append(it.key, it.value)
				}
			}
		}.body()
	}

}
