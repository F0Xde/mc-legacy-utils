package de.f0x.legacyutils

import de.f0x.legacyutils.command.literal
import de.f0x.legacyutils.command.register
import de.f0x.legacyutils.config.ConfigManagerOld
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

const val CHAT_PREFIX = "§5Legacy Utils §8| §r"

val Log: Logger = LogManager.getLogger("legacyutils")
val PrettyJson = Json { prettyPrint = true }

fun init() {
    ConfigManagerOld.load()

    literal("test1").executes {
        it.source.send("Yoo")
        1
    }.register()
}
