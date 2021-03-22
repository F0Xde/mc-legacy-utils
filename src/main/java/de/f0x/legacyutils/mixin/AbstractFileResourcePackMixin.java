package de.f0x.legacyutils.mixin;

import net.minecraft.resource.AbstractFileResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.awt.image.BufferedImage;

@Mixin(AbstractFileResourcePack.class)
public class AbstractFileResourcePackMixin {
    private static final int MAX_SIZE = 64;

    @Inject(method = "method_4367", at = @At("RETURN"), cancellable = true)
    void scaleIcon(CallbackInfoReturnable<BufferedImage> cir) {
        BufferedImage original = cir.getReturnValue();
        if (original.getWidth() > MAX_SIZE) {
            BufferedImage scaled = new BufferedImage(MAX_SIZE, MAX_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = original.getGraphics();
            graphics.drawImage(scaled, 0, 0, MAX_SIZE, MAX_SIZE, null);
            graphics.dispose();
            cir.setReturnValue(scaled);
        }
    }
}
