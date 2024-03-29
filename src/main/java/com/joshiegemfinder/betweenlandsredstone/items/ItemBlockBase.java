package com.joshiegemfinder.betweenlandsredstone.items;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class ItemBlockBase extends ItemBlock implements IModelInterface {

	public ItemBlockBase(Block block, String name) {
		super(block);

        setUnlocalizedName(name);
        setRegistryName(new ResourceLocation(BetweenlandsRedstone.MODID, name));

        ModItems.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		BetweenlandsRedstone.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
