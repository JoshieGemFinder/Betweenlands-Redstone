package com.joshiegemfinder.betweenlandsredstone.proxy;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.util.ModelRegisterer;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public interface IProxy {

	public void preInit();
	
	public void init();
	
	public void postInit();
	
	public void registerItemRenderer(Item item, int meta, String id);
	
	public void registerItemRenderer(Item item, int meta, ResourceLocation location, String id);
	
	public void addItemToRegistry(Item item, String name, @Nullable ModelRegisterer registerModel);
	
	public void addItemToRegistry(Item item, String name);
	
	public void playAttenuatedSound(float xPosF, float yPosF, float zPosF, SoundEvent soundEvent, SoundCategory category, float volume, float pitch, float attenuationDistance);
}