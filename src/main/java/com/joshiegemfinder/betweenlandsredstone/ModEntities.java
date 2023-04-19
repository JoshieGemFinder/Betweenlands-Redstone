package com.joshiegemfinder.betweenlandsredstone;

import java.util.ArrayList;
import java.util.List;

import com.joshiegemfinder.betweenlandsredstone.entity.minecart.EntityScabystMinecartChest;
import com.joshiegemfinder.betweenlandsredstone.entity.minecart.EntityScabystMinecartEmpty;
import com.joshiegemfinder.betweenlandsredstone.entity.minecart.EntityScabystMinecartFurnace;
import com.joshiegemfinder.betweenlandsredstone.entity.minecart.EntityScabystMinecartHopper;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

public class ModEntities {

	public static final List<EntityEntry> ENTITIES = new ArrayList<EntityEntry>();
	
	private static int id = 0;

	public static EntityEntry SCABYST_MINECART_EMPTY;
	public static EntityEntry SCABYST_MINECART_CHEST;
	public static EntityEntry SCABYST_MINECART_HOPPER;
	public static EntityEntry SCABYST_MINECART_FURNACE;
	
	public static void init() {
		
		SCABYST_MINECART_EMPTY = EntityEntryBuilder.create()
				.entity(EntityScabystMinecartEmpty.class)
				.id(new ResourceLocation(Reference.MODID, "scabyst_minecart"), id)
				.name("scabyst_minecart")
				.tracker(80, 3, true)
				.build();
		id++;
		ENTITIES.add(SCABYST_MINECART_EMPTY);
		SCABYST_MINECART_CHEST = EntityEntryBuilder.create()
				.entity(EntityScabystMinecartChest.class)
				.id(new ResourceLocation(Reference.MODID, "scabyst_chest_minecart"), id)
				.name("scabyst_chest_minecart")
				.tracker(80, 3, true)
				.build();
		id++;
		ENTITIES.add(SCABYST_MINECART_CHEST);
		SCABYST_MINECART_HOPPER = EntityEntryBuilder.create()
				.entity(EntityScabystMinecartHopper.class)
				.id(new ResourceLocation(Reference.MODID, "scabyst_hopper_minecart"), id)
				.name("scabyst_hopper_minecart")
				.tracker(80, 3, true)
				.build();
		id++;
		ENTITIES.add(SCABYST_MINECART_HOPPER);
		SCABYST_MINECART_FURNACE = EntityEntryBuilder.create()
				.entity(EntityScabystMinecartFurnace.class)
				.id(new ResourceLocation(Reference.MODID, "scabyst_furnace_minecart"), id)
				.name("scabyst_furnace_minecart")
				.tracker(80, 3, true)
				.build();
		id++;
		ENTITIES.add(SCABYST_MINECART_FURNACE);
	}
}
