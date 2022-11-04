package us.berkovitz.plexapi.config

import java.util.*

object Config {
	var X_PLEX_PROVIDES = "controller"
	var X_PLEX_PLATFORM = System.getProperty("os.name")
	var X_PLEX_PLATFORM_VERSION = System.getProperty("os.version")
	var X_PLEX_PRODUCT = "PlexAPI"
	var X_PLEX_VERSION = "1.0.0"
	var X_PLEX_DEVICE = X_PLEX_PLATFORM

	// can't get name easily without triggering network false positives
	// client is responsible for providing name
	var X_PLEX_DEVICE_NAME = "N/A"
	var X_PLEX_IDENTIFIER = UUID.randomUUID().toString() // should be consistent once logged in
}
