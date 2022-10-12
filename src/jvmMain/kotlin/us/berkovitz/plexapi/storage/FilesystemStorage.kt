package us.berkovitz.plexapi.storage

import java.io.File

object FilesystemStorage: Storage {
	var DIRECTORY = System.getProperty("user.home") + "/.plex-api/"

	override fun get(key: String): String? {
		val file = File(DIRECTORY + key)
		if(!file.exists()) return null
		return file.readText()
	}

	override fun set(key: String, value: String?) {
		val storageDirectory = File(DIRECTORY)
		if(!storageDirectory.exists()){
			storageDirectory.mkdir()
		}
		val file = File(DIRECTORY + key)
		if(value == null && file.exists()){
			file.delete()
		} else if(value != null) {
			file.writeText(value)
		}
	}

}
