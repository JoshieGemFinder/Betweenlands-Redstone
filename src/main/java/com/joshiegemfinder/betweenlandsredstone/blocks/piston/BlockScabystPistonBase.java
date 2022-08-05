package com.joshiegemfinder.betweenlandsredstone.blocks.piston;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystWorldWrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.common.handler.LocationHandler;


public class BlockScabystPistonBase extends BlockDirectional implements IScabystBlock {

    public static final PropertyBool EXTENDED = PropertyBool.create("extended");
    protected static final AxisAlignedBB PISTON_BASE_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_WEST_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D);
    protected static final AxisAlignedBB PISTON_BASE_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_UP_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_DOWN_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.0D, 1.0D, 1.0D, 1.0D);
    private final boolean isSticky;

    public BlockScabystPistonBase(String name, boolean isSticky)
    {
    	super(Material.PISTON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, Boolean.valueOf(false)));
        this.isSticky = isSticky;
        
        this.setSoundType(SoundType.STONE);
        this.setHardness(0.5F);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
    }

    public boolean causesSuffocation(IBlockState state)
    {
        return !((Boolean)state.getValue(EXTENDED)).booleanValue();
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            switch ((EnumFacing)state.getValue(FACING))
            {
                case DOWN:
                    return PISTON_BASE_DOWN_AABB;
                case UP:
                default:
                    return PISTON_BASE_UP_AABB;
                case NORTH:
                    return PISTON_BASE_NORTH_AABB;
                case SOUTH:
                    return PISTON_BASE_SOUTH_AABB;
                case WEST:
                    return PISTON_BASE_WEST_AABB;
                case EAST:
                    return PISTON_BASE_EAST_AABB;
            }
        }
        else
        {
            return FULL_BLOCK_AABB;
        }
    }

    public boolean isTopSolid(IBlockState state)
    {
        return !((Boolean)state.getValue(EXTENDED)).booleanValue() || state.getValue(FACING) == EnumFacing.DOWN;
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getBoundingBox(worldIn, pos));
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);

        if (!worldIn.isRemote)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)).withProperty(EXTENDED, Boolean.valueOf(false));
    }

    private void checkForMove(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

        if (flag && !((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
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

    private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (enumfacing != facing && ScabystWorldWrapper.isSideScabystPowered(worldIn, pos.offset(enumfacing), enumfacing))
            {
                return true;
            }
        }

        if (ScabystWorldWrapper.isSideScabystPowered(worldIn, pos, EnumFacing.DOWN))
        {
            return true;
        }
        else
        {
            BlockPos blockpos = pos.up();

            for (EnumFacing enumfacing1 : EnumFacing.values())
            {
                if (enumfacing1 != EnumFacing.DOWN && ScabystWorldWrapper.isSideScabystPowered(worldIn, blockpos.offset(enumfacing1), enumfacing1))
                {
                    return true;
                }
            }

            return false;
        }
    }

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

            worldIn.setBlockState(pos, ModBlocks.SCABYST_PISTON_EXTENSION.getDefaultState().withProperty(BlockScabystPistonMoving.FACING, enumfacing).withProperty(BlockScabystPistonMoving.TYPE, this.isSticky ? BlockScabystPistonExtension.EnumPistonType.STICKY : BlockScabystPistonExtension.EnumPistonType.DEFAULT), 3);
            worldIn.setTileEntity(pos, BlockScabystPistonMoving.createTilePiston(this.getStateFromMeta(param), enumfacing, false, true));

            if (this.isSticky)
            {
                BlockPos blockpos = pos.add(enumfacing.getFrontOffsetX() * 2, enumfacing.getFrontOffsetY() * 2, enumfacing.getFrontOffsetZ() * 2);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                boolean flag1 = false;

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

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Nullable
    public static EnumFacing getFacing(int meta)
    {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }


    public static boolean canPush(IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing pushDir, boolean destroyBlocks, EnumFacing blockFace)
    {
        Block block = blockStateIn.getBlock();

        if(!worldIn.isRemote) {
	        if(LocationHandler.isProtected(worldIn, Main.getFakePlayer((WorldServer) worldIn), pos)) {
	        	return false;
	        }
        }
        
        if (block == Blocks.OBSIDIAN)
        {
            return false;
        }
        else if (!worldIn.getWorldBorder().contains(pos))
        {
            return false;
        }
        else if (pos.getY() >= 0 && (pushDir != EnumFacing.DOWN || pos.getY() != 0))
        {
            if (pos.getY() <= worldIn.getHeight() - 1 && (pushDir != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1))
            {
                if (block == ModBlocks.SCABYST_PISTON || block == ModBlocks.SCABYST_STICKY_PISTON)
                {
                	if (((Boolean)blockStateIn.getValue(EXTENDED)).booleanValue())
                    {
                        return false;
                    }
                }
                else if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON)
                {
                	if (((Boolean)blockStateIn.getValue(BlockPistonBase.EXTENDED)).booleanValue())
                    {
                        return false;
                    }
                }
                else {
                    if (blockStateIn.getBlockHardness(worldIn, pos) == -1.0F)
                    {
                        return false;
                    }

                    switch (blockStateIn.getMobilityFlag())
                    {
                        case BLOCK:
                            return false;
                        case DESTROY:
                            return destroyBlocks;
                        case PUSH_ONLY:
                            return pushDir == blockFace;
                        default:
                        	break;
                    }
                }
                return !block.hasTileEntity(blockStateIn);
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

    private boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending)
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
                BlockScabystPistonExtension.EnumPistonType blockpistonextension$enumpistontype = this.isSticky ? BlockScabystPistonExtension.EnumPistonType.STICKY : BlockScabystPistonExtension.EnumPistonType.DEFAULT;
                IBlockState iblockstate3 = ModBlocks.SCABYST_PISTON_HEAD.getDefaultState().withProperty(BlockScabystPistonExtension.FACING, direction).withProperty(BlockScabystPistonExtension.TYPE, blockpistonextension$enumpistontype);
                IBlockState iblockstate1 = ModBlocks.SCABYST_PISTON_EXTENSION.getDefaultState().withProperty(BlockScabystPistonMoving.FACING, direction).withProperty(BlockScabystPistonMoving.TYPE, this.isSticky ? BlockScabystPistonExtension.EnumPistonType.STICKY : BlockScabystPistonExtension.EnumPistonType.DEFAULT);
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

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(EXTENDED, Boolean.valueOf((meta & 8) > 0));
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            i |= 8;
        }

        return i;
    }

    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, EXTENDED});
    }

    public EnumPushReaction getMobilityFlag() {
    	return EnumPushReaction.NORMAL;
    }
    
    /* ======================================== FORGE START =====================================*/
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        IBlockState state = world.getBlockState(pos);
        return !state.getValue(EXTENDED) && super.rotateBlock(world, pos, axis);
    }

    @SuppressWarnings("deprecation")
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        state = this.getActualState(state, worldIn, pos);
        return state.getValue(FACING) != face.getOpposite() && ((Boolean)state.getValue(EXTENDED)).booleanValue() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }
}
