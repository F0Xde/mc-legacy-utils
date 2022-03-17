package de.f0x.legacyutils.command

import de.f0x.legacyutils.util.send
import net.minecraft.entity.player.ClientPlayerEntity
import net.minecraft.text.Text

class LegacyCommandSource(val player: ClientPlayerEntity) {
    fun send(text: String, prefixed: Boolean = true) {
        player.send(text, prefixed)
    }

    fun send(text: Text, prefixed: Boolean = true) {
        player.send(text, prefixed)
    }
}
