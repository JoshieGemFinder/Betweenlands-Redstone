package com.joshiegemfinder.betweenlandsredstone.proxy;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.RegistryHandlerClient;
import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityChestBetweenlandsTrapped;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.TileEntityScabystPiston;
import com.joshiegemfinder.betweenlandsredstone.entity.EntityScabystItemFrame;
import com.joshiegemfinder.betweenlandsredstone.entity.minecart.EntityScabystMinecart;
import com.joshiegemfinder.betweenlandsredstone.network.MinecartFacingMessage;
import com.joshiegemfinder.betweenlandsredstone.network.PlantTonicMessage;
import com.joshiegemfinder.betweenlandsredstone.renderer.RenderChestBetweenlandsTrapped;
import com.joshiegemfinder.betweenlandsredstone.renderer.RenderScabystItemFrame;
import com.joshiegemfinder.betweenlandsredstone.renderer.RenderScabystMinecart;
import com.joshiegemfinder.betweenlandsredstone.renderer.TileEntityScabystPistonRenderer;
import com.joshiegemfinder.betweenlandsredstone.util.ModelRegisterer;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.client.render.tile.RenderItemStackAsTileEntity;

public class ClientProxy implements IProxy {

	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}

	@Override
	public void preInit() {
		
		Main.NETWORK_CHANNEL.registerMessage(MinecartFacingMessage.MinecartFacingMessageHandler.class, MinecartFacingMessage.class, 0, Side.CLIENT);
		Main.NETWORK_CHANNEL.registerMessage(PlantTonicMessage.PlantTonicMessageHandler.class, PlantTonicMessage.class, 1, Side.CLIENT);
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityScabystPiston.class, new TileEntityScabystPistonRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChestBetweenlandsTrapped.class, new RenderChestBetweenlandsTrapped());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityScabystItemFrame.class, RenderScabystItemFrame.FACTORY);
		RenderingRegistry.registerEntityRenderingHandler(EntityScabystMinecart.class, RenderScabystMinecart.FACTORY);		
		
	}

	@Override
	public void init() {
		
	}

	@Override
	public void postInit() {
		ModItems.CHEST_TRAPPED.setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityChestBetweenlandsTrapped.class));
	}

    public void addItemToRegistry(Item item, String name, @Nullable ModelRegisterer registerModel) {
    	item.setUnlocalizedName(name);
    	item.setRegistryName(new ResourceLocation(Main.MODID, name));
		ModItems.ITEMS.add(item);
		
		item.setCreativeTab(CreativeTabs.REDSTONE);
		
		if(registerModel != null) {
			RegistryHandlerClient.modelRegisters.add(registerModel);
		}
	}

    public void addItemToRegistry(Item item, String name) {
    	addItemToRegistry(item, name, new ModelRegisterer() {
			@Override
			public void registerModels() {
				Main.proxy.registerItemRenderer(item, 0, "inventory");
			}
		});
	}
}
