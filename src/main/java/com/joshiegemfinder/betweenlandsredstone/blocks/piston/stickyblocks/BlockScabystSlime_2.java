package com.joshiegemfinder.betweenlandsredstone.blocks.piston.stickyblocks;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModSounds;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityFishBait;

@SuppressWarnings("deprecation")
public class BlockScabystSlime_2 extends BlockStickyBase {

	private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0625F, 0.0625F, 0.0625F, 1 - 0.0625F, 1 - 0.0625F, 1 - 0.0625F);
	
	public BlockScabystSlime_2(String name)
    {
        super(Material.CLAY, MapColor.YELLOW_STAINED_HARDENED_CLAY);
        this.stickyProperties = new BlockStickyProperties() {
    		public boolean shouldUseHoneyPush() {
    			return true;
    		}
    	};
        this.setHardness(0f);
        this.setSoundType(ModSounds.SAP);
        this.slipperiness = 0.8F;

		this.setUnlocalizedName(name);
		this.setRegistryName(name);
        ModBlocks.BLOCKS.add(this);
    }
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return BOUNDING_BOX;
	}

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    
    @Override
    public boolean isFullBlock(IBlockState state) {
    	return false;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
    	return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();
        return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    
    //honey block stuff
    @Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
    	this.honeyBehaviour(pos, world, entity);
    	super.onEntityCollidedWithBlock(world, pos, state, entity);
	}
    
    public void honeyBehaviour(BlockPos pos, World world, Entity entity) {
    	if(this.shouldSlide(pos, entity)) {
    		entity.fallDistance = 0.0F;
    		this.slide(entity);
    		this.slideEffects(world, entity);
    	}
    }
    
    protected boolean shouldSlide(BlockPos pos, Entity entity) {
    	//entity.onGround (and entity.isAirBorne, most of the time) aren't set properly for non-players (or on the server at all)
    	if(entity instanceof EntityPlayer && entity.world.isRemote && (entity.onGround || !entity.isAirBorne)) {
    		return false;
    	}
    	else if(entity.posY > (double)pos.getY() + 0.9375D - 1.0E-7D) {
            return false;
    	}
    	else if(entity.motionY >= -0.08D) {
    		return false;
    	}
    	else {
    		double d0 = Math.abs((double)pos.getX() + 0.5D - entity.posX);
            double d1 = Math.abs((double)pos.getZ() + 0.5D - entity.posZ);
            double d2 = 0.4375D + (double)(entity.width	 / 2.0F);
            return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
    	}
    }
    
    protected void slide(Entity entity) {
    	double y = entity.motionY;
    	if(y < -0.13D) {
    		//Entity.SetVelocity is client side only, and so causes issues when run on a server
            double multiplier = -0.05D / y;
        	entity.motionX *= multiplier;
        	entity.motionY = -0.05D;
        	entity.motionZ *= multiplier;
    	} else {
        	entity.motionY = -0.05D;
    	}
    	
    	entity.fallDistance = 0;
    }

    protected void slideEffects(World world, Entity entity) {
    	if(!doesSlideEffects(entity)) {
    		return;
		}
    	
    	if(world.rand.nextInt(5) == 0) {
    		playSlideSound(entity);
    	}
    }
    
    protected void playSlideSound(Entity entity, float volume, float pitch) {
    	entity.getEntityWorld().playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, ModSounds.BLOCK_SAP_SLIDE, SoundCategory.BLOCKS, volume, pitch);
    }

    protected void playSlideSound(Entity entity) {
    	playSlideSound(entity, 1.0F, 0.2F);
    }
    
    protected boolean doesSlideEffects(Entity entity) {
    	return
    			//vanilla things
    			entity instanceof EntityLivingBase || entity instanceof EntityMinecart || entity instanceof EntityTNTPrimed || entity instanceof EntityBoat
    			//bl things
    			|| entity instanceof IProjectile || entity instanceof EntityFishBait;
    }
    
    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
//		entityIn.playSound(SoundEvents.BLOCK_SLIME_FALL, 1.0F, 1.0F);
    	entityIn.fall(fallDistance, 0.2F);
    	entityIn.playSound(this.blockSoundType.getFallSound(), this.blockSoundType.getVolume() * 0.5F,
				this.blockSoundType.getPitch() * 0.75F);
    }
    
    //this is in 1.15, in BlockProperties
    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        double d0 = 0.4D;
        entityIn.motionX *= d0;
        entityIn.motionZ *= d0;

        super.onEntityWalk(worldIn, pos, entityIn);
    }
}
