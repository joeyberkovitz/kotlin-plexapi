val ktor_version: String by project

plugins {
	val kotlinVersion = "1.7.20"
	kotlin("multiplatform") version kotlinVersion
	//kotlin("jvm") version kotlinVersion
	kotlin("plugin.serialization") version kotlinVersion
	id("maven-publish")
}

group = "us.berkovitz"
version = "1.0-SNAPSHOT"

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
				implementation("io.ktor:ktor-client-core:$ktor_version")
				implementation("io.ktor:ktor-client-cio:$ktor_version")
				implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
				implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
				implementation("io.ktor:ktor-serialization-kotlinx-xml:$ktor_version")
			}
		}
		val jvmTest by getting {
			dependencies {
				implementation(kotlin("test"))
				implementation("io.ktor:ktor-client-mock:$ktor_version")
			}
		}
	}
}
