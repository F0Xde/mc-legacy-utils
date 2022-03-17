package de.f0x.legacyutils.mixin;

import com.mojang.authlib.GameProfile;
import de.f0x.legacyutils.command.LegacyCommandManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.f0x.legacyutils.util.MinecraftExtensionsKt.getPitch;
import static de.f0x.legacyutils.util.MinecraftExtensionsKt.getYaw;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;

    public ClientPlayerEntityMixin(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    void onSendChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith(LegacyCommandManager.PREFIX)) {
            LegacyCommandManager.INSTANCE.execute(
                message.substring(LegacyCommandManager.PREFIX.length()),
                (ClientPlayerEntity) (Object) this
            );
            ci.cancel();
        } else if (message.startsWith('\\' + LegacyCommandManager.PREFIX)) {
            networkHandler.sendPacket(new ChatMessageC2SPacket(message.substring(1)));
            ci.cancel();
        }
    }

    @Override
    public Vec3d getRotationVector(float tickDelta) {
        return getRotationVector(getPitch(this, tickDelta), getYaw(this, tickDelta));
    }
}
