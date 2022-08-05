package com.joshiegemfinder.betweenlandsredstone.blocks.rail;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;

import net.minecraft.block.BlockRail;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;

public class BlockScabystRail extends BlockRail implements IScabystBlock {

	public BlockScabystRail(String name) {
		super();
		
		this.setHardness(0.7F);
		this.setSoundType(SoundType.METAL);

		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
	}	
}
