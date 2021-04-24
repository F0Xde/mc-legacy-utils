package de.f0x.legacyutils.config

import de.f0x.legacyutils.Color
import de.f0x.legacyutils.CrosshairType
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

        val width by int(2)
        val height by int(2)
        val gap by int(0)
        val thickness by int(1)
    }
}

val ConfigManager = ConfigHolder(Config, Paths.get("legacyutils.json"))
