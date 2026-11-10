plugins {
	id("net.fabricmc.fabric-loom")
	`maven-publish`
	id("me.modmuss50.mod-publish-plugin") version "2.2.0"
	id("co.uzzu.dotenv.gradle") version "4.0.0"
}

version = "${property("mod_version")}+${property("deps.minecraft")}"
group = providers.gradleProperty("maven_group").get()

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
	maven {
		name = "Modrinth"
		url = uri("https://api.modrinth.com/maven")
		content {
			includeGroupAndSubgroups("maven.modrinth")
		}
	}
	maven {
		name = "Sisby Maven"
		url = uri("https://repo.sleeping.town/")
		content {
			includeGroupAndSubgroups("folk.sisby")
		}
	}
	maven {
		name = "Xander Maven"
		url = uri("https://maven.isxander.dev/releases")
		content {
			includeGroupAndSubgroups("dev.isxander")
			includeGroupAndSubgroups("org.quiltmc.parsers")
		}
	}
	maven {
		name = "Cassian's Maven"
		url = uri("https://maven.cassian.cc")
		content {
			includeGroupAndSubgroups("cc.cassian")
		}
	}
	maven {
		name = "NeoForge"
		url = uri("https://maven.neoforged.net/releases/")
		content {
			includeGroupAndSubgroups("net.neoforged")
			includeGroupAndSubgroups("cpw.mods")
			includeGroupAndSubgroups("net.minecraftforge")
		}
	}
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${providers.gradleProperty("deps.minecraft").get()}")

	implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("deps.fabric_api").get()}")
	implementation("cc.cassian.asphodel:asphodel-api-fabric:${property("deps.asphodel")}+${property("deps.minecraft")}"){
		exclude("net.fabricmc")
	}
	// Mod Menu
	localRuntime("maven.modrinth:modmenu:${property("deps.modmenu")}")

	// Configs
	implementation("folk.sisby:kaleido-config:${property("deps.kaleido")}")
	include("folk.sisby:kaleido-config:${property("deps.kaleido")}")

	// McQoy
	localRuntime("dev.isxander:yet-another-config-lib:${property("deps.yacl")}")
	localRuntime("maven.modrinth:mcqoy:${property("deps.mcqoy")}")

	// RRV
	compileOnly("cc.cassian.rrv:reliable-recipe-viewer-fabric:${property("deps.rrv")}+${property("deps.minecraft")}") {
		isTransitive = false
	}
	localRuntime("cc.cassian.rrv:reliable-recipe-viewer-fabric:${property("deps.rrv")}+${property("deps.minecraft")}") {
		isTransitive = false
	}

}

tasks.processResources {
	fun prop(name: String) = project.property(name) as String

	val props = HashMap<String, String>().apply {
		this["mod_version"] = prop("mod_version") + "+" + prop("deps.minecraft")
		this["mod_name"] = prop("mod_name")
		this["mod_license"] = prop("mod_license")
		this["mod_id"] = prop("mod_id")
		this["mod_description"] = prop("mod_description")

	}

	filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
		expand(props)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 25
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_${project.name}" }
	}
}

// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
	?.split(",")
	?.map { it.trim() }
	?.filter { it.isNotEmpty() }
	?: emptyList()


publishMods {
	file = tasks.jar.map { it.archiveFile.get() }

	// one of BETA, ALPHA, STABLE
	type = STABLE
	displayName = "${property("mod_name")} ${property("mod_version")} for ${property("deps.minecraft")}"
	version = "${property("mod_version")}+${property("deps.minecraft")}"
	changelog = provider { rootProject.file("CHANGELOG-LATEST.md").readText() }
	modLoaders.add("fabric")
	modLoaders.add("neoforge")

	modrinth {
		additionalFile(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar")) {
			type.set(SOURCES_JAR)
		}
		projectId = property("modrinth_id") as String
		accessToken = env.MODRINTH_API_KEY.orNull()
		minecraftVersions.add(property("deps.minecraft").toString())
		minecraftVersions.addAll(additionalVersions)
		requires("asphodel-api")
		optional("mcqoy")
		optional("rrv")
		environment = SERVER_ONLY_CLIENT_OPTIONAL
	}


	curseforge {
		additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })
		projectId = property("curseforge_id") as String
		accessToken = env.CURSEFORGE_API_KEY.orNull()
		minecraftVersions.add(property("deps.minecraft").toString())
		minecraftVersions.addAll(additionalVersions)
		requires("asphodel-api")
		optional("mcqoy")
		optional("rrv")
		client=true
		server=true
	}
}