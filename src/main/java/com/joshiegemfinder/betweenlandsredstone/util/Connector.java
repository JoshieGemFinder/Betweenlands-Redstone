package com.joshiegemfinder.betweenlandsredstone.util;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class Connector {

	public static boolean canScabystConnect(IBlockAccess access, BlockPos pos, EnumFacing side) {
		return access == null || pos == null || side == null || access.getBlockState(pos.offset(side.getOpposite())).getBlock() != Blocks.REDSTONE_WIRE;
	}
	
	// no i don't
//	// gotta bring this old code back to fix target blocks
//    public static int getPower(World worldIn, BlockPos pos, EnumFacing facing)
//    {
//        IBlockState iblockstate1 = worldIn.getBlockState(pos);
//        int power = iblockstate1.getWeakPower(worldIn, pos, facing);
//        // as far as i understand it, strong power is power passed through indirectly from blocks
//        // shouldCheckWeakPower is deceptively named, since it tells it to check strong power or not
//        // and will only check strong power if its a full block (power can be transmitted through it)
//        return iblockstate1.getBlock().shouldCheckWeakPower(iblockstate1, worldIn, pos, facing) ? Math.max(worldIn.getStrongPower(pos), power) : power;
//    }
//    
//    public static int isBlockIndirectlyGettingPowered(World worldIn, BlockPos pos) {
//
//        int power = 0;
//
//        for (EnumFacing enumfacing : EnumFacing.values())
//        {
//            int newPower = getPower(worldIn, pos.offset(enumfacing), enumfacing);
//
//            if (newPower >= 15)
//            {
//                return 15;
//            }
//
//            if (newPower > power)
//            {
//            	power = newPower;
//            }
//        }
//
//        return power;
//	}
}
