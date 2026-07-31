pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		mavenCentral()
		gradlePluginPortal()
	}

	plugins {
		id("net.fabricmc.fabric-loom") version providers.gradleProperty("loom_version")
		id("co.uzzu.dotenv.gradle") version "4.0.0"
	}
}

// Should match your modid
rootProject.name = "permanent_paintings"