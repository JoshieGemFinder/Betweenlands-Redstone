package com.joshiegemfinder.betweenlandsredstone;

import java.util.ArrayList;
import java.util.List;

import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;
import com.joshiegemfinder.betweenlandsredstone.util.ModelRegisterer;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystWireBlockColor;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
//@SuppressWarnings("deprecation")
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
