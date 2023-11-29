package com.joshiegemfinder.betweenlandsredstone.blocks.dispenser;

import com.joshiegemfinder.betweenlandsredstone.util.DispenserHideUtil;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IHidingDispenser {

	public default boolean shouldDispenserTryToHide(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
		TileEntity te = blockAccess.getTileEntity(pos);
		final boolean triggeredFlag = !(state.getValue(BlockDispenser.TRIGGERED).booleanValue());
		if(!(te instanceof ISyncIsEmpty)) {
			return triggeredFlag;
		}
		return triggeredFlag && !(((ISyncIsEmpty)te).isEmptyClient());
	}
	
	public default boolean shouldDispenserBeHidden(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
		 return this.shouldDispenserTryToHide(state, blockAccess, pos) && DispenserHideUtil.canDispenserHide(state, blockAccess, pos);
	}
	
	public void updateHidingState(IBlockState state, World worldIn, BlockPos pos);
}
