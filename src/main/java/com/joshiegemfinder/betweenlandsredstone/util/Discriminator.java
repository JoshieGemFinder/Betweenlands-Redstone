package com.joshiegemfinder.betweenlandsredstone.util;

import java.util.ArrayList;
import java.util.List;

import com.joshiegemfinder.betweenlandsredstone.BLRedstoneConfig;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.common.registries.BlockRegistry;

public class Discriminator {
	
	public static enum DiscriminatorContext {
		SCABYST,
		BOTH,
		REDSTONE
	}
	
	private static List<DiscriminatorContext> contextStack = new ArrayList<DiscriminatorContext>();
	
	public static DiscriminatorContext context = DiscriminatorContext.BOTH;

	public static void pushContext(DiscriminatorContext context) {
		contextStack.add(Discriminator.context);
		Discriminator.context = context;
	}
	
	public static void popContext() {
		if(contextStack.size() == 0) {
			Discriminator.context = DiscriminatorContext.BOTH;
			return;
		}
		Discriminator.context = contextStack.remove(contextStack.size() - 1);
	}
	
	public static boolean on() {
		return BLRedstoneConfig.scabystExclusivity;
	}
	
	public static boolean non() {
		return !BLRedstoneConfig.scabystExclusivity;
	}
	
	public static void setContext(DiscriminatorContext newContext) {
		context = newContext;
	}
	
	public static DiscriminatorContext getContext() {
		if(non()) {
			return DiscriminatorContext.BOTH;
		} else {
			return context;
		}
	}
	
	public static boolean canScabystProvidePower() {
		return non() || context == DiscriminatorContext.BOTH || context == DiscriminatorContext.SCABYST;
	}
	
	public static boolean canRedstoneProvidePower() {
		return non() || context == DiscriminatorContext.BOTH || context == DiscriminatorContext.REDSTONE;
	}
	
	public static boolean isScabystBlock(Block block) {
		return block instanceof IScabystBlock || BlockRegistry.BLOCKS.contains(block);
	}
	
	public static boolean isRedstoneBlock(Block block) {
		return !(block instanceof IScabystBlock);
	}
	
	public static boolean canProvidePower(Block block) {
		return non() || (isScabystBlock(block) && canScabystProvidePower()) || (isRedstoneBlock(block) && canRedstoneProvidePower());
	}
	
	public static boolean canScabystConnect(IBlockAccess access, BlockPos pos, EnumFacing side) {
		return non() || (side == null || isScabystBlock(access.getBlockState(pos.offset(side.getOpposite())).getBlock()));
	}
	
	public static boolean canRedstoneConnect(IBlockAccess access, BlockPos pos, EnumFacing side) {
		return non() || (side == null || isRedstoneBlock(access.getBlockState(pos.offset(side.getOpposite())).getBlock()));
	}
	
	public static boolean canProvidePower(IBlockState state) {
		return non() || canProvidePower(state.getBlock());
	}
	
	public static int getProvidedWeakPower(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
		Block block = state.getBlock();
		boolean scab = isScabystBlock(block);
		if(scab) {
			if(canScabystProvidePower()) {
				if(block instanceof IScabystBlock) {
					return ((IScabystBlock)block).getScabystWeakPower(state, access, pos, side);
				} else {
//					return canScabystConnect(access, pos, side) ? state.getWeakPower(access, pos, side) : 0;
					return state.getWeakPower(access, pos, side);
				}
			} else {
				return 0;
			}
		} else {
			if(canRedstoneProvidePower()) {
				return state.getWeakPower(access, pos, side);
			} else {
				return 0;
			}
		}
	}
	
	public static int getProvidedStrongPower(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
		Block block = state.getBlock();
		boolean scab = isScabystBlock(block);
		if(scab) {
			if(canScabystProvidePower()) {
				if(block instanceof IScabystBlock) {
					return ((IScabystBlock)block).getScabystStrongPower(state, access, pos, side);
				} else {
//					return canScabystConnect(access, pos, side) ? state.getStrongPower(access, pos, side) : 0;
					return state.getStrongPower(access, pos, side);
				}
			} else {
				return 0;
			}
		} else {
			if(canRedstoneProvidePower()) {
				return state.getStrongPower(access, pos, side);
			} else {
				return 0;
			}
		}
	}
}
