package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;
import com.joshiegemfinder.betweenlandsredstone.gui.BetweenlandsRedstoneGuiHandler;
import com.joshiegemfinder.betweenlandsredstone.util.ParticleHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrafter extends BlockContainer implements IModelInterface {

	public static final PropertyDirection FACING = BlockDirectional.FACING;
	public static final PropertyBool TRIGGERED = BlockDispenser.TRIGGERED;
	public static final PropertyBool CRAFTING = PropertyBool.create("crafting");
	
	public BlockCrafter(Material material) {
		super(material);

		this.setSoundType(SoundType.STONE);
		
		this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(TRIGGERED, false).withProperty(CRAFTING, false));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCrafter();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote) {
			return true;
		}
		
        playerIn.openGui(BetweenlandsRedstone.instance, BetweenlandsRedstoneGuiHandler.GUI_CRAFTER_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)).withProperty(TRIGGERED, world.isBlockPowered(pos));
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		
//		boolean isTriggered = state.getValue(TRIGGERED);

		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof TileEntityCrafter) {
			TileEntityCrafter entity = (TileEntityCrafter)te;
//			entity.setTriggered(isTriggered);
//			if(isTriggered) {
				entity.tryToCraft();
//			}
		}
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		
		boolean isTriggered = state.getValue(TRIGGERED);

		if(isTriggered && !worldIn.isUpdateScheduled(pos, this)) {
			worldIn.scheduleBlockUpdate(pos, this, 4, 0);
		}
		
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof TileEntityCrafter) {
			TileEntityCrafter entity = (TileEntityCrafter)te;
			entity.setTriggered(isTriggered);
		}
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		boolean isPowered = worldIn.isBlockPowered(pos);
		boolean wasPowered = state.getValue(TRIGGERED);
		
		if(isPowered != wasPowered) {
			IBlockState newState = state.withProperty(TRIGGERED, isPowered);
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof TileEntityCrafter) {
				TileEntityCrafter entity = (TileEntityCrafter)te;
				entity.setTriggered(isPowered);
				if(isPowered && !worldIn.isUpdateScheduled(pos, this)) {
					worldIn.scheduleBlockUpdate(pos, this, 4, 0);
				}
			}
			
			worldIn.setBlockState(pos, newState);
		}
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		
        if (tileentity instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		// TODO Auto-generated method stub
		return new BlockStateContainer(this, new IProperty[] {FACING, TRIGGERED, CRAFTING});
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i |= state.getValue(FACING).getIndex();
		i |= state.getValue(TRIGGERED) ? 8 : 0;
		return i;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta & 7]).withProperty(TRIGGERED, (meta & 8) != 0);
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof TileEntityCrafter) {
			state = state.withProperty(CRAFTING, ((TileEntityCrafter)tileentity).isCrafting());
		}
		return state;
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {

		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof TileEntityCrafter) {
			return ((TileEntityCrafter)tileentity).getComparatorPower();
		}
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomStateMapper(this, CrafterStateMapper.INSTANCE);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
		return EnumBlockRenderType.MODEL;
	}

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
    }
    
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }
    
    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id,
    		int param) {
    	if(id == 9002) {
	    	EnumFacing facing = world.getBlockState(pos).getValue(FACING);
			IPosition dispensePosition = BlockDispenser.getDispensePosition(new BlockSourceImpl(world, pos));
			
	        double x = dispensePosition.getX();
	        double y = dispensePosition.getY();
	        double z = dispensePosition.getZ();
	        
	        if (facing.getAxis() == EnumFacing.Axis.Y)
	        {
	            y = y - 0.125D;
	        }
	        else
	        {
	            y = y - 0.15625D;
	        }
	        
			for(int i = 0; i < 20; ++i) {
		        double speedFactor = world.rand.nextDouble() * 0.02D + 0.04D;
		        double xSpeed = (double)facing.getFrontOffsetX() * speedFactor;
		        double ySpeed = ((double)facing.getFrontOffsetY() + 0.1D) * speedFactor;
		        double zSpeed = (double)facing.getFrontOffsetZ() * speedFactor;
		        xSpeed += world.rand.nextGaussian() * 0.004D * (double)TileEntityCrafter.DISPENSE_SPEED;
		        ySpeed += world.rand.nextGaussian() * 0.004D * (double)TileEntityCrafter.DISPENSE_SPEED;
		        zSpeed += world.rand.nextGaussian() * 0.004D * (double)TileEntityCrafter.DISPENSE_SPEED;
		        
		        ParticleHelper.spawnFlexibleParticle(world, EnumParticleTypes.CLOUD, x, y, z, xSpeed, ySpeed, zSpeed, 30, 0.01F);
//				world.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, motionX, motionY, motionZ, 0);
			}
    	}
		return super.eventReceived(state, world, pos, id, param);
    }
}
