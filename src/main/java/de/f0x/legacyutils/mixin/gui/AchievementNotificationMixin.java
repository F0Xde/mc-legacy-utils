package de.f0x.legacyutils.mixin.gui;

import de.f0x.legacyutils.config.Config;
import de.f0x.legacyutils.config.ConfigItem;
import net.minecraft.client.gui.AchievementNotification;
import net.minecraft.client.gui.DrawableHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.f0x.legacyutils.config.ConfigKt.getConfigManager;

@Mixin(AchievementNotification.class)
public abstract class AchievementNotificationMixin extends DrawableHelper {
    private final ConfigItem<Boolean> noAchievementNotification =
        getConfigManager().get(Config.INSTANCE.getNoAchievementNotification());

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void onTick(CallbackInfo ci) {
        if (noAchievementNotification.get()) {
            ci.cancel();
        }
    }
}
