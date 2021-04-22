package de.f0x.legacyutils.mixin.gui.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends HandledScreen {
    public InventoryScreenMixin(ScreenHandler screenHandler) {
        super(screenHandler);
    }

    @Inject(method = "applyStatusEffectOffset", at = @At("RETURN"))
    void removeInventoryOffset(CallbackInfo ci) {
        x = (width - backgroundWidth) / 2;
    }
}
