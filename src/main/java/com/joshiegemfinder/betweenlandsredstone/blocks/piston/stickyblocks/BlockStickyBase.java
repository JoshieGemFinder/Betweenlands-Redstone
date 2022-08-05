package com.joshiegemfinder.betweenlandsredstone.blocks.piston.stickyblocks;

import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public abstract class BlockStickyBase extends Block implements IScabystBlock {
	
	protected BlockStickyProperties stickyProperties = new BlockStickyProperties();
	
	public BlockStickyBase(Material materialIn) {
		super(materialIn);
	}

	public BlockStickyBase(Material blockMaterialIn, MapColor blockMapColorIn) {
		super(blockMaterialIn, blockMapColorIn);
	}
	
	public BlockStickyProperties getStickyProperties() {
		return stickyProperties;
	}
	
	@Override
	public boolean isStickyBlock(IBlockState state) {
		Block block = state.getBlock();
		if(block == this) {
			return true;
		}
		return block.isStickyBlock(state);
	}
	
	public static boolean shouldBlocksStick(IBlockState blockstate1, IBlockState blockstate2) {
		Block block1 = blockstate1.getBlock();
		Block block2 = blockstate2.getBlock();
		return (
	  				(
  						block1 instanceof BlockStickyBase &&
						((BlockStickyBase)block1).stickyProperties.shouldTryStickTo(blockstate1, blockstate2)
					)
					||
		      		(
		  				block2 instanceof BlockStickyBase &&
	  	 				((BlockStickyBase)block2).stickyProperties.shouldTryStickTo(blockstate2, blockstate1)
		      		)
				);
	}
}
