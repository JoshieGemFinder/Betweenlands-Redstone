package com.joshiegemfinder.betweenlandsredstone.blocks.piston;

import java.util.List;

import com.google.common.collect.Lists;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.handler.LocationHandler;


public class BlockScabystPistonBase extends BlockPistonBase {

	protected final boolean isSticky;
	
    public BlockScabystPistonBase(String name, boolean isSticky) {
    	super(isSticky);
    	this.isSticky = isSticky;
        
        this.setSoundType(SoundType.STONE);
        this.setHardness(0.5F);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
    }
    
    //necessary
    @Override
    protected void checkForMove(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

        if (flag && !((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
        	//this line
            if ((new BlockScabystPistonStructureHelper(worldIn, pos, enumfacing, true)).canMove())
            {
                worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
            }
        }
        else if (!flag && ((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            worldIn.addBlockEvent(pos, this, 1, enumfacing.getIndex());
        }
    }

    //necessary, also yucky
    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

        if (!worldIn.isRemote)
        {
            boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

            if (flag && id == 1)
            {
                worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
                return false;
            }

            if (!flag && id == 0)
            {
                return false;
            }
        }

        if (id == 0)
        {
            if (!this.doMove(worldIn, pos, enumfacing, true))
            {
                return false;
            }

            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 3);
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (id == 1)
        {
            TileEntity tileentity1 = worldIn.getTileEntity(pos.offset(enumfacing));

            if (tileentity1 instanceof TileEntityScabystPiston)
            {
                ((TileEntityScabystPiston)tileentity1).clearPistonTileEntity();
            }

        	//this line
            worldIn.setBlockState(pos, ModBlocks.SCABYST_PISTON_EXTENSION.getDefaultState().withProperty(BlockScabystPistonMoving.FACING, enumfacing).withProperty(BlockScabystPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            worldIn.setTileEntity(pos, BlockScabystPistonMoving.createTilePiston(this.getStateFromMeta(param), enumfacing, false, true));

            if (this.isSticky)
            {
                BlockPos blockpos = pos.add(enumfacing.getFrontOffsetX() * 2, enumfacing.getFrontOffsetY() * 2, enumfacing.getFrontOffsetZ() * 2);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                boolean flag1 = false;

            	//this line
                if (block == ModBlocks.SCABYST_PISTON_EXTENSION)
                {
                    TileEntity tileentity = worldIn.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityScabystPiston)
                    {
                        TileEntityScabystPiston tileentitypiston = (TileEntityScabystPiston)tileentity;

                        if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending())
                        {
                            tileentitypiston.clearPistonTileEntity();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && !iblockstate.getBlock().isAir(iblockstate, worldIn, blockpos) && canPush(iblockstate, worldIn, blockpos, enumfacing.getOpposite(), false, enumfacing) && (iblockstate.getMobilityFlag() == EnumPushReaction.NORMAL || block == ModBlocks.SCABYST_PISTON || block == ModBlocks.SCABYST_STICKY_PISTON))
                {
                    this.doMove(worldIn, pos, enumfacing, false);
                }
            }
            else
            {
                worldIn.setBlockToAir(pos.offset(enumfacing));
            }

            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    //necessary, static
    public static boolean canPush(IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing pushDir, boolean destroyBlocks, EnumFacing blockFace)
    {
        Block block = blockStateIn.getBlock();

    	//can't push/pull guarded blocks
        if(LocationHandler.isProtected(worldIn, null, pos)) {
        	return false;
        }
        
        if(
    		worldIn.getWorldBorder().contains(pos) &&
    		(pos.getY() >= 0 && (pushDir != EnumFacing.DOWN || pos.getY() != 0)) &&
    		(pos.getY() <= worldIn.getHeight() - 1 && (pushDir != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1))
		)
        {
            if (block == ModBlocks.SCABYST_PISTON || block == ModBlocks.SCABYST_STICKY_PISTON)
            {
            	if (((Boolean)blockStateIn.getValue(EXTENDED)).booleanValue())
                {
                    return false;
                }
            	return !block.hasTileEntity(blockStateIn);
            }
        }

        // for compat
        return BlockPistonBase.canPush(blockStateIn, worldIn, pos, pushDir, destroyBlocks, blockFace);
    }

    //necessary
    @Override
    protected boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending)
    {
        if (!extending)
        {
            worldIn.setBlockToAir(pos.offset(direction));
        }

        BlockScabystPistonStructureHelper blockpistonstructurehelper = new BlockScabystPistonStructureHelper(worldIn, pos, direction, extending);

        if (!blockpistonstructurehelper.canMove())
        {
            return false;
        }
        else
        {
            List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
            List<IBlockState> list1 = Lists.<IBlockState>newArrayList();

            for (int i = 0; i < list.size(); ++i)
            {
                BlockPos blockpos = list.get(i);
                list1.add(worldIn.getBlockState(blockpos).getActualState(worldIn, blockpos));
            }

            List<BlockPos> list2 = blockpistonstructurehelper.getBlocksToDestroy();
            int k = list.size() + list2.size();
            IBlockState[] aiblockstate = new IBlockState[k];
            EnumFacing enumfacing = extending ? direction : direction.getOpposite();

            for (int j = list2.size() - 1; j >= 0; --j)
            {
                BlockPos blockpos1 = list2.get(j);
                IBlockState iblockstate = worldIn.getBlockState(blockpos1);
                // Forge: With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
                float chance = iblockstate.getBlock() instanceof BlockSnow ? -1.0f : 1.0f;
                iblockstate.getBlock().dropBlockAsItemWithChance(worldIn, blockpos1, iblockstate, chance, 0);
                worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 4);
                --k;
                aiblockstate[k] = iblockstate;
            }

            for (int l = list.size() - 1; l >= 0; --l)
            {
                BlockPos blockpos3 = list.get(l);
                IBlockState iblockstate2 = worldIn.getBlockState(blockpos3);
                worldIn.setBlockState(blockpos3, Blocks.AIR.getDefaultState(), 2);
                blockpos3 = blockpos3.offset(enumfacing);
                worldIn.setBlockState(blockpos3, ModBlocks.SCABYST_PISTON_EXTENSION.getDefaultState().withProperty(FACING, direction), 4);
                worldIn.setTileEntity(blockpos3, BlockScabystPistonMoving.createTilePiston(list1.get(l), direction, extending, false));
                --k;
                aiblockstate[k] = iblockstate2;
            }

            BlockPos blockpos2 = pos.offset(direction);

            if (extending)
            {
                BlockPistonExtension.EnumPistonType blockpistonextension$enumpistontype = this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
                IBlockState iblockstate3 = ModBlocks.SCABYST_PISTON_HEAD.getDefaultState().withProperty(BlockScabystPistonExtension.FACING, direction).withProperty(BlockScabystPistonExtension.TYPE, blockpistonextension$enumpistontype);
                IBlockState iblockstate1 = ModBlocks.SCABYST_PISTON_EXTENSION.getDefaultState().withProperty(BlockScabystPistonMoving.FACING, direction).withProperty(BlockScabystPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
                worldIn.setBlockState(blockpos2, iblockstate1, 4);
                worldIn.setTileEntity(blockpos2, BlockScabystPistonMoving.createTilePiston(iblockstate3, direction, true, true));
            }

            for (int i1 = list2.size() - 1; i1 >= 0; --i1)
            {
                worldIn.notifyNeighborsOfStateChange(list2.get(i1), aiblockstate[k++].getBlock(), false);
            }

            for (int j1 = list.size() - 1; j1 >= 0; --j1)
            {
                worldIn.notifyNeighborsOfStateChange(list.get(j1), aiblockstate[k++].getBlock(), false);
            }

            if (extending)
            {
                worldIn.notifyNeighborsOfStateChange(blockpos2, ModBlocks.SCABYST_PISTON_HEAD, false);
            }

            return true;
        }
    }
    
}
