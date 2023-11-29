package com.joshiegemfinder.betweenlandsredstone.blocks.tripwire;

import java.util.List;
import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScabystTripWire extends BlockTripWire implements IModelInterface {

    public BlockScabystTripWire() {
    	super();
    }
	
    private void notifyHook(World worldIn, BlockPos pos, IBlockState state)
    {
        for (EnumFacing enumfacing : new EnumFacing[] {EnumFacing.SOUTH, EnumFacing.WEST})
        {
            for (int i = 1; i < 42; ++i)
            {
                BlockPos blockpos = pos.offset(enumfacing, i);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);

                if (iblockstate.getBlock() == ModBlocks.TRIPWIRE_HOOK)
                {
                    if (iblockstate.getValue(BlockScabystTripWireHook.FACING) == enumfacing.getOpposite())
                    {
                    	ModBlocks.TRIPWIRE_HOOK.calculateState(worldIn, blockpos, iblockstate, false, true, i, state);
                    }

                    break;
                }

                if (iblockstate.getBlock() != ModBlocks.TRIPWIRE)
                {
                    break;
                }
            }
        }
    }
    
    public static boolean isConnectedTo(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing direction)
    {
        BlockPos blockpos = pos.offset(direction);
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (block == ModBlocks.TRIPWIRE_HOOK)
        {
            EnumFacing enumfacing = direction.getOpposite();
            return iblockstate.getValue(BlockScabystTripWireHook.FACING) == enumfacing;
        }
        else
        {
            return block == ModBlocks.TRIPWIRE;
        }
    }
    


    //start of literally just copy pasted stuff that is required to make it work because PRIVATE METHODS
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        this.notifyHook(worldIn, pos, state.withProperty(POWERED, Boolean.valueOf(true)));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos, state, 3);
        this.notifyHook(worldIn, pos, state);
    }
    
    private void updateState(World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        boolean flag = ((Boolean)iblockstate.getValue(POWERED)).booleanValue();
        boolean flag1 = false;
        List <? extends Entity > list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, iblockstate.getBoundingBox(worldIn, pos).offset(pos));

        if (!list.isEmpty())
        {
            for (Entity entity : list)
            {
                if (!entity.doesEntityNotTriggerPressurePlate())
                {
                    flag1 = true;
                    break;
                }
            }
        }

        if (flag1 != flag)
        {
            iblockstate = iblockstate.withProperty(POWERED, Boolean.valueOf(flag1));
            worldIn.setBlockState(pos, iblockstate, 3);
            this.notifyHook(worldIn, pos, iblockstate);
        }

        if (flag1)
        {
            worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (((Boolean)worldIn.getBlockState(pos).getValue(POWERED)).booleanValue())
            {
                this.updateState(worldIn, pos);
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!worldIn.isRemote)
        {
            if (!((Boolean)state.getValue(POWERED)).booleanValue())
            {
                this.updateState(worldIn, pos);
            }
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(NORTH, Boolean.valueOf(isConnectedTo(worldIn, pos, state, EnumFacing.NORTH))).withProperty(EAST, Boolean.valueOf(isConnectedTo(worldIn, pos, state, EnumFacing.EAST))).withProperty(SOUTH, Boolean.valueOf(isConnectedTo(worldIn, pos, state, EnumFacing.SOUTH))).withProperty(WEST, Boolean.valueOf(isConnectedTo(worldIn, pos, state, EnumFacing.WEST)));
    }
    //end of literally just copy pasted stuff that is required to make it work because PRIVATE METHODS

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomStateMapper(this, (new StateMap.Builder()).ignore(DISARMED, POWERED).build());
	}
    
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ModItems.TRIPWIRE);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.TRIPWIRE;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(ModItems.TRIPWIRE);
	}
}
