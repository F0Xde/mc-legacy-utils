package de.f0x.legacyutils.mixin;

import de.f0x.legacyutils.util.DummyScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SurvivalInventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    public Profiler profiler;

    @Shadow
    public Screen currentScreen;

    @Shadow
    public ClientPlayerEntity player;

    @Shadow
    public InGameHud inGameHud;

    @Shadow
    public static long getTime() {
        return 0;
    }

    @Shadow
    long sysTime;

    @Shadow
    public boolean focused;

    @Shadow
    public abstract void closeScreen();

    @Shadow
    private int attackCooldown;

    @Shadow
    public GameOptions options;

    @Shadow
    public ClientPlayerInteractionManager interactionManager;

    @Shadow
    public abstract ClientPlayNetworkHandler getNetworkHandler();

    @Shadow
    public abstract void openScreen(Screen screen);

    @Shadow
    protected abstract void doAttack();

    @Shadow
    protected abstract void doUse();

    @Shadow
    protected abstract void doPick();

    @Shadow
    private int blockPlaceDelay;

    @Shadow
    protected abstract void handleBlockBreaking(boolean bl);

    @Shadow
    private long f3CTime;

    @Shadow
    public abstract void handleKeyInput();

    @Shadow
    public GameRenderer gameRenderer;

    @Shadow
    public abstract void openGameMenuScreen();

    @Shadow
    public abstract void stitchTextures();

    @Shadow
    public WorldRenderer worldRenderer;

    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow
    public abstract Entity getCameraEntity();

    @Shadow
    protected abstract void method_2292(int i);

    @Redirect(
        method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V",
        at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V")
    )
    void gc() {
        LOGGER.info("Redirecting GC on world connect");
    }

    @Inject(
        method = "runGameLoop",
        at = @At(
            value = "INVOKE_STRING",
            target = "Lnet/minecraft/client/MinecraftClient;setGlErrorMessage(Ljava/lang/String;)V",
            args = "ldc=Pre render",
            shift = At.Shift.AFTER
        )
    )
    void input(CallbackInfo ci) {
        profiler.swap("input");
        if (currentScreen != null) {
            currentScreen.handleInput();
        } else {
            this.profiler.push("mouse");

            int k;
            while (Mouse.next()) {
                k = Mouse.getEventButton();
                KeyBinding.setKeyPressed(k - 100, Mouse.getEventButtonState());
                if (Mouse.getEventButtonState()) {
                    if (this.player.isSpectator() && k == 2) {
                        this.inGameHud.getSpectatorHud().useSelectedCommand();
                    } else {
                        KeyBinding.onKeyPressed(k - 100);
                    }
                }

                long l = getTime() - this.sysTime;
                if (l <= 200L) {
                    int j = Mouse.getEventDWheel();
                    if (j != 0) {
                        if (this.player.isSpectator()) {
                            j = j < 0 ? -1 : 1;
                            if (this.inGameHud.getSpectatorHud().isOpen()) {
                                this.inGameHud.getSpectatorHud().method_2671(-j);
                            } else {
                                float f = MathHelper.clamp(this.player.abilities.getFlySpeed() + (float) j * 0.005F, 0.0F, 0.2F);
                                this.player.abilities.setFlySpeed(f);
                            }
                        } else {
                            this.player.inventory.method_7960(j);
                        }
                    }

                    if (this.currentScreen == null) {
                        if (!this.focused && Mouse.getEventButtonState()) {
                            this.closeScreen();
                        }
                    } else if (this.currentScreen != null) {
                        this.currentScreen.handleMouse();
                    }
                }
            }

            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }

            this.profiler.swap("keyboard");

            label504:
            while (true) {
                do {
                    do {
                        do {
                            if (!Keyboard.next()) {
                                for (k = 0; k < 9; ++k) {
                                    if (this.options.keysHotbar[k].wasPressed()) {
                                        if (this.player.isSpectator()) {
                                            this.inGameHud.getSpectatorHud().selectSlot(k);
                                        } else {
                                            this.player.inventory.selectedSlot = k;
                                        }
                                    }
                                }

                                boolean bl = this.options.chatVisibilityType != PlayerEntity.ChatVisibilityType.HIDDEN;

                                while (this.options.keyInventory.wasPressed()) {
                                    if (this.interactionManager.hasRidingInventory()) {
                                        this.player.openRidingInventory();
                                    } else {
                                        this.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.OPEN_INVENTORY_ACHIEVEMENT));
                                        this.openScreen(new SurvivalInventoryScreen(this.player));
                                    }
                                }

                                while (this.options.keyDrop.wasPressed()) {
                                    if (!this.player.isSpectator()) {
                                        this.player.dropSelectedItem(Screen.hasControlDown());
                                    }
                                }

                                while (this.options.keyChat.wasPressed() && bl) {
                                    this.openScreen(new ChatScreen());
                                }

                                if (this.currentScreen == null && this.options.keyCommand.wasPressed() && bl) {
                                    this.openScreen(new ChatScreen("/"));
                                }

                                if (this.player.isUsingItem()) {
                                    if (!this.options.keyUse.isPressed()) {
                                        this.interactionManager.stopUsingItem(this.player);
                                    }

                                    while (true) {
                                        if (!this.options.keyAttack.wasPressed()) {
                                            while (this.options.keyUse.wasPressed()) {
                                            }

                                            while (this.options.keyPickItem.wasPressed()) {
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    while (this.options.keyAttack.wasPressed()) {
                                        this.doAttack();
                                    }

                                    while (this.options.keyUse.wasPressed()) {
                                        this.doUse();
                                    }

                                    while (this.options.keyPickItem.wasPressed()) {
                                        this.doPick();
                                    }
                                }

                                if (this.options.keyUse.isPressed() && this.blockPlaceDelay == 0 && !this.player.isUsingItem()) {
                                    this.doUse();
                                }

                                this.handleBlockBreaking(this.currentScreen == null && this.options.keyAttack.isPressed() && this.focused);
                                break label504;
                            }

                            k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                            KeyBinding.setKeyPressed(k, Keyboard.getEventKeyState());
                            if (Keyboard.getEventKeyState()) {
                                KeyBinding.onKeyPressed(k);
                            }

                            if (this.f3CTime > 0L) {
                                if (getTime() - this.f3CTime >= 6000L) {
                                    throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
                                }

                                if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                                    this.f3CTime = -1L;
                                }
                            } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                                this.f3CTime = getTime();
                            }

                            this.handleKeyInput();
                        } while (!Keyboard.getEventKeyState());

                        if (k == 62 && this.gameRenderer != null) {
                            this.gameRenderer.toggleShadersEnabled();
                        }

                        if (this.currentScreen != null) {
                            this.currentScreen.handleKeyboard();
                        } else {
                            if (k == 1) {
                                this.openGameMenuScreen();
                            }

                            if (k == 32 && Keyboard.isKeyDown(61) && this.inGameHud != null) {
                                this.inGameHud.getChatHud().clear();
                            }

                            if (k == 31 && Keyboard.isKeyDown(61)) {
                                this.stitchTextures();
                            }

                            if (k == 17 && Keyboard.isKeyDown(61)) {
                            }

                            if (k == 18 && Keyboard.isKeyDown(61)) {
                            }

                            if (k == 47 && Keyboard.isKeyDown(61)) {
                            }

                            if (k == 38 && Keyboard.isKeyDown(61)) {
                            }

                            if (k == 22 && Keyboard.isKeyDown(61)) {
                            }

                            if (k == 20 && Keyboard.isKeyDown(61)) {
                                this.stitchTextures();
                            }

                            if (k == 33 && Keyboard.isKeyDown(61)) {
                                this.options.getBooleanValue(GameOptions.Option.RENDER_DISTANCE, Screen.hasShiftDown() ? -1 : 1);
                            }

                            if (k == 30 && Keyboard.isKeyDown(61)) {
                                this.worldRenderer.reload();
                            }

                            if (k == 35 && Keyboard.isKeyDown(61)) {
                                this.options.advancedItemTooltips = !this.options.advancedItemTooltips;
                                this.options.save();
                            }

                            if (k == 48 && Keyboard.isKeyDown(61)) {
                                this.entityRenderDispatcher.method_3900(!this.entityRenderDispatcher.method_3896());
                            }

                            if (k == 25 && Keyboard.isKeyDown(61)) {
                                this.options.pauseOnLostFocus = !this.options.pauseOnLostFocus;
                                this.options.save();
                            }

                            if (k == 59) {
                                this.options.hudHidden = !this.options.hudHidden;
                            }

                            if (k == 61) {
                                this.options.debugEnabled = !this.options.debugEnabled;
                                this.options.debugProfilerEnabled = Screen.hasShiftDown();
                                this.options.debugFpsEnabled = Screen.hasAltDown();
                            }

                            if (this.options.keyTogglePerspective.wasPressed()) {
                                ++this.options.perspective;
                                if (this.options.perspective > 2) {
                                    this.options.perspective = 0;
                                }

                                if (this.options.perspective == 0) {
                                    this.gameRenderer.onCameraEntitySet(this.getCameraEntity());
                                } else if (this.options.perspective == 1) {
                                    this.gameRenderer.onCameraEntitySet((Entity) null);
                                }

                                this.worldRenderer.scheduleTerrainUpdate();
                            }

                            if (this.options.keySmoothCamera.wasPressed()) {
                                this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
                            }
                        }
                    } while (!this.options.debugEnabled);
                } while (!this.options.debugProfilerEnabled);

                if (k == 11) {
                    this.method_2292(0);
                }

                for (int m = 0; m < 9; ++m) {
                    if (k == 2 + m) {
                        this.method_2292(m + 1);
                    }
                }
            }
            profiler.pop();
        }
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"
        ),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;tick()V"),
            to = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=mouse")
        )
    )
    Screen removeTickInput(MinecraftClient client) {
        return DummyScreen.INSTANCE;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;handleInput()V"))
    void removeTickScreenInput(Screen screen) {
    }
}
