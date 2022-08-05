package com.joshiegemfinder.betweenlandsredstone.items;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;
import com.joshiegemfinder.betweenlandsredstone.entity.EntityScabystItemFrame;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScabystHangingEntity extends Item implements IModelInterface
{
    private final Class <? extends EntityHanging > hangingEntityClass;

    public ItemScabystHangingEntity(String name, Class <? extends EntityHanging > entityClass)
    {
        this.hangingEntityClass = entityClass;

    	this.setUnlocalizedName(name);
    	this.setRegistryName(name);
    	ModItems.ITEMS.add(this);
    }

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        BlockPos blockpos = pos.offset(facing);

        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(blockpos, facing, itemstack))
        {
            EntityHanging entityhanging = this.createEntity(worldIn, blockpos, facing);

            if (entityhanging != null && entityhanging.onValidSurface())
            {
                if (!worldIn.isRemote)
                {
                    entityhanging.playPlaceSound();
                    worldIn.spawnEntity(entityhanging);
                }

                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    @Nullable
    private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide)
    {
        /*if (this.hangingEntityClass == EntityPainting.class)
        {
            return new EntityPainting(worldIn, pos, clickedSide);
        }
        else
        {*/
            return this.hangingEntityClass == EntityScabystItemFrame.class ? new EntityScabystItemFrame(worldIn, pos, clickedSide) : null;
        //}
    }
}