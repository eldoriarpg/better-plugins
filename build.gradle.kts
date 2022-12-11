plugins {
    java
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.eldoria"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-proxies/")
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishData {
    useEldoNexusRepos()
    publishComponent("java")
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    implementation("de.eldoria", "eldo-util", "1.14.0-DEV")
    bukkitLibrary("net.kyori", "adventure-platform-bukkit", "4.1.2")
    bukkitLibrary("net.kyori", "adventure-text-minimessage", "4.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    shadowJar {
        val shadebase = "de.eldoria.betterplugins."
        relocate("de.eldoria.eldoutilities", shadebase + "eldoutilities")
        mergeServiceFiles()
        archiveFileName.set("betterplugins.jar")
    }

    register<Copy>("copyToServer") {
        val path = project.property("targetDir") ?: "";
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }

    build {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
    }
}

bukkit {
    name = "BetterPlugins"
    main = "de.eldoria.betterplugins.BetterPlugins"
    website = ""
    apiVersion = "1.19"
    version = rootProject.version.toString()
    authors = listOf("RainbowdashLabs")
    commands {
        register("betterplugins") {
            aliases = listOf("plugins", "bp")
        }
    }
}
