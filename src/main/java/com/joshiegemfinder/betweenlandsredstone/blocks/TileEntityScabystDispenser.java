package com.joshiegemfinder.betweenlandsredstone.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityScabystDispenser extends TileEntityDispenser {

	@Override
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.scabyst_dispenser";
    }
	
    public static void registerFixesScabyst(DataFixer fixer)
    {
        fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileEntityScabystDispenser.class, new String[] {"Items"}));
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
    	return oldState.getBlock() != newSate.getBlock();
    }
}