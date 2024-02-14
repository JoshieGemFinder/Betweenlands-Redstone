package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBLTarget extends Block implements IModelInterface {

	public static final PropertyInteger POWER = BlockRedstoneWire.POWER;
	
	public final boolean isCircular;
	
	public BlockBLTarget(boolean isCircular) {
		super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(0.5F);
		
		this.isCircular = isCircular;
		this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
	}
	
	public void onHit(World worldIn, BlockPos pos, IBlockState state, Entity entityIn, RayTraceResult resultIn) {
		if(entityIn instanceof IProjectile) {
			int power = getPower(resultIn);
			worldIn.setBlockState(pos, state.withProperty(POWER, power), 1 | 2);
			int delay = entityIn instanceof EntityArrow ? 20 : 8;
			worldIn.scheduleUpdate(pos, this, delay);
		}
	}
	
	@Override
	public boolean getTickRandomly() {
		return false;
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if ((Integer) state.getValue(POWER) != 0) {
			worldIn.setBlockState(pos, state.withProperty(POWER, 0), 3);
		}
	}
	
	protected int getPower(RayTraceResult result) {
		Vec3d pos = result.hitVec;
		double x = Math.abs(MathHelper.frac(pos.x) - 0.5D);
		double y = Math.abs(MathHelper.frac(pos.y) - 0.5D);
		double z = Math.abs(MathHelper.frac(pos.z) - 0.5D);
		Axis axis = result.sideHit.getAxis();
		double dist;
		if(isCircular) {
			if(axis == Axis.Y) {
				dist = Math.hypot(x, z);
			} else if(axis == Axis.Z) {
				dist = Math.hypot(x, y);
			} else {
				dist = Math.hypot(y, z);
			}
			dist = Math.min(dist, 1);
		} else {
			if(axis == Axis.Y) {
				dist = Math.max(x, z);
			} else if(axis == Axis.Z) {
				dist = Math.max(x, y);
			} else {
				dist = Math.max(y, z);
			}
		}
		return Math.max(1, MathHelper.ceil(15.0D * MathHelper.clamp((0.5D - dist) / 0.5D, 0.0D, 1.0D)));
	}
	
	@Override
	public boolean canProvidePower(IBlockState state) {
		return state.getValue(POWER) > 0;
	}
	
	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side != null;
	}
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		int power = blockState.getValue(POWER);
		if(power >= 15) 
			return 15;

		for(EnumFacing facing : EnumFacing.VALUES) {
			int power2 = blockAccess.getStrongPower(pos.offset(facing), facing);
			if(power2 > power)
				power = power2;
			if(power >= 15)
				return power;
		}
		
		return power;
	}
	
	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {POWER});
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(POWER, meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POWER);
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state) {
		return true;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		 return new ItemStack(this);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.TARGET_BLOCK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomStateMapper(this, (new StateMap.Builder()).ignore(POWER).build());
	}
}
