package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;

import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockScabystObserver extends BlockObserver {

	public BlockScabystObserver(String name) {
		super();

		this.setHardness(3.0F);
		
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(BetweenlandsRedstone.MODID, name));
		if(this.getDefaultState().getBlock() != this) {
			this.setDefaultState(this.getBlockState().getBaseState().withProperty(FACING, EnumFacing.SOUTH).withProperty(POWERED, Boolean.valueOf(false)));
		}
		ModBlocks.BLOCKS.add(this);
	}

	@Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
		super.updateTick(worldIn, pos, state, rand);
		
		// Neighbour updates could've changed the block
		IBlockState newState = worldIn.getBlockState(pos);
		if(newState.getBlock() == this)
			worldIn.addBlockEvent(pos, this, 93, newState.getValue(POWERED).booleanValue() ? 1 : 0);
    }
	
	// HACK: For no apparent reason, scabyst observers won't render updates on dedicated servers, so I had to hack together an event to manually inform the client.
	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		if(id == 93) {
			if(!worldIn.isRemote) return true;
			worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(POWERED, param != 0), 2);
			return true;
		}
		return super.eventReceived(state, worldIn, pos, id, param);
	}
}
