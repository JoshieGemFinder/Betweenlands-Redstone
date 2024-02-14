package com.joshiegemfinder.betweenlandsredstone;

import java.util.ArrayList;
import java.util.List;

import com.joshiegemfinder.betweenlandsredstone.blocks.BlockBLTarget;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockChestBetweenlandsTrapped;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockCrafter;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockRiftDetector;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockScabyst;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockScabystBulb;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockScabystLight;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockScabystObserver;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockScabystTorch;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockScabystWire;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockSyrmoriteBars;
import com.joshiegemfinder.betweenlandsredstone.blocks.diode.BlockScabystComparator;
import com.joshiegemfinder.betweenlandsredstone.blocks.diode.BlockScabystRepeater;
import com.joshiegemfinder.betweenlandsredstone.blocks.dispenser.BlockScabystDispenser;
import com.joshiegemfinder.betweenlandsredstone.blocks.dispenser.BlockScabystDropper;
import com.joshiegemfinder.betweenlandsredstone.blocks.extra.BlockCutPitstone;
import com.joshiegemfinder.betweenlandsredstone.blocks.extra.BlockPetalBasket;
import com.joshiegemfinder.betweenlandsredstone.blocks.extra.BlockPolishedPitstone;
import com.joshiegemfinder.betweenlandsredstone.blocks.extra.BlockWhitePear;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.BlockScabystPistonBase;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.BlockScabystPistonExtension;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.BlockScabystPistonMoving;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.stickyblocks.BlockScabystSlime_1;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.stickyblocks.BlockScabystSlime_2;
import com.joshiegemfinder.betweenlandsredstone.blocks.rail.BlockScabystRail;
import com.joshiegemfinder.betweenlandsredstone.blocks.rail.BlockScabystRailDetector;
import com.joshiegemfinder.betweenlandsredstone.blocks.rail.BlockScabystRailPowered;
import com.joshiegemfinder.betweenlandsredstone.blocks.tripwire.BlockScabystTripWire;
import com.joshiegemfinder.betweenlandsredstone.blocks.tripwire.BlockScabystTripWireHook;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;

public class ModBlocks {

	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	public static BlockScabystWire SCABYST_WIRE;
	public static BlockScabyst SCABYST_BLOCK;
	public static BlockScabystTorch SCABYST_TORCH;
	public static BlockScabystTorch UNLIT_SCABYST_TORCH;
	public static BlockScabystRepeater POWERED_SCABYST_REPEATER;
	public static BlockScabystRepeater UNPOWERED_SCABYST_REPEATER;
	public static BlockScabystComparator POWERED_SCABYST_COMPARATOR;
	public static BlockScabystComparator UNPOWERED_SCABYST_COMPARATOR;
	public static BlockScabystLight SCABYST_LAMP;
	public static BlockScabystLight LIT_SCABYST_LAMP;
	public static BlockScabystPistonBase SCABYST_PISTON;
	public static BlockScabystPistonBase SCABYST_STICKY_PISTON;
	
	//i know that the naming is confusing but its how vanilla names it
	public static BlockScabystPistonExtension SCABYST_PISTON_HEAD;
	public static BlockScabystPistonMoving SCABYST_PISTON_EXTENSION;
	//funny naming end

	public static BlockScabystSlime_1 SCABYST_SLIME_1;
	public static BlockScabystSlime_2 SCABYST_SLIME_2;
	
	public static BlockScabystObserver SCABYST_OBSERVER;

	public static BlockScabystRail SCABYST_RAIL;
	public static BlockScabystRailPowered SCABYST_POWERED_RAIL;
	public static BlockScabystRailPowered SCABYST_ACTIVATOR_RAIL;
	public static BlockScabystRailDetector SCABYST_DETECTOR_RAIL;

	public static BlockScabystDispenser SCABYST_DISPENSER;
	public static BlockScabystDropper SCABYST_DROPPER;

	public static BlockRiftDetector RIFT_DETECTOR;
	public static BlockRiftDetector RIFT_DETECTOR_INVERTED;

	public static BlockScabystTripWireHook TRIPWIRE_HOOK;
	public static BlockScabystTripWire TRIPWIRE;

	public static BlockChestBetweenlandsTrapped CHEST_TRAPPED;

	public static BlockBLTarget TARGET_BLOCK;

	public static BlockPolishedPitstone POLISHED_PITSTONE;
	public static BlockCutPitstone CUT_PITSTONE;
	public static BlockWhitePear WHITE_PEAR_BLOCK;
	
	public static BlockSyrmoriteBars SYRMORITE_BARS;

	public static BlockScabystBulb SCABYST_BULB;
	public static BlockCrafter CRAFTER;

	public static BlockPetalBasket PETAL_BASKET;
	
	public static Block addBlockToRegistry(Block block, String name) {
		block.setUnlocalizedName(name);
		block.setRegistryName(new ResourceLocation(BetweenlandsRedstone.MODID, name));
		CreativeTabs tab = block.getCreativeTabToDisplayOn();
		block.setCreativeTab(tab == null ? CreativeTabs.REDSTONE : tab);
		ModBlocks.BLOCKS.add(block);
		return block;
	}
	
