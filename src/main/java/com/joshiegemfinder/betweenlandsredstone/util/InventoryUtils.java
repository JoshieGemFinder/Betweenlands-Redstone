package com.joshiegemfinder.betweenlandsredstone.util;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class InventoryUtils {
	
	public static IInventory getInventoryAt(World world, BlockPos pos) {
        net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        
        if(block instanceof ISidedInventoryProvider) {
        	return ((ISidedInventoryProvider) block).getContainer(world, pos, state);
        }
        
        if (block.hasTileEntity(state))
        {
            TileEntity tileentity = world.getTileEntity(pos);
            
            if (tileentity instanceof IInventory)
            {
                if (tileentity instanceof TileEntityChest && block instanceof BlockChest)
                {
                    return ((BlockChest) block).getContainer(world, pos, true);
                }
                
                return (IInventory)tileentity;
            }
        } else {
        	double x = pos.getX();
        	double y = pos.getY();
        	double z = pos.getZ();
            List<Entity> list = world.getEntitiesInAABBexcluding((Entity)null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntitySelectors.HAS_INVENTORY);

            if (!list.isEmpty())
            {
                return (IInventory)list.get(world.rand.nextInt(list.size()));
            }
        }

        return null;
	}
	
	public static IItemHandler getItemHandler(World world, BlockPos pos, EnumFacing side) {
		Pair<IItemHandler, Object> pair = VanillaInventoryCodeHooks.getItemHandler(world, pos.getX(), pos.getY(), pos.getZ(), side);
		if(pair != null && pair.getLeft() != null) {
			return pair.getLeft();
		}
		
		IInventory inventory = getInventoryAt(world, pos);
		if(inventory != null) {
				return inventory instanceof ISidedInventory ? new SidedInvWrapper((ISidedInventory) inventory, side) : new InvWrapper(inventory);
		}
		
		return null;
	}
	
    public static ItemStack insertStacks(World world, BlockPos pos, EnumFacing side, ItemStack stack, boolean simulate) {
    	IItemHandler inventory = getItemHandler(world, pos, side);
    	return inventory != null ? ItemHandlerHelper.insertItemStacked(inventory, stack, simulate) : stack;
    }
	
    /**
     * Tries to insert an item into any available inventories, and drops it on the ground if it fails
     * @param world
     * @param pos
     * @param side
     * @param stack
     * @param dispenseSpeed
     * @return Whether or not the item was dispensed
     */
	public static boolean ejectItem(World world, BlockPos pos, EnumFacing side, ItemStack stack, int dispenseSpeed) {
		stack = insertStacks(world, pos.offset(side), side, stack, false);
		if(!stack.isEmpty()) {
			IPosition dispensePosition = BlockDispenser.getDispensePosition(new BlockSourceImpl(world, pos));
			BehaviorDefaultDispenseItem.doDispense(world, stack, dispenseSpeed, side, dispensePosition);
			return true;
		}
		return false;
	}
	
}
