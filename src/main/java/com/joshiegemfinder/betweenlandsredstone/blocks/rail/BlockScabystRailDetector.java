package com.joshiegemfinder.betweenlandsredstone.blocks.rail;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;

import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;

public class BlockScabystRailDetector extends BlockRailDetector {

	public BlockScabystRailDetector(String name) {
		super();
		
		this.setHardness(0.7F);
		this.setSoundType(SoundType.METAL);

		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
	}
	
}
