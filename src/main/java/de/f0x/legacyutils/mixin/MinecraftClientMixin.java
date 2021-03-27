package de.f0x.legacyutils.mixin;

import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Redirect(
        method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V",
        at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V")
    )
    void gc() {
        LOGGER.info("Redirecting GC on world connect");
    }
}
