import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.function.Function

plugins {
    kotlin("jvm") version "1.4.31"
    `maven-publish`
    id("fabric-loom") version "0.6-SNAPSHOT"
}

version = "0.1.0"
group = "de.f0x"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven")
}

minecraft {
    intermediaryUrl = Function {
        "https://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven/net/fabricmc/intermediary/$it/intermediary-$it-v2.jar"
    }
}

val loaderDep = "net.fabricmc:fabric-loader-1.8.9:0.9.3+build.202009100647"

dependencies {
    implementation("com.google.guava:guava:23.5-jre")
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("net.fabricmc:yarn:1.8.9+build.202102062228:v2")
    modImplementation(loaderDep) {
        exclude(module = "guava")
    }
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.10.3")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.5.0+kotlin.1.4.31")

    if (isMac) {
        implementation("org.lwjgl.lwjgl:lwjgl_util:2.9.4-nightly-20150209")
        implementation("org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209")
        implementation("org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209")
    }
}

configurations.all {
    resolutionStrategy {
        dependencySubstitution {
            substitute(module("net.fabricmc:fabric-loader")).with(module(loaderDep))
            if (isMac) {
                substitute(module("org.lwjgl.lwjgl:lwjgl_util:2.9.2-nightly-201408222")).with(module("org.lwjgl.lwjgl:lwjgl_util:2.9.4-nightly-20150209"))
                substitute(module("org.lwjgl.lwjgl:lwjgl:2.9.2-nightly-201408222")).with(module("org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209"))
            }
        }
        if (isMac) {
            force("org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209")
        }
    }
}

val javaVersion = JavaVersion.VERSION_1_8

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    withSourcesJar()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${rootProject.name}" }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.remapJar)
            artifact(tasks.remapSourcesJar)
        }
    }
}

val isMac: Boolean
    get() = System.getProperty("os.name").contains("mac", ignoreCase = true)
