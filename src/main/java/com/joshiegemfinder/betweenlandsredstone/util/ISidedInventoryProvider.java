package com.joshiegemfinder.betweenlandsredstone.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISidedInventoryProvider {

	public ISidedInventory getContainer(World worldIn, BlockPos pos, IBlockState state);
	
}
