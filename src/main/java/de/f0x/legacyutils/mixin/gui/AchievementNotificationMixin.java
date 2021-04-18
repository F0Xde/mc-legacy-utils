package de.f0x.legacyutils.mixin.gui;

import de.f0x.legacyutils.config.ConfigManager;
import net.minecraft.client.gui.AchievementNotification;
import net.minecraft.client.gui.DrawableHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AchievementNotification.class)
public abstract class AchievementNotificationMixin extends DrawableHelper {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void onTick(CallbackInfo ci) {
        if (ConfigManager.INSTANCE.getConfig().getNoAchievementNotification()) {
            ci.cancel();
        }
    }
}
