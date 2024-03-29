package com.joshiegemfinder.betweenlandsredstone.blocks;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;

import net.minecraft.block.BlockObserver;
import net.minecraft.util.ResourceLocation;

public class BlockScabystObserver extends BlockObserver {

	public BlockScabystObserver(String name) {
		super();

		this.setHardness(3.0F);
		
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(BetweenlandsRedstone.MODID, name));
		ModBlocks.BLOCKS.add(this);
	}
	
}
