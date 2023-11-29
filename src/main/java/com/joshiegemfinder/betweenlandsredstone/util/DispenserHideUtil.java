package com.joshiegemfinder.betweenlandsredstone.util;

import com.joshiegemfinder.betweenlandsredstone.blocks.dispenser.IDispenserHider;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class DispenserHideUtil {
	
	public static boolean canDispenserHide(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
		EnumFacing.Axis axis = state.getValue(BlockDirectional.FACING).getAxis();
		for(EnumFacing facing : EnumFacing.VALUES) {
			if(axis == facing.getAxis()) { continue; }
			
			BlockPos hiderPos = pos.offset(facing);
			IBlockState hiderState = blockAccess.getBlockState(hiderPos);
			if(!(hiderState.getBlock() instanceof IDispenserHider) || !(((IDispenserHider)hiderState.getBlock()).canHideDispenser(hiderState, blockAccess, hiderPos, state))) {
				return false;
			}
		}
		return true;
	}
	
}
