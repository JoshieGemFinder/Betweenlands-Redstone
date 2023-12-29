package com.joshiegemfinder.betweenlandsredstone;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSounds {

	public static final List<SoundEvent> SOUNDS = new ArrayList<SoundEvent>();
	
	public static final SoundEvent BLOCK_SAP_BREAK;
	public static final SoundEvent BLOCK_SAP_FALL;
	public static final SoundEvent BLOCK_SAP_HIT;
	public static final SoundEvent BLOCK_SAP_PLACE;
	public static final SoundEvent BLOCK_SAP_STEP;
	public static final SoundEvent BLOCK_SAP_SLIDE;
	
	public static final SoundType SAP;
	
	static {
		BLOCK_SAP_BREAK = createSound("block.sap_block.break");
		BLOCK_SAP_FALL = createSound("block.sap_block.fall");
		BLOCK_SAP_HIT = createSound("block.sap_block.hit");
		BLOCK_SAP_PLACE = createSound("block.sap_block.place");
		BLOCK_SAP_STEP = createSound("block.sap_block.step");
		BLOCK_SAP_SLIDE = createSound("block.sap_block.slide");
		
		SAP = new SoundType(1.0F, 1.0F, BLOCK_SAP_BREAK, BLOCK_SAP_STEP, BLOCK_SAP_PLACE, BLOCK_SAP_HIT, BLOCK_SAP_FALL);
	}
	
	private static final SoundEvent createSound(String path) {
		ResourceLocation location = new ResourceLocation(BetweenlandsRedstone.MODID, path);
		SoundEvent event = new SoundEvent(location).setRegistryName(location);
		SOUNDS.add(event);
		return event;
	}
}
