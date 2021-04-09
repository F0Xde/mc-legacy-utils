package de.f0x.legacyutils.mixin;

import de.f0x.legacyutils.ConfigManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private float lastMovementFovMultiplier;
    @Shadow
    private float movementFovMultiplier;

    @Redirect(
        method = "getFov",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;lastMovementFovMultiplier:F")
    )
    float getLastFovModifier(GameRenderer renderer) {
        return ConfigManager.INSTANCE.getConfig().getStaticFov() ? 1 : lastMovementFovMultiplier;
    }

    @Redirect(
        method = "getFov",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;movementFovMultiplier:F")
    )
    float getFovModifier(GameRenderer renderer) {
        return ConfigManager.INSTANCE.getConfig().getStaticFov() ? 1 : movementFovMultiplier;
    }

    @Redirect(
        method = "updateLightmap",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;gamma:F")
    )
    float getGamma(GameOptions options) {
        return ConfigManager.INSTANCE.getConfig().getFullBright() ? 100 : options.gamma;
    }
}
