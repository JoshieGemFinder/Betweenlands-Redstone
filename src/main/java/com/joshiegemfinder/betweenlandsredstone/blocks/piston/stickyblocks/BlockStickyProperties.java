package com.joshiegemfinder.betweenlandsredstone.blocks.piston.stickyblocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockStickyProperties {

	public boolean shouldTryStickTo(IBlockState myBlockIn, IBlockState stickToIn) {
		Block myBlock = myBlockIn.getBlock();
		Block stickTo = stickToIn.getBlock();
		if(myBlock.isStickyBlock(myBlockIn) && stickTo.isStickyBlock(stickToIn) && myBlock != stickTo) {
			return false;
		}
		return true;
	}
	
	public boolean shouldUseHoneyPush() {
		return false;
	}
}
