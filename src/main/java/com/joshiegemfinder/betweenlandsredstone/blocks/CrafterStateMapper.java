package com.joshiegemfinder.betweenlandsredstone.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class CrafterStateMapper extends StateMapperBase {

	public static final CrafterStateMapper INSTANCE = new CrafterStateMapper();

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		final String activationState;
		boolean crafting = state.getValue(BlockCrafter.CRAFTING);
		boolean triggered = state.getValue(BlockCrafter.TRIGGERED);
		if(crafting && triggered)
			activationState = "crafting_triggered";
		else if(crafting) {
			activationState = "crafting";
		}
		else if(triggered) {
			activationState = "triggered";
		} else {
			activationState = "normal";
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("facing=").append(state.getValue(BlockCrafter.FACING).getName());
		builder.append(",");
		builder.append("state=").append(activationState);
		
		return new ModelResourceLocation(state.getBlock().getRegistryName(), builder.toString());
	}

}
