package com.joshiegemfinder.betweenlandsredstone.blocks.piston;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;

import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockScabystPistonMoving extends BlockPistonMoving {
	
    public static final PropertyDirection FACING = BlockScabystPistonExtension.FACING;
    public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockScabystPistonExtension.TYPE;

    public BlockScabystPistonMoving(String name)
    {
        super();

		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
    }

    @Nullable
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return null;
    }

    public static TileEntity createTilePiston(IBlockState blockStateIn, EnumFacing facingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn)
    {
        return new TileEntityScabystPiston(blockStateIn, facingIn, extendingIn, shouldHeadBeRenderedIn);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityScabystPiston)
        {
            ((TileEntityScabystPiston)tileentity).clearPistonTileEntity();
        }
        else
        {
            super.breakBlock(worldIn, pos, state);
        }
    }

    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
        BlockPos blockpos = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() instanceof BlockScabystPistonBase && ((Boolean)iblockstate.getValue(BlockScabystPistonBase.EXTENDED)).booleanValue())
        {
            worldIn.setBlockToAir(blockpos);
        } else {
        	super.onBlockDestroyedByPlayer(worldIn, blockpos, iblockstate);
        }
    }
}
