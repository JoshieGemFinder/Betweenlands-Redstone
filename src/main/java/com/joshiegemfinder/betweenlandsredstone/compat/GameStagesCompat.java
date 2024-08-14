package com.joshiegemfinder.betweenlandsredstone.compat;

import java.lang.reflect.Field;

import com.joshiegemfinder.betweenlandsredstone.BLRedstoneConfig;
import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityCrafter;

import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;

public class GameStagesCompat {


	private static Field recipeField = null;
	private static Field tierField = null;
	
	protected static boolean canBypass(IRecipe recipe, TileEntityCrafter tile) {
		if(BLRedstoneConfig.COMPATIBILITY.crafterBypassesStages)
			return true;
		
		if(!recipe.getClass().getTypeName().equals("com.blamejared.recipestages.recipes.RecipeStage"))
			return false;
		
		try {
			if(tierField == null) {
					tierField = recipe.getClass().getDeclaredField("tier");
			}
			tierField.setAccessible(true);
			
			String tier = (String) tierField.get(recipe);
	
			for(int i = 0; i < BLRedstoneConfig.COMPATIBILITY.crafterStages.length; ++i) {
				if(tier.equalsIgnoreCase(BLRedstoneConfig.COMPATIBILITY.crafterStages[i])) {
					return true;
				}
			}
			
			if(BLRedstoneConfig.COMPATIBILITY.crafterUsesPlayerStages && tile.crafterStages.contains(tier))
				return true;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// no-op
		}
		return false;
	}
	
	protected static IRecipe tryToBypassRecipeStagesIfInstalledAndAlsoConfigOptionIsEnabled(IRecipe recipe, TileEntityCrafter tile) {
		if(!(BLRedstoneConfig.COMPATIBILITY.crafterBypassesStages || BLRedstoneConfig.COMPATIBILITY.crafterUsesPlayerStages || BLRedstoneConfig.COMPATIBILITY.crafterStages.length > 0) || !Loader.isModLoaded("recipestages")) {
			return null;
		}
		
		if(recipe.getClass().getTypeName().equals("com.blamejared.recipestages.recipes.RecipeStage")) {
			try {
				if(recipeField == null) {
						recipeField = recipe.getClass().getDeclaredField("recipe");
				}
				recipeField.setAccessible(true);
				do {
					if(canBypass(recipe, tile)) {
						recipe = (IRecipe) recipeField.get(recipe);
					} else {
						return null;
					}
				}
				while(recipe.getClass().getTypeName().equals("com.blamejared.recipestages.recipes.RecipeStage"));
				
				return recipe;
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException  e) {
				return null;
			}
		}
		
		return null;
	}

	@Method(modid = "gamestages")
	public static IRecipe getCrafterRecipeBypass(IRecipe recipe, TileEntityCrafter tile) {
		IRecipe recipe2 = tryToBypassRecipeStagesIfInstalledAndAlsoConfigOptionIsEnabled(recipe, tile);
		return recipe2;
	}

	@Method(modid = "gamestages")
	public static void addCrafterStages(TileEntityCrafter tile, EntityPlayer player) {
		try {
			IStageData data = GameStageHelper.getPlayerData(player);
			boolean changed = tile.crafterStages.addAll(data.getStages());
			// Recalculate the recipe if new stages just got added
			if(changed)
				tile.onCraftMatrixChanged();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
