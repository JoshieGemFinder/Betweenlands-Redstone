package com.joshiegemfinder.betweenlandsredstone.gui;

import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityCrafter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryCrafterResult implements IInventory {

	private final TileEntityCrafter tile;
	
	public InventoryCrafterResult(TileEntityCrafter tile) {
		this.tile = tile;
	}
	
	@Override
	public String getName() {
		return "tile.crafter.name";
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(this.getName());
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void clear() {}

	@Override
	public void closeInventory(EntityPlayer arg0) {}

	@Override
	public void openInventory(EntityPlayer arg0) {}

	@Override
	public ItemStack decrStackSize(int arg0, int arg1) {
		return null;
	}

	@Override
	public int getField(int arg0) {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int arg0) {
		return tile.resultStack;
	}

	@Override
	public boolean isEmpty() {
		return tile.resultStack.isEmpty();
	}

	@Override
	public boolean isItemValidForSlot(int arg0, ItemStack arg1) {
		return false;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer arg0) {
		return false;
	}

	@Override
	public void markDirty() {}

	@Override
	public ItemStack removeStackFromSlot(int arg0) {
		return null;
	}

	@Override
	public void setField(int arg0, int arg1) {}

	@Override
	public void setInventorySlotContents(int arg0, ItemStack arg1) {}

}
