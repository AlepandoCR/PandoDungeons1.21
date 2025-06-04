plugins {
    kotlin("jvm") version "2.1.21"
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("xyz.jpenilla.run-paper") version "2.3.1"

    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "PandoDungeons"
version = "1.0"


repositories {
    mavenCentral()

    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.citizensnpcs.co/repo") {
        name = "citizens-repo"
    }
    maven("https://repo.opencollab.dev/main/") {
        name = "opencollab-snapshot"
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52"))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.4.0-SNAPSHOT")
    compileOnly("io.github.toxicity188:BetterModel:1.6.0")
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")
    compileOnly("org.geysermc.geyser:api:2.4.2-SNAPSHOT")
    compileOnly("net.citizensnpcs:citizensapi:2.0.35-SNAPSHOT")
    implementation(kotlin("stdlib"))

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
    runServer {
        minecraftVersion("1.21.5")
    }
}
