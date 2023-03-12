plugins {
    java
    `maven-publish`
    id("de.chojo.publishdata") version "1.2.4"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    id("com.github.johnrengelman.shadow") version "8.1.0"
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
    addBuildData()
    publishComponent("java")
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    implementation("de.eldoria", "eldo-util", "1.14.4")
    bukkitLibrary("net.kyori", "adventure-platform-bukkit", "4.2.0")
    bukkitLibrary("net.kyori", "adventure-text-minimessage", "4.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            name = "EldoNexus"
            url = uri(publishData.getRepository())
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        val shadebase = "de.eldoria.betterplugins."
        relocate("de.eldoria.eldoutilities", shadebase + "eldoutilities")
        mergeServiceFiles()
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
        testLogging {
            events("passed", "skipped", "failed")
        }
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
