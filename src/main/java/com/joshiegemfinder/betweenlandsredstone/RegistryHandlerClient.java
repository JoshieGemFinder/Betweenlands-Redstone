package com.joshiegemfinder.betweenlandsredstone;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;
import com.joshiegemfinder.betweenlandsredstone.util.ModelRegisterer;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystWireBlockColor;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.api.item.IFoodSicknessItem;
import thebetweenlands.common.handler.FoodSicknessHandler;
import thebetweenlands.common.item.food.ItemBLFood;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(Side.CLIENT)
@SuppressWarnings("deprecation")
public class RegistryHandlerClient {

    public static final List<ModelRegisterer> modelRegisters = new ArrayList<ModelRegisterer>();
    
    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(new ScabystWireBlockColor(), ModBlocks.SCABYST_WIRE);
    }

    
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
    	for(Item i : ModItems.ITEMS) {
    		if(i instanceof IModelInterface) {
    			((IModelInterface)i).registerModels();
    		}
    	}
    	for(Block b : ModBlocks.BLOCKS) {
    		if(b instanceof IModelInterface) {
    			((IModelInterface)b).registerModels();
    		}
    	}
    	for(ModelRegisterer r : modelRegisters) {
    		r.registerModels();
    	}
    }

    //old item frame stuff
    /*
    private static Method registerModel;

    private static Method getRegisterModel(ModelLoader loader) {
    	try {
	    	if(registerModel == null) {
	    		registerModel = ReflectionHelper.findMethod(loader.getClass().getSuperclass(), "registerVariant", "func_177569_a", ModelBlockDefinition.class, ModelResourceLocation.class);
	    	}
	    	registerModel.setAccessible(true);
	    	return registerModel;
    	} catch(Exception e) {
    		Main.logger.info("Error getting registerVariant for registryHandler");
    		e.printStackTrace();
    		throw new RuntimeException("Error getting registerVariant for registryHandler");
    	}
    }

    private static Method getBlockDefinition;

    private static Method getGetBlockDefinition(ModelLoader loader) {
    	try {
	    	if(getBlockDefinition == null) {
	    		getBlockDefinition = ReflectionHelper.findMethod(loader.getClass().getSuperclass(), "getModelBlockDefinition", "func_177586_a", ResourceLocation.class);
	    	}
	    	getBlockDefinition.setAccessible(true);
	    	return getBlockDefinition;
    	} catch(Exception e) {
    		Main.logger.info("Error getting getModelBlockDefinition for registryHandler, error type: {}", e.getClass());
    		e.printStackTrace();
    		throw new RuntimeException("Error getting getModelBlockDefinition for registryHandler", e);
    	}
    }

    private static IBakedModel bakeLocation(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, ModelResourceLocation location) {
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Failed loading model '" + location);
		IBakedModel bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK, (loc) -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString()));
		modelRegistry.putObject(location, bakedModel);
		return bakedModel;
	}
    
    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
    	ModelLoader loader = event.getModelLoader();
    	
    	try {
    		ModelBlockDefinition definition = (ModelBlockDefinition)getGetBlockDefinition(loader).invoke(loader, Reference.ITEM_FRAME_MODEL);

        	getRegisterModel(loader).invoke(loader, definition, Reference.ITEM_FRAME_MODEL_NORMAL);
        	getRegisterModel(loader).invoke(loader, definition, Reference.ITEM_FRAME_MODEL_MAP);
        	
        	IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();

        	bakeLocation(registry, Reference.ITEM_FRAME_MODEL_NORMAL);
        	bakeLocation(registry, Reference.ITEM_FRAME_MODEL_MAP);

    	} catch (Exception e) {
			e.printStackTrace();
        	Main.logger.info("Error initialising item frame model");
    	}
    }

	private static void stitchLocation(TextureStitchEvent.Pre event, ModelResourceLocation location) {
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Failed loading model '" + location);
		for(ResourceLocation texture : model.getTextures()) {
			event.getMap().registerSprite(texture);
		}
	}
	
	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event) {
		stitchLocation(event, Reference.ITEM_FRAME_MODEL_NORMAL);
		stitchLocation(event, Reference.ITEM_FRAME_MODEL_MAP);
	}*/

	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		
		ItemStack stack = event.getItemStack();
		List<String> tooltip = event.getToolTip();
		Entity entity = event.getEntity();
		EntityPlayer player = event.getEntityPlayer();
		
		if(stack.getItem() == ModItems.WHITE_PEAR_BLOCK) {
			tooltip.add("- Bet you couldn't fit that whole thing in your mouth!");
			tooltip.add("- ...You bet?");
			if(entity != null && entity.getEntityWorld() != null && player != null && FoodSicknessHandler.isFoodSicknessEnabled(entity.getEntityWorld()) && player.hasCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null)) {
				IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
				if(cap != null) {
					ItemBLFood item = (ItemBLFood)ItemRegistry.MIDDLE_FRUIT;
					ItemStack istack = new ItemStack(item);
					((IFoodSicknessItem)item).getSicknessTooltip(istack, cap.getSickness(item), cap.getFoodHatred(item), event.getFlags().isAdvanced(), tooltip);
				}
			}
		}
	}
}
