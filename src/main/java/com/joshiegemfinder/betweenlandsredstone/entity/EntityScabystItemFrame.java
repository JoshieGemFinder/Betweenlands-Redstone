package com.joshiegemfinder.betweenlandsredstone.entity;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.ModItems;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityScabystItemFrame extends EntityItemFrame implements IEntityAdditionalSpawnData {

	private static final String TAG_ITEM_DROP_CHANCE = "ItemDropChance";

	private float itemDropChance = 1.0F;
	
	public EntityScabystItemFrame(World worldIn)
    {
        super(worldIn);
    }

    public EntityScabystItemFrame(World worldIn, BlockPos pos, EnumFacing facing)
    {
    	super(worldIn, pos, facing);
    	this.facingDirection = facing;
    }
    
    @Override
    protected void entityInit() {
    	super.entityInit();
    	//Packet<?> packet = FMLNetworkHandler.getEntitySpawningPacket(this);
    	
    }
    
    @Override
    public void dropItemOrSelf(@Nullable Entity entityIn, boolean empty)
    {
        if (this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            ItemStack itemstack = this.getDisplayedItem();

            if (entityIn instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)entityIn;

                if (entityplayer.capabilities.isCreativeMode)
                {
                    this.removeFrameFromMap(itemstack);
                    return;
                }
            }

            if (empty)
            {
                this.entityDropItem(new ItemStack(ModItems.SCABYST_ITEM_FRAME), 0.0F);
            }

            if (!itemstack.isEmpty() && this.rand.nextFloat() < itemDropChance)
            {
                itemstack = itemstack.copy();
                this.removeFrameFromMap(itemstack);
                this.entityDropItem(itemstack, 0.0F);
            }
        }
    }

    private void removeFrameFromMap(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof net.minecraft.item.ItemMap)
            {
                MapData mapdata = ((ItemMap)stack.getItem()).getMapData(stack, this.world);
                mapdata.mapDecorations.remove("frame-" + this.getEntityId());
            }

            stack.setItemFrame((EntityItemFrame)null);
            this.setDisplayedItem(ItemStack.EMPTY); //Forge: Fix MC-124833 Pistons duplicating Items.
        }
    }
    
    public static void registerFixesItemFrame(DataFixer fixer)
    {
        fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(EntityScabystItemFrame.class, new String[] {"Item"}));
    }
    
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		if(compound.hasKey(TAG_ITEM_DROP_CHANCE, 99)) {
			itemDropChance = compound.getFloat(TAG_ITEM_DROP_CHANCE);
		}

		super.readEntityFromNBT(compound);
		this.updateFacingWithBoundingBox(EnumFacing.getHorizontal(compound.getByte("Facing")));
	}
	
	@Override
    protected void updateBoundingBox() {
		super.updateBoundingBox();
	}


	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeByte(facingDirection.getHorizontalIndex());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		updateFacingWithBoundingBox(EnumFacing.getHorizontal(additionalData.readByte()));
	}
}
