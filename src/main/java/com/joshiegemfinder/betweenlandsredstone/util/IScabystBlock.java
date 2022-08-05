package com.joshiegemfinder.betweenlandsredstone.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IScabystBlock {
	
	public default int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if(Discriminator.non()) {
			return this.getScabystWeakPower(blockState, blockAccess, pos, side);
		}
		return Discriminator.canScabystConnect(blockAccess, pos, side) && (side == null || !(blockAccess.getBlockState(pos.offset(side.getOpposite())).getBlock() instanceof IScabystBlock)) ? this.getScabystWeakPower(blockState, blockAccess, pos, side) : 0;
	}
	
	public default int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if(Discriminator.non()) {
			return this.getScabystStrongPower(blockState, blockAccess, pos, side);
		}
		IBlockState state = side == null ? null : blockAccess.getBlockState(pos.offset(side.getOpposite()));
		if(state != null) {
			Block block = state.getBlock();
			for(EnumFacing facing : EnumFacing.VALUES) {
				if(block.shouldCheckWeakPower(state, blockAccess, pos, facing)) {
					return 0;
				}
			}
		}
		return Discriminator.non() || (Discriminator.canScabystConnect(blockAccess, pos, side) && !(state == null || state.getBlock() instanceof IScabystBlock)) ? this.getScabystStrongPower(blockState, blockAccess, pos, side) : 0;
	}
	
	public default int getScabystWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return 0;
	}
	
	public default int getScabystStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return 0;
	}
}
