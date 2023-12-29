package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.List;

import com.joshiegemfinder.betweenlandsredstone.BLRedstoneConfig;

import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class BlockSyrmoriteBars extends BlockPane {
	public BlockSyrmoriteBars(Material materialIn, boolean canDrop) {
		super(materialIn, canDrop);
		
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		if(BLRedstoneConfig.EXTRA_FEATURES.syrmoriteBarsAllowProjectiles && (entityIn instanceof IProjectile || entityIn instanceof IThrowableEntity)) {
			collidingBoxes.clear();
			return;
		}
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		if(BLRedstoneConfig.EXTRA_FEATURES.syrmoriteBarsAllowProjectiles) {
			tooltip.add(I18n.translateToLocal("tooltip.betweenlandsredstone.bars_allow_projectiles"));
		}
		super.addInformation(stack, player, tooltip, advanced);
	}
}
