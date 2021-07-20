package de.f0x.legacyutils

import de.f0x.legacyutils.command.registerCommands
import de.f0x.legacyutils.config.ConfigManager
import kotlinx.serialization.json.Json
import net.legacyfabric.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

const val CHAT_PREFIX = "§5Legacy Utils §8» §r"

val Log: Logger = LogManager.getLogger("legacyutils")
val PrettyJson = Json { prettyPrint = true }

fun init() {
    ConfigManager.load()
    ConfigManager.registerCommands()
}
