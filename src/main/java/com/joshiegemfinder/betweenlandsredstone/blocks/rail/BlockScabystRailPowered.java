package com.joshiegemfinder.betweenlandsredstone.blocks.rail;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityStalker;

@SuppressWarnings("deprecation")
public class BlockScabystRailPowered extends BlockRailPowered {

	protected final boolean isActivator;
	
	//Vanilla is dumb and stupid and thats why there's two constructors
	public BlockScabystRailPowered(String name) {
		this(name, false);
	}

	public BlockScabystRailPowered(String name, boolean isActivator) {
		super(isActivator);
		this.isActivator = isActivator;
		this.setHardness(0.7F);
		this.setSoundType(SoundType.METAL);

		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
	}
	
	protected boolean getIsActivator() {
		return this.isActivator;
	}
	
	@Override
	public int getLightValue(IBlockState state) {
		if(state.getValue(POWERED) && !this.isActivator) {
			return 4;
		}
		return super.getLightValue(state);
	}

	//stop stalkers munching on blocks
	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		if(entity instanceof EntityStalker) {
			return false;
		}
		return super.canEntityDestroy(state, world, pos, entity);
	}
	
    protected boolean isSameRailWithPower(World worldIn, BlockPos pos, boolean p_176567_3_, int distance, BlockRailBase.EnumRailDirection p_176567_5_)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (!(iblockstate.getBlock() instanceof BlockScabystRailPowered) || this.isActivator != ((BlockScabystRailPowered)iblockstate.getBlock()).isActivator)
        {
            return false;
        }
        else
        {
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = (BlockRailBase.EnumRailDirection)iblockstate.getValue(SHAPE);

            if (p_176567_5_ != BlockRailBase.EnumRailDirection.EAST_WEST || blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.NORTH_SOUTH && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_NORTH && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_SOUTH)
            {
                if (p_176567_5_ != BlockRailBase.EnumRailDirection.NORTH_SOUTH || blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.EAST_WEST && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_EAST && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_WEST)
                {
                    if (((Boolean)iblockstate.getValue(POWERED)).booleanValue())
                    {
                        return worldIn.isBlockPowered(pos) ? true : this.findPoweredRailSignal(worldIn, pos, iblockstate, p_176567_3_, distance + 1);
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
    }
}
