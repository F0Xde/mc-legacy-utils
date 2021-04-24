package de.f0x.legacyutils.mixin.gui;

import de.f0x.legacyutils.CrosshairRenderer;
import de.f0x.legacyutils.CrosshairType;
import de.f0x.legacyutils.config.Config;
import de.f0x.legacyutils.config.ConfigItem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static de.f0x.legacyutils.config.ConfigKt.getConfigManager;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    @Shadow
    protected abstract void renderPumpkinBlur(Window window);

    private final ConfigItem<Boolean> noPumpkinBlur = getConfigManager().get(Config.INSTANCE.getNoPumpkinBlur());
    private final ConfigItem<CrosshairType> crosshairType = getConfigManager().get(Config.Crosshair.INSTANCE.getType());

    private final CrosshairRenderer crosshairRenderer = new CrosshairRenderer();
    private Window window;

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/Window;getScaledWidth()I"
        )
    )
    int setWindow(Window window) {
        this.window = window;
        return window.getScaledWidth();
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderPumpkinBlur(Lnet/minecraft/client/util/Window;)V"
        )
    )
    void renderPumpkinBlur(InGameHud hud, Window window) {
        if (!noPumpkinBlur.get()) {
            renderPumpkinBlur(window);
        }
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(IIIIII)V"
        )
    )
    void renderCrosshair(InGameHud hud, int x, int y, int u, int v, int width, int height) {
        if (crosshairType.get() == CrosshairType.VANILLA) {
            hud.drawTexture(x, y, u, v, width, height);
        } else {
            crosshairRenderer.render(window);
        }
    }
}
