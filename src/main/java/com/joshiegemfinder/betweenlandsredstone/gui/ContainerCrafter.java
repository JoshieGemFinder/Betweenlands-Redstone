package com.joshiegemfinder.betweenlandsredstone.gui;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityCrafter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCrafter extends Container {

	private final TileEntityCrafter tileentitycrafter;
	private final boolean[] disabledSlots;

	//slot indexes (which position in the slot array they were inserted, not which slot of the inventory they stand for); inclusive
	private final int CRAFTER_INVENTORY_BEGIN = 0;
	private final int CRAFTER_INVENTORY_END = CRAFTER_INVENTORY_BEGIN + 9 - 1;
	
	private final int OUTPUT_INVENTORY = CRAFTER_INVENTORY_END + 1;
	
	private final int PLAYER_INVENTORY_BEGIN = OUTPUT_INVENTORY + 1;
	private final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_BEGIN + 36 - 1;
	
	public ContainerCrafter(InventoryPlayer player, TileEntityCrafter tile) {
		this.tileentitycrafter = tile;
		this.disabledSlots = tile.disabledSlots.clone();

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				this.addSlotToContainer(new SlotCrafter(tile, x + y * 3, 26 + x * 18, 17 + y * 18));
			}
		}
		
		this.addSlotToContainer(new SlotCrafterResult(tile.outputInventory, 0, 134, 35));
		
		for(int x = 0; x < 9; ++x) { //hotbar
			this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 142));
		}
		
		for(int y = 0; y < 3; ++y) { //inventory
			for(int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(player, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}
		
		this.tileentitycrafter.onCraftMatrixChanged();
	}
	
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if(slotId >= CRAFTER_INVENTORY_BEGIN && slotId <= CRAFTER_INVENTORY_END && (clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && 
				player.inventory.getItemStack().isEmpty() && this.inventorySlots.get(slotId).getStack().isEmpty()) {
			this.tileentitycrafter.setSlotDisabled(slotId, !this.tileentitycrafter.disabledSlots[slotId]);
			this.detectAndSendChanges();
			return ItemStack.EMPTY;
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}
	
	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listener.sendAllWindowProperties(this, this.tileentitycrafter);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		for(IContainerListener listener : this.listeners) {
			for(int i = 0; i < 9; ++i) {
				if(this.disabledSlots[i] != this.tileentitycrafter.disabledSlots[i]) {
					listener.sendWindowProperty(this, i, this.tileentitycrafter.getField(i));
				}
			}
		}
		
		for(int i = 0; i < 9; ++i) {
			this.disabledSlots[i] = this.tileentitycrafter.disabledSlots[i];
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.tileentitycrafter.isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex)
	{
		Slot sourceSlot = (Slot)inventorySlots.get(sourceSlotIndex);
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;  //EMPTY_ITEM
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// One of the vanilla inventory slots?
		if (sourceSlotIndex >= PLAYER_INVENTORY_BEGIN && sourceSlotIndex <= PLAYER_INVENTORY_END) {
			// Try to merge into the tile entity
			if (!mergeItemStack(sourceStack, CRAFTER_INVENTORY_BEGIN, CRAFTER_INVENTORY_END + 1, false)){
				return ItemStack.EMPTY;
			}
		} 
		// One of the tile entity slots?
		else if (sourceSlotIndex >= CRAFTER_INVENTORY_BEGIN && sourceSlotIndex <= CRAFTER_INVENTORY_END) {
			// Try to merge into the player's inventory
			if (!mergeItemStack(sourceStack, PLAYER_INVENTORY_BEGIN, PLAYER_INVENTORY_END + 1, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			BetweenlandsRedstone.logger.error("Invalid slot index: {}", sourceSlotIndex);
			return ItemStack.EMPTY;
		}

		// If the entire stack was moved
		if (sourceStack.getCount() == 0) {
			sourceSlot.putStack(ItemStack.EMPTY);
		} else {
			sourceSlot.onSlotChanged();
		}

		sourceSlot.onTake(player, sourceStack);
		return copyOfSourceStack;
	}

}
