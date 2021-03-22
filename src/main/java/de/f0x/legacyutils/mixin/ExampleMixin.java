package de.f0x.legacyutils.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class ExampleMixin extends Screen {
    @Inject(at = @At("HEAD"), method = "init()V")
    void init(CallbackInfo info) {
        System.out.println("This line is printed by an example mod mixin!");
    }
}
