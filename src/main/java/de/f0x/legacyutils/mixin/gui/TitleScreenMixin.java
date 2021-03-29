package de.f0x.legacyutils.mixin.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Redirect(
        method = "initWidgetsNormal",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/TitleScreen;realmsButton:Lnet/minecraft/client/gui/widget/ButtonWidget;"
        )
    )
    void initRealmsButton(TitleScreen screen, ButtonWidget button) {
    }

    @Redirect(
        method = "initWidgetsNormal",
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/gui/screen/TitleScreen;realmsButton:Lnet/minecraft/client/gui/widget/ButtonWidget;"
            )
        ),
        at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0)
    )
    boolean addRealmsButton(List<?> list, Object e) {
        return false;
    }

    /**
     * @author _F0X
     * @reason Fully disable Realms
     */
    @Overwrite
    private boolean areRealmsNotificationsEnabled() {
        return false;
    }
}
