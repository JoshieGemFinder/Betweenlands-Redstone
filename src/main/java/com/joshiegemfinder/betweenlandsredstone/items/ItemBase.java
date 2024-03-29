package com.joshiegemfinder.betweenlandsredstone.items;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;

import net.minecraft.item.Item;

public class ItemBase extends Item implements IModelInterface {

    public ItemBase(String name) {

        setUnlocalizedName(name);
        setRegistryName(name);

        ModItems.ITEMS.add(this);
    }

	@Override
	public void registerModels() {
		BetweenlandsRedstone.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
