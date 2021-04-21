package de.f0x.legacyutils.mixin.gui;

import de.f0x.legacyutils.config.ConfigManagerOld;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class IngameHudMixin extends DrawableHelper {
    @Shadow
    protected abstract void renderPumpkinBlur(Window window);

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderPumpkinBlur(Lnet/minecraft/client/util/Window;)V"
        )
    )
    void renderPumpkinBlur(InGameHud hud, Window window) {
        if (!ConfigManagerOld.INSTANCE.getConfig().getNoPumpkinBlur()) {
            renderPumpkinBlur(window);
        }
    }
}
