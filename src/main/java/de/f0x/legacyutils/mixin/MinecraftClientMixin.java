package de.f0x.legacyutils.mixin;

import de.f0x.legacyutils.Config;
import de.f0x.legacyutils.ConfigManager;
import de.f0x.legacyutils.LegacyClient;
import de.f0x.legacyutils.LegacyUtilsKt;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements LegacyClient {
    @Shadow
    @Final
    private static Logger LOGGER;

    private final ConfigManager configManager = new ConfigManager();

    @Inject(method = "initializeGame", at = @At("HEAD"))
    void init(CallbackInfo ci) {
        configManager.load();
        LegacyUtilsKt.init((MinecraftClient) (Object) this);
    }

    @Redirect(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    void gc() {
        LOGGER.info("Redirecting GC on world connect");
    }

    @NotNull
    @Override
    public Config getConfig() {
        return configManager.getConfig();
    }
}
