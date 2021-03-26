package de.f0x.legacyutils.mixin;

import net.minecraft.entity.boss.BossBarProvider;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin extends HostileEntity implements BossBarProvider {
    public WitherEntityMixin(World world) {
        super(world);
    }

    @Override
    public Text title() {
        return getName();
    }
}
