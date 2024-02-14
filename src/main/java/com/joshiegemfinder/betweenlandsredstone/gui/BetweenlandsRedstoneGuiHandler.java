package com.joshiegemfinder.betweenlandsredstone.gui;

import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityCrafter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

// not putting this in the handlers package because it's a distinct seperate thing from them
public class BetweenlandsRedstoneGuiHandler implements IGuiHandler {
	
	public static final int GUI_CRAFTER_ID = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == GUI_CRAFTER_ID)
			return new ContainerCrafter(player.inventory, (TileEntityCrafter)world.getTileEntity(new BlockPos(x, y, z)));
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == GUI_CRAFTER_ID)
			return new GuiCrafter(player.inventory, (TileEntityCrafter)world.getTileEntity(new BlockPos(x, y, z)));
		return null;
	}

}
