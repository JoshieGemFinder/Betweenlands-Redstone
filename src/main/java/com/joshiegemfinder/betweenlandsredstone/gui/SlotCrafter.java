package com.joshiegemfinder.betweenlandsredstone.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCrafter extends Slot {

//	private boolean disabled;
	
	public SlotCrafter(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		
//		this.disabled = disabled;
	}
	
//	public void setEnabled(boolean enabled) {
//		this.disabled = !enabled;
//	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		// TODO Auto-generated method stub
		return !isDisabled() && super.isItemValid(stack);
	}
	
	public boolean isDisabled() {
		return inventory.getField(this.slotNumber) != 0;
	}
	
//	@Override
//	public boolean isEnabled() {
//		return !this.isDisabled();
////		return inventory.getField(this.slotNumber) != 0;
////		return !this.disabled;
//	}
}
