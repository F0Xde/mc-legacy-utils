package de.f0x.legacyutils.mixin.gui.screen;

import de.f0x.legacyutils.command.CommandSuggester;
import de.f0x.legacyutils.command.LegacyCommandManager;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Shadow
    protected TextFieldWidget chatField;
    private CommandSuggester suggester;

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        suggester = new CommandSuggester(client, chatField);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (chatField.getText().startsWith(LegacyCommandManager.PREFIX)
            && suggester.keyPressed(typedChar, keyCode)) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderSuggestions(CallbackInfo ci) {
        if (chatField.getText().startsWith(LegacyCommandManager.PREFIX)) {
            suggester.render();
        }
    }
}
