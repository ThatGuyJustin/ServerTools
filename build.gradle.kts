import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion

plugins {
    java
    idea
    `maven-publish`
    `java-library`
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("net.neoforged.moddev") version "2.0.140"
}

fun DependencyHandlerScope.implCollection(vararg implementations: String) {
    for (impl in implementations) { implementation(impl) }
}

fun DependencyHandlerScope.compileCollection(vararg implementations: String) {
    for (impl in implementations) { compileOnly(impl) }
}

val minecraftVersion: String by project
val minecraftVersionRange: String by project
val neoVersion: String by project
val neoVersionRange: String by project
val loaderName: String by project
val loaderVersionRange: String by project
val modId: String by project
val modName: String by project
val modLicense: String by project
val modVersion: String by project
val modGroupId: String by project
val modAuthors: String by project
val modDescription: String by project
val jacksonVersion: String by project
val parchmentMappingsVersion: String by project
val parchmentMinecraftVersion: String by project
val kotlinSerializationVersion: String by project
val jdaVersion: String by project
val slf4jVersion: String by project
val jdaKTXVersion: String by project
val trove4jVersion: String by project
val discordWebhookVersion: String by project
val tinkVersion: String by project
val adventurePlatformVersion: String by project
val admiralVersion: String by project
val admiralDepVersion: String = "$admiralVersion+$minecraftVersion+neoforge"
val okhttpVersion: String by project
val okioVersion: String by project

version = modVersion
group = modGroupId

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://maven.neoforged.net/releases")

    maven("https://api.modrinth.com/maven") {
        content {
            includeGroup("maven.modrinth")
        }
    }
}

base {
    archivesName = modId
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

neoForge {
    // We currently only support NeoForge versions later than 21.0.x
    // See https://projects.neoforged.net/neoforged/neoforge for the latest updates
    version = neoVersion

    // Validate AT files and raise errors when they have invalid targets
    // This option is false by default, but turning it on is recommended
    validateAccessTransformers = true

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }

    runs {
        create("server") {
            server()
//            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("data") {
            data()
            programArguments.addAll(listOf(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            ))
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }

        mods {
            create(modId) {
                sourceSet(sourceSets.main.get())
            }
        }
    }
}

sourceSets {
    named("main") {
        resources.srcDir("build/generated/resources")
    }
}

configurations.all {
    resolutionStrategy {
        force("org.slf4j:slf4j-api:$slf4jVersion")
    }
}

dependencies {
    implCollection(
        "net.neoforged:neoforge:$neoVersion",
        "org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinSerializationVersion",
        "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion",
        "net.dv8tion:JDA:$jdaVersion",
        "club.minnced:jda-ktx:$jdaKTXVersion",
        "com.fasterxml.jackson.core:jackson-core:$jacksonVersion",
        "com.fasterxml.jackson.core:jackson-databind:$jacksonVersion",
        "com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion",
        "net.sf.trove4j:trove4j:$trove4jVersion",
        "com.google.crypto.tink:tink:$tinkVersion",
        "net.kyori:adventure-platform-neoforge:$adventurePlatformVersion",
        "com.squareup.okhttp3:okhttp:$okhttpVersion",
        "com.squareup.okio:okio:$okioVersion",
        "maven.modrinth:admiral:$admiralDepVersion"
    )

    // Fucking hate slf4j
    implementation("club.minnced:discord-webhooks:$discordWebhookVersion") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    jarJar(implementation("net.kyori", "adventure-platform-neoforge", adventurePlatformVersion))
    jarJar(implementation("org.jetbrains.kotlin", "kotlin-stdlib", kotlinToolingVersion.toString()))
    jarJar(implementation("com.squareup.okio", "okio", okioVersion))
    jarJar(implementation("com.squareup.okhttp3", "okhttp", okhttpVersion))
    jarJar(implementation("com.fasterxml.jackson.core", "jackson-core", jacksonVersion))
    jarJar(implementation("com.fasterxml.jackson.core", "jackson-databind", jacksonVersion))
    jarJar(implementation("com.fasterxml.jackson.core", "jackson-annotations", jacksonVersion))
    jarJar(implementation("net.sf.trove4j", "trove4j", trove4jVersion))
    jarJar(implementation("com.google.crypto.tink", "tink", tinkVersion))
    jarJar(implementation("net.dv8tion", "JDA", jdaVersion))
    jarJar(implementation("club.minnced", "jda-ktx", jdaKTXVersion))
    jarJar(implementation("club.minnced", "discord-webhooks", discordWebhookVersion))
    jarJar(implementation("maven.modrinth", "admiral", admiralDepVersion))
}

tasks.processResources {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraftVersion,
        "minecraft_version_range" to minecraftVersionRange,
        "neo_version" to neoVersion,
        "neo_version_range" to neoVersionRange,
        "loader_name" to loaderName,
        "loader_version_range" to loaderVersionRange,
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_license" to modLicense,
        "mod_version" to modVersion,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription
    )
    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        javaParameters.set(true)
    }
}