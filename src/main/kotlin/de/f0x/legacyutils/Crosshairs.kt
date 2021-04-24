package de.f0x.legacyutils

import com.mojang.blaze3d.platform.GlStateManager
import de.f0x.legacyutils.config.Config.Crosshair
import de.f0x.legacyutils.config.ConfigManager
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.Window

enum class CrosshairType {
    VANILLA,
    CROSS
}

class CrosshairRenderer : DrawableHelper() {
    private val type by ConfigManager[Crosshair.type]
    private val color by ConfigManager[Crosshair.color]

    private val width by ConfigManager[Crosshair.width]
    private val height by ConfigManager[Crosshair.height]
    private val gap by ConfigManager[Crosshair.gap]
    private val thickness by ConfigManager[Crosshair.thickness]

    fun render(window: Window) {
        if (type == CrosshairType.CROSS) {
            val color = color.value

            val xVert = (window.scaledWidth - thickness) / 2
            val yVert = (window.scaledHeight - gap) / 2
            fill(xVert, yVert - height, xVert + thickness, yVert, color)
            fill(xVert, yVert + gap, xVert + thickness, yVert + gap + height, color)

            val xHor = (window.scaledWidth - gap) / 2
            val yHor = (window.scaledHeight - thickness) / 2
            fill(xHor - width, yHor, xHor, yHor + thickness, color)
            fill(xHor + gap, yHor, xHor + gap + width, yHor + thickness, color)
            GlStateManager.color3f(1f, 1f, 1f)
        }
    }
}
