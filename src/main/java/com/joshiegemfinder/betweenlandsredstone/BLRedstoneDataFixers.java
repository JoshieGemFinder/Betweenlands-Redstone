package com.joshiegemfinder.betweenlandsredstone;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import thebetweenlands.common.lib.ModInfo;

public class BLRedstoneDataFixers {

	private static final ResourceLocation OLD_LOCATION = new ResourceLocation(Main.MODID, "scabyst_item_frame");
	private static final String OLD_ID = OLD_LOCATION.toString();
	private static final String NEW_ID = ModInfo.ID + ":item_frame";
	
	public static void registerDataFixers(CompoundDataFixer dataFixer) {
		
		ModFixs fixer = dataFixer.init(Main.MODID, 2);
		
		fixer.registerFix(FixTypes.ENTITY, new IFixableData() {
			
			@Override
			public int getFixVersion() {
				return 1;
			}

			@Override
			public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
				String id = compound.getString("id");
//				System.out.println("Entity NBT id: " + id);
				if(id.equalsIgnoreCase(OLD_ID)) {
//					System.out.println("Valid Entity NBT!");
					
					compound.setString("id", NEW_ID);
					
					compound.setInteger("DyeColor", 7);
					
					compound.setByte("REAL_FACING_DIRECTION", (byte)(	EnumFacing.getHorizontal(compound.getByte("Facing")).getIndex()	));
					compound.setBoolean("IS_INVISIBLE", false);
					compound.setBoolean("IS_GLOWING", false);
				}
				return compound;
			}
			
		});
		fixer.registerFix(FixTypes.ITEM_INSTANCE, new IFixableData() {
			@Override
			public int getFixVersion() {
				return 1;
			}

			@Override
			public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
				String id = compound.getString("id");
//				System.out.println("Item NBT id: " + id);
				if(id.equalsIgnoreCase(OLD_ID)) {
//					System.out.println("Valid Item NBT!");
					compound.setString("id", NEW_ID);
					compound.setShort("Damage", (short)7);
				}
				return compound;
			}
		});
	}

	@SubscribeEvent
	public static void itemMappings(MissingMappings<Item> e) {
		for(Mapping<Item> map : e.getMappings()) {
			if(map.key.equals(OLD_LOCATION)) {
				map.ignore();
			}
		}
	}

	@SubscribeEvent
	public static void entityMappings(MissingMappings<EntityEntry> e) {
		for(Mapping<EntityEntry> map : e.getMappings()) {
			if(map.key.equals(OLD_LOCATION)) {
				map.ignore();
			}
		}
	}
}
