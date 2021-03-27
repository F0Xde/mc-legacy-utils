package de.f0x.legacyutils.mixin;

import de.f0x.legacyutils.LegacyClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private MinecraftClient client;
    @Shadow
    private float lastMovementFovMultiplier;
    @Shadow
    private float movementFovMultiplier;

    @Redirect(
        method = "method_3357",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;lastMovementFovMultiplier:F")
    )
    float getLastFovModifier(GameRenderer renderer) {
        return ((LegacyClient) client).getConfig().getDynamicFov() ? lastMovementFovMultiplier : 1;
    }

    @Redirect(
        method = "method_3357",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;movementFovMultiplier:F")
    )
    float getFovModifier(GameRenderer renderer) {
        return ((LegacyClient) client).getConfig().getDynamicFov() ? movementFovMultiplier : 1;
    }
}
