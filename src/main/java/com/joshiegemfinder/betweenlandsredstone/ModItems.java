package com.joshiegemfinder.betweenlandsredstone;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.entity.minecart.EntityScabystMinecart;
import com.joshiegemfinder.betweenlandsredstone.items.ItemBlockBase;
import com.joshiegemfinder.betweenlandsredstone.items.ItemFoodBlock;
import com.joshiegemfinder.betweenlandsredstone.items.ItemScabystDust;
import com.joshiegemfinder.betweenlandsredstone.items.ItemScabystMinecart;
import com.joshiegemfinder.betweenlandsredstone.util.ModelRegisterer;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockSpecial;
import thebetweenlands.common.registries.ItemRegistry;

public class ModItems {

	public static final List<Item> ITEMS = new ArrayList<Item>();
	
    public static Item SCABYST_DUST;
    public static Item SCABYST_BLOCK;
    public static Item SCABYST_TORCH;
    public static Item SCABYST_REPEATER;
    public static Item SCABYST_COMPARATOR;
    public static Item SCABYST_LAMP;
    public static Item SCABYST_PISTON;
    public static Item SCABYST_STICKY_PISTON;

    public static Item SCABYST_SLIME_1;
    public static Item SCABYST_SLIME_2;

    public static Item SCABYST_OBSERVER;

    public static Item SCABYST_RAIL;
    public static Item SCABYST_POWERED_RAIL;
    public static Item SCABYST_ACTIVATOR_RAIL;
    public static Item SCABYST_DETECTOR_RAIL;

    public static Item SCABYST_MINECART;
    public static Item SCABYST_CHEST_MINECART;
    public static Item SCABYST_HOPPER_MINECART;
    public static Item SCABYST_FURNACE_MINECART;
    public static Item SCABYST_DUAL_FURNACE_MINECART;

    public static Item SCABYST_DISPENSER;
    public static Item SCABYST_DROPPER;

    public static Item RIFT_DETECTOR;

    public static Item TRIPWIRE_HOOK;
    public static Item TRIPWIRE;

    public static Item CHEST_TRAPPED;

    public static Item TARGET_BLOCK;

    public static Item POLISHED_PITSONE;
    public static Item CUT_PITSONE;
    public static Item WHITE_PEAR_BLOCK;

    public static Item SYRMORITE_BARS;

    public static void addItemToRegistry(Item item, String name, @Nullable ModelRegisterer registerModel) {
    	BetweenlandsRedstone.proxy.addItemToRegistry(item, name, registerModel);
	}

    public static void addItemToRegistry(Item item, String name) {
    	BetweenlandsRedstone.proxy.addItemToRegistry(item, name);
	}
    
