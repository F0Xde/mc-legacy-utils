package de.f0x.legacyutils.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static de.f0x.legacyutils.MinecraftExtensionsKt.getPitch;
import static de.f0x.legacyutils.MinecraftExtensionsKt.getYaw;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Override
    public Vec3d getRotationVector(float tickDelta) {
        return getRotationVector(getPitch(this, tickDelta), getYaw(this, tickDelta));
    }
}
