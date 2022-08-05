package com.joshiegemfinder.betweenlandsredstone.blocks;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.util.Discriminator;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;

import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockScabystObserver extends BlockObserver implements IScabystBlock {

	public BlockScabystObserver(String name) {
		super();

		this.setHardness(3.0F);
		
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(Main.MODID, name));
		ModBlocks.BLOCKS.add(this);
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return super.canConnectRedstone(state, world, pos, side) && Discriminator.canScabystConnect(world, pos, side);
	}
	
	@Override
	public int getScabystWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return super.getWeakPower(blockState, blockAccess, pos, side);
	}
	
	@Override
	public int getScabystStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return super.getWeakPower(blockState, blockAccess, pos, side);
	}
    
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return IScabystBlock.super.getWeakPower(blockState, blockAccess, pos, side);
    }
    
    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return IScabystBlock.super.getStrongPower(blockState, blockAccess, pos, side);
    }
}
