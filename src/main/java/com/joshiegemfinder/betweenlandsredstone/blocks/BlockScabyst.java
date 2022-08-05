package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.util.Discriminator;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockScabyst extends Block implements IScabystBlock {
	
	public BlockScabyst(String name) {
		super(Material.IRON);
		
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
		
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(Main.MODID, name));
		ModBlocks.BLOCKS.add(this);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		 return new ItemStack(this);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.SCABYST_BLOCK;
	}

	@Override
    public int getScabystWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return 15;
    }

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return IScabystBlock.super.getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return super.canConnectRedstone(state, world, pos, side) && Discriminator.canScabystConnect(world, pos, side);
	}
}
