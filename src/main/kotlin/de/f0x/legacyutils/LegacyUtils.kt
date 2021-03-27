package de.f0x.legacyutils

import de.f0x.legacyutils.command.literal
import de.f0x.legacyutils.command.register
import kotlinx.serialization.json.Json

const val CHAT_PREFIX = "§5Legacy Utils §8| §r"

val PrettyJson = Json { prettyPrint = true }

fun init() {
    ConfigManager.load()

    literal("test1").executes {
        it.source.send("Yoo")
        1
    }.register()
}
