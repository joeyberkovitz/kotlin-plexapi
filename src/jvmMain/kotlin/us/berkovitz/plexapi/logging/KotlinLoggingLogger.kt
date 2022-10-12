package us.berkovitz.plexapi.logging

import mu.KotlinLogging
import kotlin.reflect.KClass


class KotlinLoggingLogger<T: Any>(clazz: KClass<T>): Logger {
	private val logger = KotlinLogging.logger(clazz.simpleName ?: "N/A")

	override fun trace(message: String) {
		logger.trace { message }
	}

	override fun debug(message: String) {
		logger.debug { message }
	}

	override fun warn(message: String) {
		logger.warn { message }
	}

	override fun info(message: String) {
		logger.info { message }
	}

	override fun error(message: String) {
		logger.error { message }
	}
}

object KotlinLoggingFactory: LoggingFactory {
	override fun <T : Any> loggerFor(clazz: KClass<T>): Logger {
		return KotlinLoggingLogger(clazz)
	}
}
