package com.joshiegemfinder.betweenlandsredstone.items;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockScabystBulb.DecayState;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemScabystBulb extends ItemBlockBase {

	public ItemScabystBulb(Block block, String name) {
		super(block, name);
		
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public void registerModels() {
		String domain = this.getRegistryName().getResourceDomain();
		String path = this.getRegistryName().getResourcePath();
		BetweenlandsRedstone.proxy.registerItemRenderer(this, 0, new ResourceLocation(domain, path + "_normal"), "inventory");
		BetweenlandsRedstone.proxy.registerItemRenderer(this, 1, new ResourceLocation(domain, path + "_sludgy"), "inventory");
		BetweenlandsRedstone.proxy.registerItemRenderer(this, 2, new ResourceLocation(domain, path + "_deteriorated"), "inventory");
		BetweenlandsRedstone.proxy.registerItemRenderer(this, 3, new ResourceLocation(domain, path + "_decayed"), "inventory");
	}

	@Override
	public int getMetadata(int damage) {
		return damage << 2;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		
		return super.getUnlocalizedName() + "." + DecayState.byIndex(meta).getName();
	}
}