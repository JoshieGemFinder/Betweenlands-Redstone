package com.joshiegemfinder.betweenlandsredstone.blocks.piston;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.stickyblocks.BlockStickyBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityScabystPiston extends TileEntityPiston {

    public TileEntityScabystPiston()
    {
    }

    public TileEntityScabystPiston(IBlockState pistonStateIn, EnumFacing pistonFacingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn)
    {
    	super(pistonStateIn, pistonFacingIn, extendingIn, shouldHeadBeRenderedIn);
    }

    //necessary
    @Override
    protected IBlockState getCollisionRelatedBlockState()
    {
        return (!this.isExtending() && this.shouldPistonHeadBeRendered()) ? ModBlocks.SCABYST_PISTON_HEAD.getDefaultState().withProperty(BlockScabystPistonExtension.TYPE, this.pistonState.getBlock() == ModBlocks.SCABYST_STICKY_PISTON ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT).withProperty(BlockScabystPistonExtension.FACING, this.pistonState.getValue(BlockScabystPistonBase.FACING)) : this.pistonState;
    }

    @Override
    protected void moveCollidedEntities(float p_184322_1_)
    {
        EnumFacing enumfacing = this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
        double d0 = (double)(p_184322_1_ - this.progress);
        List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        this.getCollisionRelatedBlockState().addCollisionBoxToList(this.world, BlockPos.ORIGIN, new AxisAlignedBB(BlockPos.ORIGIN), list, (Entity)null, true);

        if (!list.isEmpty())
        {
            AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(this.getMinMaxPiecesAABB(list));
            List<Entity> list1 = this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, this.getMovementArea(axisalignedbb, enumfacing, d0).union(axisalignedbb));

            if (!list1.isEmpty())
            {
            	Block pistonBlock = this.pistonState.getBlock();
            	boolean isHoneyFlag = pistonBlock instanceof BlockStickyBase && ((BlockStickyBase)pistonBlock).getStickyProperties().shouldUseHoneyPush();
                boolean doSlimeBounce = pistonBlock.isStickyBlock(this.pistonState) && !isHoneyFlag;
                
                for (int i = 0; i < list1.size(); ++i)
                {
                    Entity entity = list1.get(i);

                    if (entity.getPushReaction() != EnumPushReaction.IGNORE)
                    {
                        if (doSlimeBounce)
                        {
                            switch (enumfacing.getAxis())
                            {
                                case X:
                                    entity.motionX = (double)enumfacing.getFrontOffsetX();
                                    break;
                                case Y:
                                    entity.motionY = (double)enumfacing.getFrontOffsetY();
                                    break;
                                case Z:
                                    entity.motionZ = (double)enumfacing.getFrontOffsetZ();
                            }
                        }

                        double d1 = 0.0D;

                        for (int j = 0; j < list.size(); ++j)
                        {
                            AxisAlignedBB axisalignedbb1 = this.getMovementArea(this.moveByPositionAndProgress(list.get(j)), enumfacing, d0);
                            AxisAlignedBB axisalignedbb2 = entity.getEntityBoundingBox();

                            if (axisalignedbb1.intersects(axisalignedbb2))
                            {
                                d1 = Math.max(d1, this.getMovement(axisalignedbb1, enumfacing, axisalignedbb2));

                                if (d1 >= d0)
                                {
                                    break;
                                }
                            }
                        }

                        if (d1 > 0.0D)
                        {
                            d1 = Math.min(d1, d0) + 0.01D;
                            pushEntity(enumfacing, entity, d1, enumfacing);
                            
                            if (!this.extending && this.shouldHeadBeRendered)
                            {
                                this.fixEntityWithinPistonBase(entity, enumfacing, d0);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void fixEntityWithinPistonBase(Entity p_190605_1_, EnumFacing p_190605_2_, double p_190605_3_)
    {
        AxisAlignedBB axisalignedbb = p_190605_1_.getEntityBoundingBox();
        AxisAlignedBB axisalignedbb1 = Block.FULL_BLOCK_AABB.offset(this.pos);

        if (axisalignedbb.intersects(axisalignedbb1))
        {
            EnumFacing enumfacing = p_190605_2_.getOpposite();
            double d0 = this.getMovement(axisalignedbb1, enumfacing, axisalignedbb) + 0.01D;
            double d1 = this.getMovement(axisalignedbb1, enumfacing, axisalignedbb.intersect(axisalignedbb1)) + 0.01D;

            if (Math.abs(d0 - d1) < 0.01D)
            {
                d0 = Math.min(d0, p_190605_3_) + 0.01D;
                pushEntity(p_190605_2_, p_190605_1_, d0, enumfacing);
            }
        }
    }
    
    @Override
    public void clearPistonTileEntity()
    {
        if (this.lastProgress < 1.0F && this.world != null)
        {
            this.progress = 1.0F;
            this.lastProgress = this.progress;
            this.world.removeTileEntity(this.pos);
            this.invalidate();

            if (this.world.getBlockState(this.pos).getBlock() == ModBlocks.SCABYST_PISTON_EXTENSION)
            {
                this.world.setBlockState(this.pos, this.pistonState, 3);
                this.world.neighborChanged(this.pos, this.pistonState.getBlock(), this.pos);
            }
        }
    }

    @Override
    public void update()
    {
        this.lastProgress = this.progress;

        if (this.lastProgress >= 1.0F)
        {
            this.world.removeTileEntity(this.pos);
            this.invalidate();

            if (this.world.getBlockState(this.pos).getBlock() == ModBlocks.SCABYST_PISTON_EXTENSION)
            {
                this.world.setBlockState(this.pos, this.pistonState, 3);
                this.world.neighborChanged(this.pos, this.pistonState.getBlock(), this.pos);
            }
        }
        else
        {
            float f = this.progress + 0.5F;
            this.moveCollidedEntities(f);
            this.tryHoneyPush(f);
            this.progress = f;

            if (this.progress >= 1.0F)
            {
                this.progress = 1.0F;
            }
        }
    }

    @Override
    public void addCollissionAABBs(World p_190609_1_, BlockPos p_190609_2_, AxisAlignedBB p_190609_3_, List<AxisAlignedBB> p_190609_4_, @Nullable Entity p_190609_5_)
    {
        if (!this.extending && this.shouldHeadBeRendered)
        {
            this.pistonState.withProperty(BlockScabystPistonBase.EXTENDED, Boolean.valueOf(true)).addCollisionBoxToList(p_190609_1_, p_190609_2_, p_190609_3_, p_190609_4_, p_190609_5_, false);
        }

        EnumFacing enumfacing = MOVING_ENTITY.get();

        if ((double)this.progress >= 1.0D || enumfacing != (this.extending ? this.pistonFacing : this.pistonFacing.getOpposite()))
        {
            int i = p_190609_4_.size();
            IBlockState iblockstate;

            if (this.shouldPistonHeadBeRendered())
            {
                iblockstate = ModBlocks.SCABYST_PISTON_HEAD.getDefaultState().withProperty(BlockScabystPistonExtension.FACING, this.pistonFacing).withProperty(BlockScabystPistonExtension.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 0.25F));
            }
            else
            {
                iblockstate = this.pistonState;
            }

            float f = this.getExtendedProgress(this.progress);
            double d0 = (double)((float)this.pistonFacing.getFrontOffsetX() * f);
            double d1 = (double)((float)this.pistonFacing.getFrontOffsetY() * f);
            double d2 = (double)((float)this.pistonFacing.getFrontOffsetZ() * f);
            iblockstate.addCollisionBoxToList(p_190609_1_, p_190609_2_, p_190609_3_.offset(-d0, -d1, -d2), p_190609_4_, p_190609_5_, true);

            for (int j = i; j < p_190609_4_.size(); ++j)
            {
                p_190609_4_.set(j, ((AxisAlignedBB)p_190609_4_.get(j)).offset(d0, d1, d2));
            }
        }
    }
    
    protected static void pushEntity(EnumFacing direction1, Entity entity, double distance, EnumFacing direction2) {
    	MOVING_ENTITY.set(direction1);
    	entity.move(MoverType.PISTON, distance * (double)direction2.getFrontOffsetX(), distance * (double)direction2.getFrontOffsetY(), distance * (double)direction2.getFrontOffsetZ());
    	MOVING_ENTITY.set((EnumFacing)null);
    }
    
    protected void tryHoneyPush(float partialTicks) {
    	if(isPushingHoney()) {
    		EnumFacing dir = extending ? getFacing() : getFacing().getOpposite();
    		if(dir.getAxis().isHorizontal()) {
    			double end = this.pistonState.getCollisionBoundingBox(world, pos).maxY;
    			AxisAlignedBB axisalignedbb = moveByPositionAndProgress(new AxisAlignedBB(0.0D, end, 0.0D, 1.0D, 1.5000000999999998D, 1.0D));
    			double d1 = (double)(partialTicks - this.progress);
    			for(Entity entity : this.world.getEntitiesInAABBexcluding((Entity)null, axisalignedbb, (entity) -> {
	               return shouldHoneyPush(axisalignedbb, entity);
	            })) {
	               pushEntity(dir, entity, d1, dir);
	            }
    		}
    	}
    }
    
    protected static boolean shouldHoneyPush(AxisAlignedBB axisalignedbb, Entity entity) {
    	return entity.getPushReaction() == EnumPushReaction.NORMAL && entity.onGround && entity.posX >= axisalignedbb.minX && entity.posX <= axisalignedbb.maxX && entity.posZ >= axisalignedbb.minZ && entity.posZ <= axisalignedbb.maxZ;
    }
    
    protected boolean isPushingHoney() {
    	Block pistonBlock = this.pistonState.getBlock();
    	return pistonBlock == ModBlocks.SCABYST_SLIME_2;
//    	return pistonBlock instanceof BlockStickyBase && ((BlockStickyBase)pistonBlock).getStickyProperties().shouldUseHoneyPush();
    }
    
}
