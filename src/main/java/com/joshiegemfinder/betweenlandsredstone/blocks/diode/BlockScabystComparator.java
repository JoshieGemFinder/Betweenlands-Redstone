package com.joshiegemfinder.betweenlandsredstone.blocks.diode;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;

import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockScabystComparator extends BlockRedstoneComparator implements ITileEntityProvider {

	public BlockScabystComparator(String name, boolean powered) {
		super(powered);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)).withProperty(MODE, BlockScabystComparator.Mode.COMPARE));
        this.hasTileEntity = true;
        this.setHardness(0.0F);
        this.setSoundType(SoundType.WOOD);
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(BetweenlandsRedstone.MODID, name));
		ModBlocks.BLOCKS.add(this);
	}
	
	@Override
	protected int getPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return BlockScabystDiode.getPowerOnSide(this, worldIn, pos, side);
	}

	//TODO Change this if making a new class
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.SCABYST_COMPARATOR;
	}
    
	//TODO Change this if making a new class
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		 return new ItemStack(this);
	}
    
	//TODO Change this if making a new class
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		 return new ItemStack(this);
	}
	
	//TODO Change this if making a new class
	protected IBlockState getPoweredState(IBlockState unpoweredState)
    {
        Boolean obool = (Boolean)unpoweredState.getValue(POWERED);
        BlockScabystComparator.Mode blockredstonecomparator$mode = (BlockScabystComparator.Mode)unpoweredState.getValue(MODE);
        EnumFacing enumfacing = (EnumFacing)unpoweredState.getValue(FACING);
        return ModBlocks.POWERED_SCABYST_COMPARATOR.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, obool).withProperty(MODE, blockredstonecomparator$mode);
    }

	//TODO Change this if making a new class
    protected IBlockState getUnpoweredState(IBlockState poweredState)
    {
        Boolean obool = (Boolean)poweredState.getValue(POWERED);
        BlockScabystComparator.Mode blockredstonecomparator$mode = (BlockScabystComparator.Mode)poweredState.getValue(MODE);
        EnumFacing enumfacing = (EnumFacing)poweredState.getValue(FACING);
        return ModBlocks.UNPOWERED_SCABYST_COMPARATOR.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, obool).withProperty(MODE, blockredstonecomparator$mode);
    }
    
    protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state)
    {
        int i = BlockScabystDiode.calculateInputStrength(worldIn, pos, state);
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.hasComparatorInputOverride())
        {
            i = iblockstate.getComparatorInputOverride(worldIn, blockpos);
        }
        else if (i < 15 && iblockstate.isNormalCube())
        {
            blockpos = blockpos.offset(enumfacing);
            iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.hasComparatorInputOverride())
            {
                i = iblockstate.getComparatorInputOverride(worldIn, blockpos);
            }
            else if (iblockstate.getMaterial() == Material.AIR)
            {
                EntityItemFrame entityitemframe = this.findItemFrame(worldIn, enumfacing, blockpos);

                if (entityitemframe != null)
                {
                    i = entityitemframe.getAnalogOutput();
                }
            }
        }

        return i;
    }
    
//    @Override
//    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
//    	return Connector.canScabystConnect(world, pos, side) && super.canConnectRedstone(state, world, pos, side);
//    }
}
