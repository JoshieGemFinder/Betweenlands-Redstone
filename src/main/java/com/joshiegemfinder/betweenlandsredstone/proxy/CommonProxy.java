package com.joshiegemfinder.betweenlandsredstone.proxy;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.util.ModelRegisterer;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class CommonProxy implements IProxy {

	@Override
	public void registerItemRenderer(Item item, int meta, ResourceLocation location, String id) {}
	
	@Override
	public void registerItemRenderer(Item item, int meta, String id) {}

	@Override
	public void preInit() {
		
	}

	@Override
	public void init() {
		
	}

	@Override
	public void postInit() {
		
	}

	@Override
	public void addItemToRegistry(Item item, String name, @Nullable ModelRegisterer registerModel) {
    	item.setUnlocalizedName(name);
    	item.setRegistryName(new ResourceLocation(BetweenlandsRedstone.MODID, name));
		ModItems.ITEMS.add(item);
		
		item.setCreativeTab(CreativeTabs.REDSTONE);
	}

	@Override
	public void addItemToRegistry(Item item, String name) {
    	item.setUnlocalizedName(name);
    	item.setRegistryName(new ResourceLocation(BetweenlandsRedstone.MODID, name));
		ModItems.ITEMS.add(item);
		
		item.setCreativeTab(CreativeTabs.REDSTONE);
	}

	@Override
	public void playAttenuatedSound(float xPosF, float yPosF, float zPosF, SoundEvent soundEvent, SoundCategory category, float volume, float pitch, float attenuationDistance) {}
	
}
