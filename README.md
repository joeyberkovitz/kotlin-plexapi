# kotlin-plexapi

A Kotlin library for interacting with the Plex Media Server API.

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/joeyberkovitz/kotlin-plexapi")
        credentials {
            username = "your-github-username"
            password = "your-github-token"
        }
    }
}

dependencies {
    implementation("us.berkovitz:kotlin-plexapi:0.2.0")
}
```

## Features

### Authentication
- `MyPlexAccount` - Authenticate with Plex account
- `MyPlexPinLogin` - PIN-based authentication flow

### Playlists
- `PlexServer.playlists()` - Get all playlists
- `Playlist.items()` - Get tracks in a playlist

### Library Browsing (v0.2.0)
- `PlexServer.librarySections()` - Get all library sections
- `PlexServer.musicSection()` - Find the music library section
- `PlexServer.artists(sectionId)` - Get all artists
- `PlexServer.albums(sectionId)` - Get all albums
- `PlexServer.tracks(sectionId)` - Get all tracks
- `PlexServer.artistAlbums(ratingKey)` - Get albums for an artist
- `PlexServer.albumTracks(ratingKey)` - Get tracks for an album

## Usage

### Connect to a Plex Server

```kotlin
val account = MyPlexAccount(token)
val server = PlexServer(baseUrl, token)
```

### Browse Music Library

```kotlin
// Find music library section
val musicSection = server.musicSection()

// Get all artists
val artists = server.artists(musicSection.key)

// Get albums for an artist
val albums = server.artistAlbums(artist.ratingKey)

// Get tracks for an album
val tracks = server.albumTracks(album.ratingKey)
```

### Get Playlists

```kotlin
val playlists = server.playlists(PlaylistType.AUDIO)
for (playlist in playlists) {
    val tracks = playlist.items()
}
```

## Data Classes

| Class | Description |
|-------|-------------|
| `PlexServer` | Represents a Plex Media Server connection |
| `LibrarySection` | A library section (Music, Movies, etc.) |
| `Artist` | A music artist |
| `Album` | A music album |
| `Track` | A music track |
| `Playlist` | A playlist |
| `Media` | Media file information |
| `Part` | File part information |

## Changelog

### v0.2.0
- Added `LibrarySection` data class for library browsing
- Added `Artist` data class with `albums()` and `tracks()` methods
- Added `Album` data class with `tracks()` and `artist()` methods
- Added `Tag` data class for genres, countries, styles, moods
- Added `PlexServer` methods for music library browsing
- Fixed polymorphic serialization conflict between `Artist` and `Album` (both use `@SerialName("Directory")`)

### v0.1.17
- Added `originalTitle` field support

## Credits

Thanks to:
* https://github.com/jrudio/go-plex-client
* https://github.com/pkkid/python-plexapi
* https://github.com/Arcanemagus/plex-api
