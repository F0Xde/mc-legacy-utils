package de.f0x.legacyutils

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
data class Config(
    val staticFov: Boolean = false,
    val fullBright: Boolean = false,
    val noAchievementNotification: Boolean = false,
    val noPumpkinBlur: Boolean = false
)

object ConfigManager {
    private val path: Path = Paths.get("legacyutils.json")

    var config = Config()
        private set

    @Synchronized
    fun load() {
        if (path.exists()) {
            try {
                config = Json.decodeFromString(path.readText())
            } catch (e: Exception) {
                Log.error("Error loading config, replacing with default", e)
                save()
            }
        } else {
            Log.info("Config does not exist, saving default")
            save()
        }
    }

    @Synchronized
    fun save() {
        try {
            path.parent?.createDirectories()
            path.writeText(PrettyJson.encodeToString(config))
        } catch (e: IOException) {
            Log.error("Error saving config", e)
        }
    }
}
