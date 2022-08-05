package com.joshiegemfinder.betweenlandsredstone.blocks.piston.stickyblocks;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class BlockScabystSlime_1 extends BlockStickyBase implements IScabystBlock {
	
	public BlockScabystSlime_1(String name)
    {
        super(Material.CLAY, MapColor.BROWN);
        this.setHardness(0f);
        this.setSoundType(SoundType.SLIME);
        this.slipperiness = 0.8F;
        
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
        ModBlocks.BLOCKS.add(this);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();
        return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
    	if(entityIn.isSneaking()) {
    		super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    	} else {
    		entityIn.fall(fallDistance, 0.0F);
    	}
    }
    
    @Override
    public void onLanded(World worldIn, Entity entityIn) {
    	if(entityIn.isSneaking()) {
    		super.onLanded(worldIn, entityIn);
    	} else {
        	if(entityIn.motionY < 0) {
    			entityIn.motionY = -entityIn.motionY;
    			
    			if(!(entityIn instanceof EntityLivingBase)) {
    				entityIn.motionY *= 0.8D;
    			}
    		}
    	}
    }
    
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        if (Math.abs(entityIn.motionY) < 0.1D && !entityIn.isSneaking())
        {
            double d0 = 0.4D + Math.abs(entityIn.motionY) * 0.2D;
            entityIn.motionX *= d0;
            entityIn.motionZ *= d0;
        }

        super.onEntityWalk(worldIn, pos, entityIn);
    }
}
