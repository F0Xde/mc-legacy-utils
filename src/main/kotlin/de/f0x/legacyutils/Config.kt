package de.f0x.legacyutils

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
data class Config(val dynamicFov: Boolean)

class ConfigManager(private val path: Path = Paths.get("legacyutils.json")) {
    var config = Config(dynamicFov = true)
        private set

    @Synchronized
    fun load() {
        if (!path.exists()) {
            save()
        } else {
            config = Json.decodeFromString(path.readText())
        }
    }

    @Synchronized
    fun save() {
        path.parent?.createDirectories()
        path.writeText(PrettyJson.encodeToString(config))
    }
}
