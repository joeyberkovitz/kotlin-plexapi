import java.io.FileInputStream
import java.util.*

val ktorVersion: String by project
val gpr_user: String? by project
val gpr_key: String? by project

plugins {
	val kotlinVersion = "1.7.20"
	kotlin("multiplatform") version kotlinVersion
	//kotlin("jvm") version kotlinVersion
	kotlin("plugin.serialization") version kotlinVersion
	id("maven-publish")
}

group = "us.berkovitz"
version = "0.1.3"

repositories {
	mavenCentral()
}

kotlin {
	jvm {
		compilations.all {
			kotlinOptions.jvmTarget = "1.8"
		}
		withJava()
		testRuns["test"].executionTask.configure {
			useJUnitPlatform()
		}
	}
	sourceSets {
		val jvmMain by getting {
			dependencies {
				implementation("io.github.microutils:kotlin-logging-jvm:3.0.0")
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
				implementation("io.ktor:ktor-client-core:$ktorVersion")
				implementation("io.ktor:ktor-client-cio:$ktorVersion")
				implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
				implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
				implementation("io.ktor:ktor-serialization-kotlinx-xml:$ktorVersion")
			}
		}
		val jvmTest by getting {
			dependencies {
				implementation(kotlin("test"))
				implementation("io.ktor:ktor-client-mock:$ktorVersion")
			}
		}
	}

	publishing {
		repositories {
			maven {
				name = "GitHubPackages"
				url = uri("https://maven.pkg.github.com/joeyberkovitz/kotlin-plexapi")
				credentials {
					val prop = try {
						Properties().apply {
							load(FileInputStream(File(rootProject.rootDir, "local.properties")))
						}
					} catch (exc: Exception) {
						null
					}
					username = prop?.getProperty("gpr_user") ?: System.getenv("GITHUB_ACTOR")
					password = prop?.getProperty("gpr_key") ?: System.getenv("GITHUB_TOKEN")
				}
			}
		}
	}
}
