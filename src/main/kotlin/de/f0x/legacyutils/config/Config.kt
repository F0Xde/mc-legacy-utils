package de.f0x.legacyutils.config

import java.nio.file.Paths

object Config : ConfigDecl() {
    val staticFov by bool()
    val fullBright by bool()
    val noAchievementNotification by bool()
    val noPumpkinBlur by bool()
}

val ConfigManager = ConfigHolder(Config, Paths.get("legacyutils.json"))
