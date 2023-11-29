package com.joshiegemfinder.betweenlandsredstone.blocks.dispenser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IDispenserHider {

	public boolean canHideDispenser(IBlockState state, IBlockAccess blockAccess, BlockPos pos, IBlockState dispenserState);
	
}
