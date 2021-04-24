package de.f0x.legacyutils

import kotlinx.serialization.Serializable

@Serializable
data class Color(
    val rgb: Int = 0xFFFFFF,
    val alpha: Int = 0xFF,
    val isRainbow: Boolean = false
) {
    val value: Int
        get() = (if (isRainbow) rainbow() else rgb) or ((alpha and 0xFF) shl 24)

    @ExperimentalUnsignedTypes
    override fun toString() =
        "${if (isRainbow) "rainbow" else '#' + rgb.toUInt().toString(16)}, alpha = ${alpha.toUByte()}"
}

fun rainbow() = java.awt.Color.HSBtoRGB((System.nanoTime() % 4_000_000_000) / 4_000_000_000f, 1f, 1f)
