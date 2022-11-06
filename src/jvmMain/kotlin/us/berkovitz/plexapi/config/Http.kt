package us.berkovitz.plexapi.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.xml.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML
import us.berkovitz.plexapi.logging.LoggingFactory
import us.berkovitz.plexapi.media.MediaItem
import us.berkovitz.plexapi.media.Track

@OptIn(ExperimentalSerializationApi::class, ExperimentalXmlUtilApi::class)
object Http {
	private val logger = LoggingFactory.loggerFor(this::class)
	private var INSTANCE: HttpClient? = null
	val DEF_HEADERS = resetHeaders()

	fun getClient(): HttpClient {
		if (INSTANCE == null) {
			INSTANCE = HttpClient(CIO) {
				install(ContentNegotiation) {
					val serializerModule = SerializersModule {
						polymorphic(MediaItem::class) {
							subclass(Track::class)
						}
					}

					val xmlConfig = XML(serializerModule) {
						val uch = UnknownChildHandler { input, inputKind, descriptor, name, candidates ->
							logger.debug(
								"Ignoring unknown XML child: ${
									descriptor
										.tagName
								}/${name ?: "<CDATA>"}"
							)
							emptyList()
						}
						val newPolicy = DefaultXmlSerializationPolicy(
							false,
							autoPolymorphic = true,
							unknownChildHandler = uch
						)

						policy = newPolicy
						unknownChildHandler = uch
					}

					xml(format = xmlConfig, contentType = ContentType.Application.Xml)
					xml(format = xmlConfig, contentType = ContentType.Text.Xml)
					json(Json {
						ignoreUnknownKeys = true
						explicitNulls = false
					})
				}
			}
		}
		return INSTANCE!!
	}

	suspend fun authenticatedGet(
		url: String, headers: Map<String, String>? = null, token: String = "",
		timeout: Long = 0
	):
			HttpResponse {
		return getClient().get(url) {
			headers {
				//set("Accept", "application/json")
				Http.DEF_HEADERS.map {
					set(it.key, it.value)
				}
				if (token.isNotEmpty()) {
					set("X-Plex-Token", token)
				}
				// override headers with provided values
				headers?.map {
					set(it.key, it.value)
				}
			}
			if (timeout > 0) {
				timeout {
					requestTimeoutMillis = timeout
				}
			}
		}
	}

	fun resetHeaders(): Map<String, String> {
		return mapOf(
			Pair("X-Plex-Platform", Config.X_PLEX_PLATFORM),
			Pair("X-Plex-Platform-Version", Config.X_PLEX_PLATFORM_VERSION),
			Pair("X-Plex-Provides", Config.X_PLEX_PROVIDES),
			Pair("X-Plex-Product", Config.X_PLEX_PRODUCT),
			Pair("X-Plex-Version", Config.X_PLEX_VERSION),
			Pair("X-Plex-Device", Config.X_PLEX_DEVICE),
			Pair("X-Plex-Device-Name", Config.X_PLEX_DEVICE_NAME),
			Pair("X-Plex-Client-Identifier", Config.X_PLEX_IDENTIFIER),
			Pair("X-Plex-Sync-Version", "2"),
			Pair("X-Plex-Features", "external-media")
		)
	}
}
