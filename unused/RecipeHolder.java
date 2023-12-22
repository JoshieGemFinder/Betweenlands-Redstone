package com.joshiegemfinder.betweenlandsredstone.util;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;

public class RecipeHolder {

	public static enum RecipeType {
		SHAPELESS,
		SHAPED
	}
	
	private RecipeType recipeType = RecipeType.SHAPELESS;
	
	@Nonnull
	private Object[] params = {};
	
	@Nonnull
	private ItemStack result = ItemStack.EMPTY;
	
	@Nonnull
	private ResourceLocation name = new ResourceLocation("");
	
	private ResourceLocation group = new ResourceLocation("");
	
	public RecipeHolder(RecipeType type, ResourceLocation name, ResourceLocation group, ItemStack result, Object[] params) {
		recipeType = type;
		this.name = name;
		this.group = group;
		this.result = result;
		this.params = params;
	}

	public RecipeType getType() { return recipeType; }
	public ResourceLocation getName() { return name; }
	public ResourceLocation getGroup() { return group; }
	public ItemStack getResult() { return result; }
	
	public IRecipe parse() {
		switch(recipeType) {
			case SHAPELESS:
				NonNullList<Ingredient> lst = NonNullList.create();
		        for (Ingredient i : (Ingredient[])params)
		            lst.add(i);
		        return new ShapelessRecipes(group == null ? "" : group.toString(), result, lst).setRegistryName(name);
			case SHAPED:
			default:
				ShapedPrimer primer = CraftingHelper.parseShaped(params);
				return new ShapedRecipes(group == null ? "" : group.toString(), primer.width, primer.height, primer.input, result).setRegistryName(name);
		}
	}
}
