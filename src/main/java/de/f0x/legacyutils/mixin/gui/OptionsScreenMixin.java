package de.f0x.legacyutils.mixin.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

@Mixin(SettingsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    @Redirect(
        method = "init",
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/options/GameOptions$Option;REALMS_NOTIFICATIONS:Lnet/minecraft/client/options/GameOptions$Option;"
            )
        ),
        at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0)
    )
    boolean addRealmsButton(List<?> list, Object button) {
        return false;
    }
}
