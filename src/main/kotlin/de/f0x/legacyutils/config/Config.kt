package de.f0x.legacyutils.config

import de.f0x.legacyutils.Color
import de.f0x.legacyutils.CrosshairType
import net.minecraft.client.options.GameOptions
import net.minecraft.client.options.KeyBinding
import org.lwjgl.input.Keyboard
import java.nio.file.Paths

object Config : ConfigDecl() {
    val staticFov by bool()
    val fullBright by bool()
    val noAchievementNotification by bool()
    val noPumpkinBlur by bool()
    val ownNameTag by bool()

    object Crosshair : ConfigDecl() {
        val type by config(CrosshairType.VANILLA)
        val color by config(Color())

        val width by uint(1)
        val height by uint(1)
        val gap by uint(1)
        val thickness by uint(1)
    }

    object Zoom : ConfigDecl() {
        val fovDivisor by float(4f) { min = 1 / Float.MAX_VALUE }
    }
}

val ConfigManager = ConfigHolder(Config, Paths.get("legacyutils.json"))

object KeyBindings {
    private val bindings = mutableListOf<KeyBinding>()

    val zoom = key("zoom", Keyboard.KEY_C)

    fun register(options: GameOptions) {
        options.keysAll += bindings
    }

    private fun key(
        name: String,
        code: Int,
        category: String = "key.categories.legacyutils"
    ) = KeyBinding("key.legacyutils.$name", code, category).also { bindings.add(it) }
}
