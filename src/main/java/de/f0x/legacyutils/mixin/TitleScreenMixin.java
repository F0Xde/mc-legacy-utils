package de.f0x.legacyutils.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
}
