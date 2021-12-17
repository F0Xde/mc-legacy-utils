package de.f0x.legacyutils.mixin.render;

import de.f0x.legacyutils.config.Config;
import de.f0x.legacyutils.config.ConfigItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.f0x.legacyutils.config.ConfigKt.getConfigManager;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> extends EntityRenderer<T> {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final ConfigItem<Boolean> ownNameTag = getConfigManager().get(Config.INSTANCE.getOwnNameTag());

    protected LivingEntityRendererMixin(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    void hasOwnLabel(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (ownNameTag.get() && entity.equals(client.getCameraEntity())) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
