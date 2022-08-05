package com.joshiegemfinder.betweenlandsredstone.blocks.rail;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;

import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockScabystRailPowered extends BlockRailPowered implements IScabystBlock {

	//Vanilla is dumb and stupid and thats why there's two constructors
	public BlockScabystRailPowered(String name) {
		this(name, false);
	}

	public BlockScabystRailPowered(String name, boolean isActivator) {
		super(isActivator);

		this.setHardness(0.7F);
		this.setSoundType(SoundType.METAL);

		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int getLightValue(IBlockState state) {
		if(state.getValue(POWERED)) {
			return 4;
		}
		return super.getLightValue(state);
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if(state.getValue(POWERED)) {
			return 4;
		}
		return super.getLightValue(state, world, pos);
	}
}
