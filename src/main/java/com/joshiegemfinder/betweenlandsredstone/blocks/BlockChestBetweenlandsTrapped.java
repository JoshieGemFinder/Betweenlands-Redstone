package com.joshiegemfinder.betweenlandsredstone.blocks;

import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.container.BlockChestBetweenlands;

public class BlockChestBetweenlandsTrapped extends BlockChest {
	public static BlockChest.Type TRAPPED_WEEDWOOD_CHEST = EnumHelper.addEnum(BlockChest.Type.class, "TRAPPED_WEEDWOOD_CHEST", new Class[0], new Object[0]);

	public BlockChestBetweenlandsTrapped(Type chestTypeIn) {
		super(chestTypeIn);
        this.setCreativeTab(chestTypeIn == BlockChest.Type.TRAP || chestTypeIn == TRAPPED_WEEDWOOD_CHEST ? CreativeTabs.REDSTONE : (chestTypeIn == BlockChest.Type.BASIC ? CreativeTabs.DECORATIONS : BLCreativeTabs.BLOCKS));
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("axe", 0);
	}

	@Override
    public boolean canProvidePower(IBlockState state)
    {
        return this.chestType == BlockChest.Type.TRAP || this.chestType == TRAPPED_WEEDWOOD_CHEST;
    }

	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            ILockableContainer ilockablecontainer = this.getLockableContainer(worldIn, pos);

            if (ilockablecontainer != null)
            {
                playerIn.displayGUIChest(ilockablecontainer);

                if (this.chestType == BlockChest.Type.BASIC || this.chestType == BlockChestBetweenlands.WEEDWOOD_CHEST)
                {
                    playerIn.addStat(StatList.CHEST_OPENED);
                }
                else if (this.chestType == BlockChest.Type.TRAP || this.chestType == TRAPPED_WEEDWOOD_CHEST)
                {
                    playerIn.addStat(StatList.TRAPPED_CHEST_TRIGGERED);
                }
            }

            return true;
        }
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityChestBetweenlandsTrapped();
	}
	
	@Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (!blockState.canProvidePower())
        {
            return 0;
        }
        else
        {
            int i = 0;
            TileEntity tileentity = blockAccess.getTileEntity(pos);

            if (tileentity instanceof TileEntityChest)
            {
                i = ((TileEntityChest)tileentity).numPlayersUsing;
            }

            return MathHelper.clamp(i, 0, 15);
        }
    }

	@Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
        return side == EnumFacing.UP ? getWeakPower(blockState, blockAccess, pos, side) : 0;
    }
}
