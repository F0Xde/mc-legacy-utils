package de.f0x.legacyutils

import net.minecraft.client.MinecraftClient

interface LegacyClient {
    val config: Config
}

val MinecraftClient.config get() = (this as LegacyClient).config
