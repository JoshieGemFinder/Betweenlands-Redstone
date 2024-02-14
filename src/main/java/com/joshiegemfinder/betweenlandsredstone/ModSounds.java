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

	public static final SoundEvent BLOCK_BULB_BREAK;
	public static final SoundEvent BLOCK_BULB_FALL;
	public static final SoundEvent BLOCK_BULB_HIT;
	public static final SoundEvent BLOCK_BULB_PLACE;
	public static final SoundEvent BLOCK_BULB_STEP;

	public static final SoundEvent BLOCK_BULB_SMEAR;
	public static final SoundEvent BLOCK_BULB_SCRAPE;
	public static final SoundEvent BLOCK_BULB_TURN_ON;
	public static final SoundEvent BLOCK_BULB_TURN_OFF;
	
	public static final SoundType BULB;
	
	public static final SoundEvent BLOCK_CRAFTER_SUCCEED;
	public static final SoundEvent BLOCK_CRAFTER_FAIL;
	
	static {
		BLOCK_SAP_BREAK = createSound("block.sap_block.break");
		BLOCK_SAP_FALL = createSound("block.sap_block.fall");
		BLOCK_SAP_HIT = createSound("block.sap_block.hit");
		BLOCK_SAP_PLACE = createSound("block.sap_block.place");
		BLOCK_SAP_STEP = createSound("block.sap_block.step");
		BLOCK_SAP_SLIDE = createSound("block.sap_block.slide");
		
		SAP = new SoundType(1.0F, 1.0F, BLOCK_SAP_BREAK, BLOCK_SAP_STEP, BLOCK_SAP_PLACE, BLOCK_SAP_HIT, BLOCK_SAP_FALL);
		
		BLOCK_BULB_BREAK = createSound("block.scabyst_bulb.break");
		BLOCK_BULB_FALL = createSound("block.scabyst_bulb.fall");
		BLOCK_BULB_HIT = createSound("block.scabyst_bulb.hit");
		BLOCK_BULB_PLACE = createSound("block.scabyst_bulb.place");
		BLOCK_BULB_STEP = createSound("block.scabyst_bulb.step");

		BLOCK_BULB_SMEAR = createSound("block.scabyst_bulb.smear");
		BLOCK_BULB_SCRAPE = createSound("block.scabyst_bulb.scrape");
		BLOCK_BULB_TURN_ON = createSound("block.scabyst_bulb.turn_on");
		BLOCK_BULB_TURN_OFF = createSound("block.scabyst_bulb.turn_off");
		
		BULB = new SoundType(1.0F, 1.0F, BLOCK_BULB_BREAK, BLOCK_BULB_FALL, BLOCK_BULB_PLACE, BLOCK_BULB_HIT, BLOCK_BULB_STEP);

		BLOCK_CRAFTER_SUCCEED = createSound("block.crafter.craft");
		BLOCK_CRAFTER_FAIL = createSound("block.crafter.fail");
	}
	
	private static final SoundEvent createSound(String path) {
		ResourceLocation location = new ResourceLocation(BetweenlandsRedstone.MODID, path);
		SoundEvent event = new SoundEvent(location).setRegistryName(location);
		SOUNDS.add(event);
		return event;
	}
}
