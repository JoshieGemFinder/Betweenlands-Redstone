package com.joshiegemfinder.betweenlandsredstone.blocks.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class DispenserStateMapper extends StateMapperBase {

	public static final DispenserStateMapper INSTANCE = new DispenserStateMapper();
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
//		Block block = state.getBlock();
		
//		if(!(block instanceof IHidingDispenser)) {
//			throw new IllegalArgumentException("Invalid blockstate;");
//		}
		
		ResourceLocation domain = (ResourceLocation)Block.REGISTRY.getNameForObject(state.getBlock());
		
		StringBuilder builder = new StringBuilder();
		builder.append(BlockDirectional.FACING.getName()).append("=").append(state.getValue(BlockDirectional.FACING).getName2());
		boolean hidden = state.getValue(BlockScabystDispenser.HIDDEN) && !state.getValue(BlockDispenser.TRIGGERED);
		builder.append(",hidden=").append(hidden);
		
		return new ModelResourceLocation(domain, builder.toString());
	}

	
}
