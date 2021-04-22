package de.f0x.legacyutils.mixin.gui;

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
public abstract class IngameHudMixin extends DrawableHelper {
    @Shadow
    protected abstract void renderPumpkinBlur(Window window);

    private final ConfigItem<Boolean> noPumpkinBlur = getConfigManager().get(Config.INSTANCE.getNoPumpkinBlur());

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
}