    public static void init() {
    	SCABYST_DUST = new ItemScabystDust("scabyst_dust").setCreativeTab(CreativeTabs.REDSTONE);
    	SCABYST_BLOCK = new ItemBlockBase(ModBlocks.SCABYST_BLOCK, "scabyst_block");
    	SCABYST_TORCH = new ItemBlockBase(ModBlocks.SCABYST_TORCH, "scabyst_torch");
    	SCABYST_REPEATER = new ItemBlockBase(ModBlocks.UNPOWERED_SCABYST_REPEATER, "scabyst_repeater");
    	SCABYST_COMPARATOR = new ItemBlockBase(ModBlocks.UNPOWERED_SCABYST_COMPARATOR, "scabyst_comparator");
    	SCABYST_LAMP = new ItemBlockBase(ModBlocks.SCABYST_LAMP, "scabyst_lamp");
    	SCABYST_PISTON = new ItemBlockBase(ModBlocks.SCABYST_PISTON, "scabyst_piston");
    	SCABYST_STICKY_PISTON = new ItemBlockBase(ModBlocks.SCABYST_STICKY_PISTON, "scabyst_sticky_piston");
    	SCABYST_SLIME_1 = new ItemBlockBase(ModBlocks.SCABYST_SLIME_1, "scabyst_slime_1");
    	SCABYST_SLIME_2 = new ItemBlockBase(ModBlocks.SCABYST_SLIME_2, "scabyst_slime_2");
    	SCABYST_OBSERVER = new ItemBlockBase(ModBlocks.SCABYST_OBSERVER, "scabyst_observer");

    	SCABYST_RAIL = new ItemBlockBase(ModBlocks.SCABYST_RAIL, "scabyst_rail");
    	SCABYST_POWERED_RAIL = new ItemBlockBase(ModBlocks.SCABYST_POWERED_RAIL, "scabyst_golden_rail");
    	SCABYST_ACTIVATOR_RAIL = new ItemBlockBase(ModBlocks.SCABYST_ACTIVATOR_RAIL, "scabyst_activator_rail");
    	SCABYST_DETECTOR_RAIL = new ItemBlockBase(ModBlocks.SCABYST_DETECTOR_RAIL, "scabyst_detector_rail");

    	SCABYST_MINECART = new ItemScabystMinecart("scabyst_minecart", EntityScabystMinecart.Type.RIDEABLE);
    	SCABYST_MINECART.setCreativeTab(CreativeTabs.TRANSPORTATION);
    	SCABYST_CHEST_MINECART = new ItemScabystMinecart("scabyst_chest_minecart", EntityScabystMinecart.Type.CHEST);
    	SCABYST_CHEST_MINECART.setCreativeTab(CreativeTabs.TRANSPORTATION);
    	SCABYST_HOPPER_MINECART = new ItemScabystMinecart("scabyst_hopper_minecart", EntityScabystMinecart.Type.HOPPER);
    	SCABYST_HOPPER_MINECART.setCreativeTab(CreativeTabs.TRANSPORTATION);
    	SCABYST_FURNACE_MINECART = new ItemScabystMinecart("scabyst_furnace_minecart", EntityScabystMinecart.Type.FURNACE);
    	SCABYST_FURNACE_MINECART.setCreativeTab(CreativeTabs.TRANSPORTATION);
    	SCABYST_DUAL_FURNACE_MINECART = new ItemScabystMinecart("scabyst_furnace_minecart_dual", EntityScabystMinecart.Type.DUAL_FURNACE);
    	SCABYST_DUAL_FURNACE_MINECART.setCreativeTab(CreativeTabs.TRANSPORTATION);

    	SCABYST_DISPENSER = new ItemBlockBase(ModBlocks.SCABYST_DISPENSER, "scabyst_dispenser");
    	SCABYST_DROPPER = new ItemBlockBase(ModBlocks.SCABYST_DROPPER, "scabyst_dropper");

    	RIFT_DETECTOR = new ItemBlockBase(ModBlocks.RIFT_DETECTOR, "rift_detector");
    	
    	TRIPWIRE_HOOK = new ItemBlockBase(ModBlocks.TRIPWIRE_HOOK, "scabyst_tripwire_hook");
    	TRIPWIRE = new ItemBlockSpecial(ModBlocks.TRIPWIRE);
    	addItemToRegistry(TRIPWIRE, "scabyst_tripwire");
    	
    	CHEST_TRAPPED = new ItemBlockBase(ModBlocks.CHEST_TRAPPED, "weedwood_chest_trapped");
    	
    	TARGET_BLOCK = new ItemBlockBase(ModBlocks.TARGET_BLOCK, "target_block");
    	
    	POLISHED_PITSONE = new ItemBlockBase(ModBlocks.POLISHED_PITSTONE, "polished_pitstone");
    	CUT_PITSONE = new ItemBlockBase(ModBlocks.CUT_PITSTONE, "cut_pitstone");
		
		if(BLRedstoneConfig.EXTRA_FEATURES.registerWhitePearBlock) {
	    	WHITE_PEAR_BLOCK = new ItemFoodBlock(ModBlocks.WHITE_PEAR_BLOCK, "white_pear_block", 54, 5.4F, false, true, ItemRegistry.MIDDLE_FRUIT, 45);
		}
		
		if(BLRedstoneConfig.EXTRA_FEATURES.registerSyrmoriteBars) {
	    	SYRMORITE_BARS = new ItemBlockBase(ModBlocks.SYRMORITE_BARS, "syrmorite_bars");
		}
    }
    
}
