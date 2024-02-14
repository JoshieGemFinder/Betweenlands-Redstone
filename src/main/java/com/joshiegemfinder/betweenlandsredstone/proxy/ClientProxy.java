package com.joshiegemfinder.betweenlandsredstone.proxy;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.RegistryHandlerClient;
import com.joshiegemfinder.betweenlandsredstone.audio.AttenuatedSound;
import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityChestBetweenlandsTrapped;
import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityCrafter;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.TileEntityScabystPiston;
import com.joshiegemfinder.betweenlandsredstone.entity.minecart.EntityScabystMinecart;
import com.joshiegemfinder.betweenlandsredstone.renderer.entity.RenderScabystMinecart;
import com.joshiegemfinder.betweenlandsredstone.renderer.tile.RenderChestBetweenlandsTrapped;
import com.joshiegemfinder.betweenlandsredstone.renderer.tile.RenderCrafter;
import com.joshiegemfinder.betweenlandsredstone.renderer.tile.RenderScabystPiston;
import com.joshiegemfinder.betweenlandsredstone.util.ModelRegisterer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import thebetweenlands.client.render.tile.RenderItemStackAsTileEntity;

public class ClientProxy implements IProxy {

	@Override
	public void registerItemRenderer(Item item, int meta, ResourceLocation location, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(location, id));
	}

	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		this.registerItemRenderer(item, meta, item.getRegistryName(), id);
	}

	@Override
	public void preInit() {
		
//		BetweenlandsRedstone.NETWORK_CHANNEL.registerMessage(MinecartFacingMessage.MinecartFacingMessageHandler.class, MinecartFacingMessage.class, 0, Side.CLIENT);
//		BetweenlandsRedstone.NETWORK_CHANNEL.registerMessage(PlantTonicMessage.PlantTonicMessageHandler.class, PlantTonicMessage.class, 1, Side.CLIENT);
//		BetweenlandsRedstone.NETWORK_CHANNEL.registerMessage(PlayAttenuatedSoundMessage.PlayAttenuatedSoundMessageHandler.class, PlayAttenuatedSoundMessage.class, 2, Side.CLIENT);
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityScabystPiston.class, new RenderScabystPiston());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChestBetweenlandsTrapped.class, new RenderChestBetweenlandsTrapped());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrafter.class, new RenderCrafter());
		
//		RenderingRegistry.registerEntityRenderingHandler(EntityScabystItemFrame.class, RenderScabystItemFrame.FACTORY);
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
    	item.setRegistryName(new ResourceLocation(BetweenlandsRedstone.MODID, name));
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
				BetweenlandsRedstone.proxy.registerItemRenderer(item, 0, "inventory");
			}
		});
	}

	@Override
	public void playAttenuatedSound(float xPosF, float yPosF, float zPosF, SoundEvent soundEvent,
			SoundCategory category, float volume, float pitch, float attenuationDistance) {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.getRenderViewEntity().getDistance(xPosF, yPosF, zPosF) < attenuationDistance)
			mc.getSoundHandler().playSound(new AttenuatedSound(xPosF, yPosF, zPosF, soundEvent, category, volume, pitch, attenuationDistance));
		
	}
}
