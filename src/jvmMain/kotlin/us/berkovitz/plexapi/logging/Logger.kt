package us.berkovitz.plexapi.logging

import kotlin.jvm.Volatile
import kotlin.reflect.KClass

interface Logger {
	fun trace(message: String)
	fun debug(message: String)
	fun warn(message: String)
	fun info(message: String)
	fun error(message: String)
}

interface LoggingFactory {
	fun <T : Any> loggerFor(clazz: KClass<T>): Logger

	companion object {
		@Volatile private var FACTORY: LoggingFactory = KotlinLoggingFactory

		fun <T : Any> loggerFor(clazz: KClass<T>): Logger {
			return FACTORY.loggerFor(clazz)
		}

		fun setFactory(factory: LoggingFactory){
			FACTORY = factory
		}
	}
}
