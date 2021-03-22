rootProject.name = "fabric-example-mod"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://jitpack.io")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "fabric-loom" && requested.version?.endsWith("-SNAPSHOT") != true) {
                useModule("com.github.Chocohead.Fabric-Loom:fabric-loom:${requested.version}")
            }
        }
    }
}
