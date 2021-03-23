package de.f0x.legacyutils.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Redirect(
        method = "method_3357",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;lastMovementFovMultiplier:F")
    )
    float redirectLastFovModifier(GameRenderer renderer) {
        return 1;
    }

    @Redirect(
        method = "method_3357",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;movementFovMultiplier:F")
    )
    float redirectFovModifier(GameRenderer renderer) {
        return 1;
    }
}
