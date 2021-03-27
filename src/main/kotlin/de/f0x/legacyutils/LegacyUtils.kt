package de.f0x.legacyutils

import de.f0x.legacyutils.command.literal
import de.f0x.legacyutils.command.register
import kotlinx.serialization.json.Json
import net.minecraft.client.MinecraftClient

const val CHAT_PREFIX = "§5Legacy Utils §8| §r"

val PrettyJson = Json { prettyPrint = true }

// haha look at my funny non-standard entrypoint with cool argument
fun init(client: MinecraftClient) {
    literal("test1").executes {
        it.source.send("Yoo")
        1
    }.register()
}
