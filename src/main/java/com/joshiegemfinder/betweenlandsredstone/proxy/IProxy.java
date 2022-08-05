package com.joshiegemfinder.betweenlandsredstone.proxy;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.util.ModelRegisterer;

import net.minecraft.item.Item;

public interface IProxy {

	public void preInit();
	
	public void init();
	
	public void postInit();
	
	public void registerItemRenderer(Item item, int meta, String id);
	
	public void addItemToRegistry(Item item, String name, @Nullable ModelRegisterer registerModel);
	
	public void addItemToRegistry(Item item, String name);
}