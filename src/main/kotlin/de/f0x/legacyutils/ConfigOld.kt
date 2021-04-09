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
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Serializable
data class ConfigOld(
    val staticFov: Boolean = false,
    val fullBright: Boolean = false,
    val noAchievementNotification: Boolean = false,
    val noPumpkinBlur: Boolean = false
)

object ConfigManager {
    private val path: Path = Paths.get("legacyutils.json")

    var config = ConfigOld()
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

object Config {
    val staticFov by bool()
    val fullBright by bool()
    val noAchievementNotification by bool()
    val noPumpkinBlur by bool()

    private val root: ConfigObject = ConfigObject()

    private fun bool(default: Boolean = false) = ConfigValue(default).also { root.nodes["todo"] = it }

}

private sealed class ConfigNode

private class ConfigObject(val nodes: MutableMap<String, ConfigNode> = mutableMapOf()) : ConfigNode()

private class ConfigValue<T>(private val default: T) : ConfigNode(), ReadWriteProperty<Any?, T> {
    var value: T = default

    fun reset() {
        val x: Int by mapOf()
        value = default
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

private fun <T> config(default: T) = ConfigValue(default)

private fun bool(default: Boolean = false) = ConfigValue(default)
