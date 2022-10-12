package us.berkovitz.plexapi.storage

interface Storage {
	fun get(key: String): String?
	fun set(key: String, value: String?)

	companion object {
		@Volatile private var IMPL: Storage = FilesystemStorage
		fun getStorage(): Storage {
			return IMPL
		}
	}
}
