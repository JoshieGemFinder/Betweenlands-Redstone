package com.joshiegemfinder.betweenlandsredstone.gui;

import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityCrafter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

//i'm just removing all of the base code here
public class InventoryCrafter extends InventoryCrafting {

    private final NonNullList<ItemStack> stackList;
	private final TileEntityCrafter crafter;
	
	public InventoryCrafter(TileEntityCrafter tile) {
		super(null, 3, 3);
		this.stackList = tile.slots;
		this.crafter = tile;
	}

	@Override
    public int getSizeInventory()
    {
        return this.stackList.size();
    }

	@Override
    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.stackList)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

	@Override
    public ItemStack getStackInSlot(int index)
    {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : (ItemStack)this.stackList.get(index);
    }
	
	@Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.stackList, index);
    }

	@Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.stackList, index, count);

        if (!itemstack.isEmpty())
        {
            this.crafter.onCraftMatrixChanged();
        }

        return itemstack;
    }

	@Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.stackList.set(index, stack);
        this.crafter.onCraftMatrixChanged();
    }

	@Override
    public void clear()
    {
        this.stackList.clear();
        this.crafter.onCraftMatrixChanged();
    }

	@Override //thank you BL devs
	public void markDirty() {
		this.crafter.markDirty();
		IBlockState state = this.crafter.getWorld().getBlockState(this.crafter.getPos());
		this.crafter.getWorld().notifyBlockUpdate(this.crafter.getPos(), state, state, 3);
	}
    

	@Override
    public void fillStackedContents(RecipeItemHelper helper)
    {
        for (ItemStack itemstack : this.stackList)
        {
            helper.accountStack(itemstack);
        }
    }

	@Override
	public boolean isItemValidForSlot(int arg0, ItemStack arg1) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		super.openInventory(player);

		this.crafter.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		super.closeInventory(player);

		this.crafter.closeInventory(player);
	}
	
	@Override
	public int getField(int p_174887_1_) {
		return this.crafter.getField(p_174887_1_);
	}

	@Override
	public void setField(int p_174885_1_, int p_174885_2_) {
		this.crafter.setField(p_174885_1_, p_174885_2_);
	}

	@Override
	public int getFieldCount() {
		return this.crafter.getFieldCount();
	}
}
