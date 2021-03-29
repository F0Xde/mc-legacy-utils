package de.f0x.legacyutils.mixin.gui;

import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.ResourcePackLoader;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Mixin(ResourcePackScreen.class)
public abstract class ResourcePackScreenMixin extends Screen {
    @Redirect(
        method = "init",
        at = @At(value = "INVOKE", target = "Ljava/util/List;removeAll(Ljava/util/Collection;)Z")
    )
    boolean sortResourcePacks(List<ResourcePackLoader.Entry> packs, Collection<ResourcePackLoader.Entry> selectedPacks) {
        boolean changed = packs.removeAll(selectedPacks);
        packs.sort(Comparator.comparing(entry -> Formatting.strip(entry.getName())));
        return changed;
    }
}
