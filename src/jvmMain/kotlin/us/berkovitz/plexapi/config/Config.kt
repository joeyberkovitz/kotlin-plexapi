package us.berkovitz.plexapi.config

import java.net.InetAddress
import java.util.*

object Config {
	var X_PLEX_PROVIDES = "controller"
	var X_PLEX_PLATFORM = System.getProperty("os.name")
	var X_PLEX_PLATFORM_VERSION = System.getProperty("os.version")
	var X_PLEX_PRODUCT = "PlexAPI"
	var X_PLEX_VERSION = "1.0.0"
	var X_PLEX_DEVICE = X_PLEX_PLATFORM
	var X_PLEX_DEVICE_NAME = InetAddress.getLocalHost().hostName
	var X_PLEX_IDENTIFIER = UUID.randomUUID().toString() // should be consistent once logged in
}
