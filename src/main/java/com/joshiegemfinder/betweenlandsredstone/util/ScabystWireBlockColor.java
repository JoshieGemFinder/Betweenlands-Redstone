package com.joshiegemfinder.betweenlandsredstone.util;

import com.joshiegemfinder.betweenlandsredstone.blocks.BlockScabystWire;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ScabystWireBlockColor implements IBlockColor {

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		int power = state.getValue(BlockScabystWire.POWER);
		
		int red = ScabystColor.shades[power].getR();
		int green = ScabystColor.shades[power].getG();
		int blue = ScabystColor.shades[power].getB();
		return -16777216 | red << 16 | green << 8 | blue;
	}

}
