package com.joshiegemfinder.betweenlandsredstone.entity.minecart;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

public class EntityScabystMinecartEmpty extends EntityScabystMinecart {

	public EntityScabystMinecartEmpty(World worldIn) {
		super(worldIn);
	}
	
	public EntityScabystMinecartEmpty(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

    public static void registerFixesMinecartEmpty(DataFixer fixer)
    {
        EntityMinecart.registerFixesMinecart(fixer, EntityScabystMinecartEmpty.class);
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (super.processInitialInteract(player, hand)) return true;

        if (player.isSneaking())
        {
            return false;
        }
        else if (this.isBeingRidden())
        {
            return true;
        }
        else
        {
            if (!this.world.isRemote)
            {
                player.startRiding(this);
            }

            return true;
        }
    }

    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
    {
        if (receivingPower)
        {
            if (this.isBeingRidden())
            {
                this.removePassengers();
            }

            if (this.getRollingAmplitude() == 0)
            {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setDamage(50.0F);
                this.markVelocityChanged();
            }
        }
    }

	@Override
	public Type getTrueType() {
		return Type.RIDEABLE;
	}

}
