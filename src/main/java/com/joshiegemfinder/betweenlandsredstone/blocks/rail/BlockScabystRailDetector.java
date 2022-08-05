package com.joshiegemfinder.betweenlandsredstone.blocks.rail;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;

import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockScabystRailDetector extends BlockRailDetector implements IScabystBlock {

	public BlockScabystRailDetector(String name) {
		super();
		
		this.setHardness(0.7F);
		this.setSoundType(SoundType.METAL);

		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
	}
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return IScabystBlock.super.getWeakPower(blockState, blockAccess, pos, side);
	}
	
	@Override
	public int getScabystWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return super.getWeakPower(blockState, blockAccess, pos, side);
	}
	
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return IScabystBlock.super.getStrongPower(blockState, blockAccess, pos, side);
	}

	@Override
    public int getScabystStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (!((Boolean)blockState.getValue(POWERED)).booleanValue())
        {
            return 0;
        }
        else
        {
            return side == EnumFacing.UP ? 15 : 0;
        }
    }
}
