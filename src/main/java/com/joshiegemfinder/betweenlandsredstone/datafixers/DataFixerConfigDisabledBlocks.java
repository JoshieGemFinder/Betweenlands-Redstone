package com.joshiegemfinder.betweenlandsredstone.datafixers;

import com.joshiegemfinder.betweenlandsredstone.BLRedstoneConfig;
import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DataFixerConfigDisabledBlocks {

	private static final ResourceLocation SYRMORITE_BARS_LOCATION = new ResourceLocation(BetweenlandsRedstone.MODID, "syrmorite_bars");
	private static final ResourceLocation WHITE_PEAR_BLOCK_LOCATION = new ResourceLocation(BetweenlandsRedstone.MODID, "white_pear_block");
	private static final ResourceLocation CRAFTER_LOCATION = new ResourceLocation(BetweenlandsRedstone.MODID, "crafter");
	private static final ResourceLocation PETAL_BASKET_LOCATION = new ResourceLocation(BetweenlandsRedstone.MODID, "petal_basket");

	@SubscribeEvent
	public static void itemMappings(MissingMappings<Item> e) {
		for(Mapping<Item> map : e.getMappings()) {
			if(
				map.key.equals(SYRMORITE_BARS_LOCATION) || map.key.equals(WHITE_PEAR_BLOCK_LOCATION) ||
				map.key.equals(CRAFTER_LOCATION) || map.key.equals(PETAL_BASKET_LOCATION)
			) {
				if(BLRedstoneConfig.EXTRA_FEATURES.disableSilently)
					map.ignore();
//				else
//					map.warn(); // warn DOENS'T WORK >:(
			}
		}
	}

	@SubscribeEvent
	public static void blockMappings(MissingMappings<Block> e) {
		for(Mapping<Block> map : e.getMappings()) {
			if(
				map.key.equals(SYRMORITE_BARS_LOCATION) || map.key.equals(WHITE_PEAR_BLOCK_LOCATION) ||
				map.key.equals(CRAFTER_LOCATION) || map.key.equals(PETAL_BASKET_LOCATION)
			) {
				if(BLRedstoneConfig.EXTRA_FEATURES.disableSilently)
					map.ignore();
//				else
//					map.warn();
			}
		}
	}
}
