package com.joshiegemfinder.betweenlandsredstone.util;

import com.joshiegemfinder.betweenlandsredstone.util.Discriminator.DiscriminatorContext;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ScabystWorldWrapper {

	//base methods
	
	public static int getPower(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        IBlockState iblockstate1 = world.getBlockState(pos);
        int power = Discriminator.canProvidePower(iblockstate1) ? Discriminator.getProvidedWeakPower(iblockstate1, world, pos, facing) : 0;
//        int power = Discriminator.getProvidedWeakPower(iblockstate1, world, pos, facing);
        // as far as i understand it, StrongPower is power passed through indirectly from blocks
        // shouldCheckWeakPower is deceptively named, since it tells it to check strong power or not
        // and will only check strong power if its a full block (power can be transmitted through it)
        return iblockstate1.getBlock().shouldCheckWeakPower(iblockstate1, world, pos, facing) ? Math.max(power, getStrongPower(world, pos)) : power;
    }

    public static int getStrongPower(IBlockAccess worldIn, BlockPos pos, EnumFacing direction)
    {
    	IBlockState state = worldIn.getBlockState(pos);
        return Discriminator.canProvidePower(state) ? Discriminator.getProvidedStrongPower(state, worldIn, pos, direction) : 0;
    }
	
	public static int getStrongPower(IBlockAccess worldIn, BlockPos pos) {
		int p = 0;
		for(EnumFacing facing : EnumFacing.VALUES) {
			p = Math.max(p, getStrongPower(worldIn, pos.offset(facing), facing));
			if(p >= 15) {
				return p;
			}
		}
		return p;
	}
	
	public static boolean isBlockPowered(IBlockAccess worldIn, BlockPos pos)
    {
		for(EnumFacing facing : EnumFacing.VALUES) {
			if (getPower(worldIn, pos.offset(facing), facing) > 0)
	        {
				return true;
	        }
		}
		return false;
    }
	
	public static boolean isSidePowered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return getPower(worldIn, pos, side) > 0;
    }
	
	public static int isBlockIndirectlyGettingPowered(IBlockAccess worldIn, BlockPos pos) {

        int power = 0;

        for (EnumFacing enumfacing : EnumFacing.values())
        {
            int newPower = getPower(worldIn, pos.offset(enumfacing), enumfacing);

            if (newPower >= 15)
            {
                return 15;
            }

            if (newPower > power)
            {
            	power = newPower;
            }
        }

        return power;
	}
	
	//scabyst-specific methods
	
	public static int getScabystPower(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
		Discriminator.pushContext(DiscriminatorContext.SCABYST);
		int power = getPower(world, pos, facing);
		Discriminator.popContext();
		return power;
    }

    public static int getStrongScabystPower(IBlockAccess worldIn, BlockPos pos, EnumFacing direction)
    {
		Discriminator.pushContext(DiscriminatorContext.SCABYST);
		int power = getStrongPower(worldIn, pos, direction);
		Discriminator.popContext();
		return power;
    }
	
	public static int getStrongScabystPower(IBlockAccess worldIn, BlockPos pos) {
		Discriminator.pushContext(DiscriminatorContext.SCABYST);
		int power = getStrongPower(worldIn, pos);
		Discriminator.popContext();
		return power;
	}
	
	public static boolean isBlockScabystPowered(IBlockAccess worldIn, BlockPos pos)
    {
		Discriminator.pushContext(DiscriminatorContext.SCABYST);
		boolean powered = isBlockPowered(worldIn, pos);
		Discriminator.popContext();
		return powered;
    }
	
	public static boolean isSideScabystPowered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
		Discriminator.pushContext(DiscriminatorContext.SCABYST);
		boolean powered = getPower(worldIn, pos, side) > 0;
		Discriminator.popContext();
		return powered;
    }
	
	public static int isBlockIndirectlyGettingScabystPowered(IBlockAccess worldIn, BlockPos pos) {
		Discriminator.pushContext(DiscriminatorContext.SCABYST);
		int power = isBlockIndirectlyGettingPowered(worldIn, pos);
		Discriminator.popContext();
		return power;
	}
	
	//redstone-specific methods
	
	public static int getRedstonePower(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
		Discriminator.pushContext(DiscriminatorContext.REDSTONE);
		int power = getPower(world, pos, facing);
		Discriminator.popContext();
		return power;
    }

    public static int getStrongRedstonePower(IBlockAccess worldIn, BlockPos pos, EnumFacing direction)
    {
		Discriminator.pushContext(DiscriminatorContext.REDSTONE);
		int power = getStrongPower(worldIn, pos, direction);
		Discriminator.popContext();
		return power;
    }
	
	public static int getStrongRedstonePower(IBlockAccess worldIn, BlockPos pos) {
		Discriminator.pushContext(DiscriminatorContext.REDSTONE);
		int power = getStrongPower(worldIn, pos);
		Discriminator.popContext();
		return power;
	}
	
	public static boolean isBlockRedstonePowered(IBlockAccess worldIn, BlockPos pos)
    {
		Discriminator.pushContext(DiscriminatorContext.REDSTONE);
		boolean powered = isBlockPowered(worldIn, pos);
		Discriminator.popContext();
		return powered;
    }
	
	public static boolean isSideRedstonePowered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
		Discriminator.pushContext(DiscriminatorContext.REDSTONE);
		boolean powered = getPower(worldIn, pos, side) > 0;
		Discriminator.popContext();
		return powered;
    }
	
	public static int isBlockIndirectlyGettingRedstonePowered(IBlockAccess worldIn, BlockPos pos) {
		Discriminator.pushContext(DiscriminatorContext.REDSTONE);
		int power = isBlockIndirectlyGettingPowered(worldIn, pos);
		Discriminator.popContext();
		return power;
	}
	
	//Methods for both
	
	public static int getBothPower(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
		Discriminator.pushContext(DiscriminatorContext.BOTH);
		int power = getPower(world, pos, facing);
		Discriminator.popContext();
		return power;
    }

    public static int getStrongBothePower(IBlockAccess worldIn, BlockPos pos, EnumFacing direction)
    {
		Discriminator.pushContext(DiscriminatorContext.BOTH);
		int power = getStrongPower(worldIn, pos, direction);
		Discriminator.popContext();
		return power;
    }
	
	public static int getStrongBothPower(IBlockAccess worldIn, BlockPos pos) {
		Discriminator.pushContext(DiscriminatorContext.BOTH);
		int power = getStrongPower(worldIn, pos);
		Discriminator.popContext();
		return power;
	}
	
	public static boolean isBlockBothPowered(IBlockAccess worldIn, BlockPos pos)
    {
		Discriminator.pushContext(DiscriminatorContext.BOTH);
		boolean powered = isBlockPowered(worldIn, pos);
		Discriminator.popContext();
		return powered;
    }
	
	public static boolean isSideBothPowered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
		Discriminator.pushContext(DiscriminatorContext.BOTH);
		boolean powered = getPower(worldIn, pos, side) > 0;
		Discriminator.popContext();
		return powered;
    }
	
	public static int isBlockIndirectlyGettingBothPowered(IBlockAccess worldIn, BlockPos pos) {
		Discriminator.pushContext(DiscriminatorContext.BOTH);
		int power = isBlockIndirectlyGettingPowered(worldIn, pos);
		Discriminator.popContext();
		return power;
	}
}
