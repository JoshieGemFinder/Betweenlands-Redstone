package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneLight;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityStalker;

public class BlockScabystLight extends BlockRedstoneLight {
    
	public BlockScabystLight(String name, boolean isOn) {
		super(isOn);
		
		this.setHardness(0.3F);
		this.setSoundType(SoundType.GLASS);
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(Main.MODID, name));
		ModBlocks.BLOCKS.add(this);
	}
	

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
        	boolean powered = worldIn.isBlockPowered(pos);
            if (this.isOn && !powered)
            {
                worldIn.setBlockState(pos, ModBlocks.SCABYST_LAMP.getDefaultState(), 2);
            }
            else if (!this.isOn && powered)
            {
                worldIn.setBlockState(pos, ModBlocks.LIT_SCABYST_LAMP.getDefaultState(), 2);
            }
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
        	boolean powered = worldIn.isBlockPowered(pos);
            if (this.isOn && !powered)
            {
                worldIn.scheduleUpdate(pos, this, 4);
            }
            else if (!this.isOn && powered)
            {
                worldIn.setBlockState(pos, ModBlocks.LIT_SCABYST_LAMP.getDefaultState(), 2);
            }
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (this.isOn && !worldIn.isBlockPowered(pos))
            {
                worldIn.setBlockState(pos, ModBlocks.SCABYST_LAMP.getDefaultState(), 2);
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ModItems.SCABYST_LAMP;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModItems.SCABYST_LAMP);
    }

    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        return new ItemStack(ModItems.SCABYST_LAMP);
    }
    
    //stop stalkers munching on blocks
    @Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		if(entity instanceof EntityStalker) {
			return false;
		}
		return super.canEntityDestroy(state, world, pos, entity);
	}
}
