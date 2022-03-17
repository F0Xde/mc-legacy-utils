package de.f0x.legacyutils

import de.f0x.legacyutils.config.Config.Hypixel
import de.f0x.legacyutils.config.ConfigManager

private val hideJoins by ConfigManager[Hypixel.hideJoins]
private val joinRegex = Regex(
    """(§b\[MVP§.\+§b] \S+§f §6(joined|slid into) the lobby!| §b>§c>§a>§r §.\[MVP§.\+\+§.] \S+§f §6(joined|slid into) the lobby! §a<§c<§b<)"""
)

fun String.shouldHide() = hideJoins && matches(joinRegex)
