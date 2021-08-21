package de.f0x.legacyutils.mixin;

import de.f0x.legacyutils.ChatKt;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow @Final private static Logger LOGGER;

    @Redirect(
        method = "onChatMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"
        )
    )
    void addMessage(ChatHud chat, Text message) {
        String string = message.getString();
        if (ChatKt.shouldHide(string)) {
            LOGGER.info("Join message '{}' hidden", string);
        } else {
            chat.addMessage(message);

        }
    }
}
