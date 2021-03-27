package de.f0x.legacyutils.command

import de.f0x.legacyutils.send
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text

class LegacyCommandSource(val player: ClientPlayerEntity) {
    fun send(text: String, prefixed: Boolean = true) {
        player.send(text, prefixed)
    }

    fun send(text: Text, prefixed: Boolean = true) {
        player.send(text, prefixed)
    }
}
