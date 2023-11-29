package com.joshiegemfinder.betweenlandsredstone.blocks.diode;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockScabystDiode {

    public static int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(BlockRedstoneDiode.FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        int i = worldIn.getRedstonePower(blockpos, enumfacing);

        if (i >= 15)
        {
            return i;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            int power = (block == Blocks.REDSTONE_WIRE || block == ModBlocks.SCABYST_WIRE) ? ((Integer)iblockstate.getValue(BlockRedstoneWire.POWER)).intValue() : 0;
            return Math.max(i, power);
        }
    }

    public static int getPowerOnSide(BlockRedstoneDiode _this, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (_this.isAlternateInput(iblockstate))
        {
            if (
            		block == Blocks.REDSTONE_BLOCK ||
            		block == ModBlocks.SCABYST_BLOCK
            	)
            {
                return 15;
            }
            else
            {
                return (
                		block == Blocks.REDSTONE_WIRE ||
                		block == ModBlocks.SCABYST_WIRE
            		) ? ((Integer)iblockstate.getValue(BlockRedstoneWire.POWER)).intValue() : worldIn.getStrongPower(pos, side);
            }
        }
        else
        {
            return 0;
        }
    }
    
    public static boolean isDiode(IBlockState state)
    {
        return 	Blocks.UNPOWERED_REPEATER.isSameDiode(state) ||
        		Blocks.UNPOWERED_COMPARATOR.isSameDiode(state) ||
        		ModBlocks.UNPOWERED_SCABYST_REPEATER.isSameDiode(state) ||
        		ModBlocks.UNPOWERED_SCABYST_COMPARATOR.isSameDiode(state);
    }
}
