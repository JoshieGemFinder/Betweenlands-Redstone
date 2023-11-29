package com.joshiegemfinder.betweenlandsredstone.blocks.tripwire;

import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;

import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockScabystTripWireHook extends BlockTripWireHook {


	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ModItems.TRIPWIRE_HOOK);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.TRIPWIRE_HOOK;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(ModItems.TRIPWIRE_HOOK);
	}
	
    public void calculateState(World worldIn, BlockPos pos, IBlockState hookState, boolean p_176260_4_, boolean p_176260_5_, int p_176260_6_, @Nullable IBlockState p_176260_7_)
    {
        EnumFacing enumfacing = (EnumFacing)hookState.getValue(FACING);
        boolean flag = ((Boolean)hookState.getValue(ATTACHED)).booleanValue();
        boolean flag1 = ((Boolean)hookState.getValue(POWERED)).booleanValue();
        boolean flag2 = !p_176260_4_;
        boolean flag3 = false;
        int i = 0;
        IBlockState[] aiblockstate = new IBlockState[42];

        for (int j = 1; j < 42; ++j)
        {
            BlockPos blockpos = pos.offset(enumfacing, j);
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() == ModBlocks.TRIPWIRE_HOOK)
            {
                if (iblockstate.getValue(FACING) == enumfacing.getOpposite())
                {
                    i = j;
                }

                break;
            }

            if (iblockstate.getBlock() != ModBlocks.TRIPWIRE && j != p_176260_6_)
            {
                aiblockstate[j] = null;
                flag2 = false;
            }
            else
            {
                if (j == p_176260_6_)
                {
                    iblockstate = (IBlockState)MoreObjects.firstNonNull(p_176260_7_, iblockstate);
                }

                boolean flag4 = !((Boolean)iblockstate.getValue(BlockTripWire.DISARMED)).booleanValue();
                boolean flag5 = ((Boolean)iblockstate.getValue(BlockTripWire.POWERED)).booleanValue();
                flag3 |= flag4 && flag5;
                aiblockstate[j] = iblockstate;

                if (j == p_176260_6_)
                {
                    worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
                    flag2 &= flag4;
                }
            }
        }

        flag2 = flag2 & i > 1;
        flag3 = flag3 & flag2;
        IBlockState iblockstate1 = this.getDefaultState().withProperty(ATTACHED, Boolean.valueOf(flag2)).withProperty(POWERED, Boolean.valueOf(flag3));

        if (i > 0)
        {
            BlockPos blockpos1 = pos.offset(enumfacing, i);
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            worldIn.setBlockState(blockpos1, iblockstate1.withProperty(FACING, enumfacing1), 3);
            this.notifyNeighbors(worldIn, blockpos1, enumfacing1);
            this.playSound(worldIn, blockpos1, flag2, flag3, flag, flag1);
        }

        this.playSound(worldIn, pos, flag2, flag3, flag, flag1);

        if (!p_176260_4_)
        {
            worldIn.setBlockState(pos, iblockstate1.withProperty(FACING, enumfacing), 3);

            if (p_176260_5_)
            {
                this.notifyNeighbors(worldIn, pos, enumfacing);
            }
        }

        if (flag != flag2)
        {
            for (int k = 1; k < i; ++k)
            {
                BlockPos blockpos2 = pos.offset(enumfacing, k);
                IBlockState iblockstate2 = aiblockstate[k];

                if (iblockstate2 != null && worldIn.getBlockState(blockpos2).getMaterial() != Material.AIR)
                {
                    worldIn.setBlockState(blockpos2, iblockstate2.withProperty(ATTACHED, Boolean.valueOf(flag2)), 3);
                }
            }
        }
    }

    private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing side)
    {
        worldIn.notifyNeighborsOfStateChange(pos, this, false);
        worldIn.notifyNeighborsOfStateChange(pos.offset(side.getOpposite()), this, false);
    }
    
    private void playSound(World worldIn, BlockPos pos, boolean p_180694_3_, boolean p_180694_4_, boolean p_180694_5_, boolean p_180694_6_)
    {
        if (p_180694_4_ && !p_180694_6_)
        {
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
        }
        else if (!p_180694_4_ && p_180694_6_)
        {
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
        }
        else if (p_180694_3_ && !p_180694_5_)
        {
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
        }
        else if (!p_180694_3_ && p_180694_5_)
        {
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (worldIn.rand.nextFloat() * 0.2F + 0.9F));
        }
    }
}
