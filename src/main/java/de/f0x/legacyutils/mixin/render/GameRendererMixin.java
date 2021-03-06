package de.f0x.legacyutils.mixin.render;

import de.f0x.legacyutils.config.Config;
import de.f0x.legacyutils.config.ConfigItem;
import de.f0x.legacyutils.config.KeyBindings;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static de.f0x.legacyutils.config.ConfigKt.getConfigManager;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private float lastMovementFovMultiplier;
    @Shadow
    private float movementFovMultiplier;

    private final ConfigItem<Float> zoomFovDivisor = getConfigManager().get(Config.Zoom.INSTANCE.getFovDivisor());
    private final ConfigItem<Boolean> staticFov = getConfigManager().get(Config.INSTANCE.getStaticFov());
    private final ConfigItem<Boolean> fullBright = getConfigManager().get(Config.INSTANCE.getFullBright());

    @Redirect(method = "getFov", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;fov:F"))
    float getFovOption(GameOptions options) {
        return options.fov / (KeyBindings.INSTANCE.getZoom().isPressed() ? zoomFovDivisor.get() : 1);
    }

    @Redirect(
            method = "getFov",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;lastMovementFovMultiplier:F")
    )
    float getLastFovModifier(GameRenderer renderer) {
        return staticFov.get() ? 1 : lastMovementFovMultiplier;
    }

    @Redirect(
            method = "getFov",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;movementFovMultiplier:F")
    )
    float getFovModifier(GameRenderer renderer) {
        return staticFov.get() ? 1 : movementFovMultiplier;
    }

    @Redirect(
            method = {"render", "tick"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;sensitivity:F")
    )
    float getSensitivity(GameOptions options) {
        return options.sensitivity / (KeyBindings.INSTANCE.getZoom().isPressed() ? zoomFovDivisor.get() : 1);
    }

    @Redirect(
            method = "updateLightmap",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;gamma:F")
    )
    float getGamma(GameOptions options) {
        return fullBright.get() ? 100 : options.gamma;
    }
}
