package de.f0x.legacyutils.mixin.render;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    public float field_4824;

    @Shadow
    public GameOptions field_4825;

    @Inject(method = "method_3887", at = @At("RETURN"))
    void adjustViewPitch(CallbackInfo ci) {
        if (field_4825.perspective == 2) {
            field_4824 = -field_4824;
        }
    }
}
