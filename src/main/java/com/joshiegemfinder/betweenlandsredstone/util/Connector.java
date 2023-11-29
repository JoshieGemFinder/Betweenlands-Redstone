package com.joshiegemfinder.betweenlandsredstone.util;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class Connector {

	public static boolean canScabystConnect(IBlockAccess access, BlockPos pos, EnumFacing side) {
		return access == null || pos == null || side == null || access.getBlockState(pos.offset(side.getOpposite())).getBlock() != Blocks.REDSTONE_WIRE;
	}
	
}