	public static void init() {

		SCABYST_WIRE = new BlockScabystWire("scabyst_wire");
		
		SCABYST_BLOCK = new BlockScabyst("scabyst_block");
		SCABYST_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
		
		SCABYST_TORCH = new BlockScabystTorch("scabyst_torch", true);
		SCABYST_TORCH.setLightLevel(0.5F);
		SCABYST_TORCH.setCreativeTab(CreativeTabs.REDSTONE);
		UNLIT_SCABYST_TORCH = new BlockScabystTorch("unlit_scabyst_torch", false);
		
		POWERED_SCABYST_REPEATER = new BlockScabystRepeater("powered_scabyst_repeater", true);
		UNPOWERED_SCABYST_REPEATER = new BlockScabystRepeater("unpowered_scabyst_repeater", false);
		UNPOWERED_SCABYST_REPEATER.setCreativeTab(CreativeTabs.REDSTONE);
		
		POWERED_SCABYST_COMPARATOR = new BlockScabystComparator("powered_scabyst_comparator", true);
		POWERED_SCABYST_COMPARATOR.setLightLevel(0.625F);
		UNPOWERED_SCABYST_COMPARATOR = new BlockScabystComparator("unpowered_scabyst_comparator", false);
		UNPOWERED_SCABYST_COMPARATOR.setCreativeTab(CreativeTabs.REDSTONE);
		
		SCABYST_LAMP = new BlockScabystLight("scabyst_lamp", false);
		SCABYST_LAMP.setCreativeTab(CreativeTabs.REDSTONE);
		LIT_SCABYST_LAMP = new BlockScabystLight("lit_scabyst_lamp", true);

		SCABYST_PISTON = new BlockScabystPistonBase("scabyst_piston", false);
		SCABYST_PISTON.setCreativeTab(CreativeTabs.REDSTONE);
		SCABYST_STICKY_PISTON = new BlockScabystPistonBase("scabyst_sticky_piston", true);
		SCABYST_STICKY_PISTON.setCreativeTab(CreativeTabs.REDSTONE);
		
		//i know that the naming is confusing but its how vanilla does it
		SCABYST_PISTON_HEAD = new BlockScabystPistonExtension("scabyst_piston_head");
		SCABYST_PISTON_EXTENSION = new BlockScabystPistonMoving("scabyst_piston_extension");
		//funny naming end

		SCABYST_SLIME_1 = new BlockScabystSlime_1("scabyst_slime_1");
		SCABYST_SLIME_1.setCreativeTab(CreativeTabs.REDSTONE);
		SCABYST_SLIME_2 = new BlockScabystSlime_2("scabyst_slime_2");
		SCABYST_SLIME_2.setCreativeTab(CreativeTabs.REDSTONE);
		
		SCABYST_OBSERVER = new BlockScabystObserver("scabyst_observer");

		SCABYST_RAIL = new BlockScabystRail("scabyst_rail");
		SCABYST_POWERED_RAIL = new BlockScabystRailPowered("scabyst_golden_rail");
		SCABYST_ACTIVATOR_RAIL = new BlockScabystRailPowered("scabyst_activator_rail", true);
		SCABYST_DETECTOR_RAIL = new BlockScabystRailDetector("scabyst_detector_rail");

		SCABYST_DISPENSER = new BlockScabystDispenser("scabyst_dispenser");
		SCABYST_DROPPER = new BlockScabystDropper("scabyst_dropper");

		RIFT_DETECTOR = new BlockRiftDetector(false, "rift_detector");
		RIFT_DETECTOR_INVERTED = new BlockRiftDetector(true, "rift_detector_inverted");
		
		TRIPWIRE_HOOK = new BlockScabystTripWireHook();
		addBlockToRegistry(TRIPWIRE_HOOK, "scabyst_tripwire_hook");
		TRIPWIRE = new BlockScabystTripWire();
		addBlockToRegistry(TRIPWIRE, "scabyst_tripwire");
		
		CHEST_TRAPPED = new BlockChestBetweenlandsTrapped(BlockChestBetweenlandsTrapped.TRAPPED_WEEDWOOD_CHEST);
		addBlockToRegistry(CHEST_TRAPPED, "weedwood_chest_trapped");
		
		TARGET_BLOCK = new BlockBLTarget(false);
		addBlockToRegistry(TARGET_BLOCK, "target_block");
		
		POLISHED_PITSTONE = new BlockPolishedPitstone("polished_pitstone");
		CUT_PITSTONE = new BlockCutPitstone("cut_pitstone");
		
		
		SCABYST_BULB = new BlockScabystBulb(Material.IRON);
		addBlockToRegistry(SCABYST_BULB, "scabyst_bulb");
		
		
		if(BLRedstoneConfig.EXTRA_FEATURES.registerWhitePearBlock) {
			WHITE_PEAR_BLOCK = new BlockWhitePear("white_pear_block");
		}
		
		if(BLRedstoneConfig.EXTRA_FEATURES.registerSyrmoriteBars) {
			SYRMORITE_BARS = new BlockSyrmoriteBars(Material.IRON, true);
			addBlockToRegistry(SYRMORITE_BARS, "syrmorite_bars");
		}

		if(BLRedstoneConfig.EXTRA_FEATURES.registerCrafter) {
			CRAFTER = new BlockCrafter(Material.ROCK);
			addBlockToRegistry(CRAFTER, "crafter");
		}

		if(BLRedstoneConfig.EXTRA_FEATURES.registerPetalBasket) {
			PETAL_BASKET = new BlockPetalBasket(Material.CLOTH);
			addBlockToRegistry(PETAL_BASKET, "petal_basket");
		}
	}
}
