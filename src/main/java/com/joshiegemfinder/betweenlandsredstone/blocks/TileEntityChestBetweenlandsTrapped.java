package com.joshiegemfinder.betweenlandsredstone.blocks;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;

public class TileEntityChestBetweenlandsTrapped extends TileEntityChestBetweenlands {

	public static boolean isTrapped(BlockChest.Type type) {
		return type == BlockChest.Type.TRAP || type == BlockChestBetweenlandsTrapped.TRAPPED_WEEDWOOD_CHEST;
	}

    public void openInventory(EntityPlayer player)
    {
        if (!player.isSpectator())
        {
            if (this.numPlayersUsing < 0)
            {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);

            if (isTrapped(this.getChestType()))
            {
                this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
            }
        }
    }

    public void closeInventory(EntityPlayer player)
    {
        if (!player.isSpectator() && this.getBlockType() instanceof BlockChest)
        {
            --this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);

            if (isTrapped(this.getChestType()))
            {
                this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
            }
        }
    }
}
