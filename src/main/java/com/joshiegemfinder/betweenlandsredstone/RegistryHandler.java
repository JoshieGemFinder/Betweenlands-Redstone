package com.joshiegemfinder.betweenlandsredstone;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

@EventBusSubscriber
public class RegistryHandler {

    @SubscribeEvent
    public static void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
    	BetweenlandsRedstone.logger.info("Registering Sounds!");
    	event.getRegistry().registerAll(ModSounds.SOUNDS.toArray(new SoundEvent[0]));
    }
    
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
    	BetweenlandsRedstone.logger.info("Registering Items!");
    	event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
    }
    
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
    	BetweenlandsRedstone.logger.info("Registering Blocks!");
    	event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityEntry> event) {
    	BetweenlandsRedstone.logger.info("Registering Entities!");
    	event.getRegistry().registerAll(ModEntities.ENTITIES.toArray(new EntityEntry[0]));
    }
}
