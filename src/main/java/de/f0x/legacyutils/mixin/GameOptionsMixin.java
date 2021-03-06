package de.f0x.legacyutils.mixin;

import de.f0x.legacyutils.config.KeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Inject(
        method = "<init>(Lnet/minecraft/client/MinecraftClient;Ljava/io/File;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/options/GameOptions;keysAll:[Lnet/minecraft/client/options/KeyBinding;",
            shift = At.Shift.AFTER
        )
    )
    void registerBindings(MinecraftClient client, File optionsDir, CallbackInfo ci) {
        KeyBindings.INSTANCE.register((GameOptions) (Object) this);
    }

    @Redirect(
        method = "<init>()V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;realmsNotifications:Z")
    )
    void initRealmsNotifications(GameOptions options, boolean value) {
        options.realmsNotifications = false;
    }

    @Redirect(
        method = "save",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;realmsNotifications:Z")
    )
    boolean getRealmsNotifications(GameOptions options) {
        return false;
    }

    @Redirect(
        method = "load",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;realmsNotifications:Z")
    )
    void setRealmsNotifications(GameOptions options, boolean value) {
        options.realmsNotifications = false;
    }

    @Inject(method = "getIntVideoOptions", at = @At("HEAD"), cancellable = true)
    void getOptionRealms(GameOptions.Option option, CallbackInfoReturnable<Boolean> cir) {
        if (option == GameOptions.Option.REALMS_NOTIFICATIONS) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "getBooleanValue", at = @At("HEAD"), cancellable = true)
    void setOptionRealms(GameOptions.Option option, int value, CallbackInfo ci) {
        if (option == GameOptions.Option.REALMS_NOTIFICATIONS) {
            ci.cancel();
        }
    }
}
