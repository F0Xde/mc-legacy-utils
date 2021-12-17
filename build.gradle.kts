import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.function.Function

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    id("fabric-loom") version "0.7-SNAPSHOT"
    `maven-publish`
}

version = "0.3.0"
group = "de.f0x"

repositories {
    maven("https://maven.legacyfabric.net")
    mavenCentral()
    maven("https://libraries.minecraft.net")
}

minecraft {
    intermediaryUrl = Function {
        "https://maven.legacyfabric.net/net/fabricmc/intermediary/$it/intermediary-$it-v2.jar"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("net.fabricmc:yarn:1.8.9+build.202112162000:v2")
    modImplementation("net.fabricmc:fabric-loader:0.12.12")

    modImplementation("net.fabricmc:fabric-language-kotlin:1.7.0+kotlin.1.6.0")

    implInclude("com.mojang:brigadier:1.0.18")

    if (isMac) {
        implementation("org.lwjgl.lwjgl:lwjgl_util:2.9.4-nightly-20150209")
        implementation("org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209")
        implementation("org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209")
    }

    val kotestVersion = "5.0.1"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}

fun DependencyHandlerScope.implInclude(dependencyNotation: Any) {
    modImplementation(dependencyNotation)
    include(dependencyNotation)
}

if (isMac) {
    configurations.all {
        resolutionStrategy {
            dependencySubstitution {
                substitute(module("org.lwjgl.lwjgl:lwjgl_util:2.9.2-nightly-201408222")).using(module("org.lwjgl.lwjgl:lwjgl_util:2.9.4-nightly-20150209"))
                substitute(module("org.lwjgl.lwjgl:lwjgl:2.9.2-nightly-201408222")).using(module("org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209"))
            }
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

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        languageSettings.optIn("kotlin.io.path.ExperimentalPathApi")
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        if (JavaVersion.current().isJava9Compatible) {
            options.release.set(javaVersion.ordinal + 1)
        }
    }

    withType<Test> {
        useJUnitPlatform()
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
