package com.joshiegemfinder.betweenlandsredstone.blocks.piston;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockScabystPistonExtension extends BlockPistonExtension {

	public BlockScabystPistonExtension(String name) {
        super();
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT).withProperty(SHORT, Boolean.valueOf(false)));
        this.setSoundType(SoundType.STONE);
        this.setHardness(0.5F);

		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
	}

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            BlockPos blockpos = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());
            Block block = worldIn.getBlockState(blockpos).getBlock();

            if (block == ModBlocks.SCABYST_PISTON || block == ModBlocks.SCABYST_STICKY_PISTON)
            {
                worldIn.setBlockToAir(blockpos);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        EnumFacing enumfacing = ((EnumFacing)state.getValue(FACING)).getOpposite();
        pos = pos.offset(enumfacing);
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if ((iblockstate.getBlock() == ModBlocks.SCABYST_PISTON || iblockstate.getBlock() == ModBlocks.SCABYST_STICKY_PISTON) && ((Boolean)iblockstate.getValue(BlockScabystPistonBase.EXTENDED)).booleanValue())
        {
        	iblockstate.getBlock().dropBlockAsItem(worldIn, pos, iblockstate, 0);
        	worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() != ModBlocks.SCABYST_PISTON && iblockstate.getBlock() != ModBlocks.SCABYST_STICKY_PISTON &&
    		iblockstate.getBlock() != Blocks.PISTON && iblockstate.getBlock() != Blocks.STICKY_PISTON)
        {
            worldIn.setBlockToAir(pos);
        }
        else
        {
            iblockstate.neighborChanged(worldIn, blockpos, blockIn, fromPos);
        }
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY ? ModBlocks.SCABYST_STICKY_PISTON : ModBlocks.SCABYST_PISTON);
    }
}
