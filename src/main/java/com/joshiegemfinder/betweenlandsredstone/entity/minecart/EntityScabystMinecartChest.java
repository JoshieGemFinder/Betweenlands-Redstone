package com.joshiegemfinder.betweenlandsredstone.entity.minecart;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public class EntityScabystMinecartChest extends EntityScabystMinecartContainer {

    public EntityScabystMinecartChest(World worldIn)
    {
        super(worldIn);
    }

    public EntityScabystMinecartChest(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    public static void registerFixesMinecartChest(DataFixer fixer)
    {
        EntityMinecartContainer.addDataFixers(fixer, EntityMinecartChest.class);
    }

    public void killMinecart(DamageSource source)
    {
        super.killMinecart(source);

        if (this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            this.dropItemWithOffset(Item.getItemFromBlock(BlockRegistry.WEEDWOOD_CHEST), 1, 0.0F);
        }
    }

    public int getSizeInventory()
    {
        return 27;
    }

	@Override
	public Type getTrueType() {
		return Type.CHEST;
	}

    public IBlockState getDefaultDisplayTile()
    {
        return BlockRegistry.WEEDWOOD_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
    }

    public int getDefaultDisplayTileOffset()
    {
        return 8;
    }

    public String getGuiID()
    {
        return "minecraft:chest";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        this.addLoot(playerIn);
        return new ContainerChest(playerInventory, this, playerIn);
    }
}
