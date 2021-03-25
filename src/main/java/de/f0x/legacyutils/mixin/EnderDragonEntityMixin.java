package de.f0x.legacyutils.mixin;

import net.minecraft.entity.boss.BossBarProvider;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity implements BossBarProvider {
    public EnderDragonEntityMixin(World world) {
        super(world);
    }

    @Override
    public Text getBossName() {
        return getName();
    }
}
