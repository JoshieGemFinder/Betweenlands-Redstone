package com.joshiegemfinder.betweenlandsredstone.handler;

import com.joshiegemfinder.betweenlandsredstone.BLRedstoneConfig;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockBLTarget;
import com.joshiegemfinder.betweenlandsredstone.blocks.BlockSyrmoriteBars;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ProjectileHandler {

	@SubscribeEvent
	public static void onProjectileImpact(ProjectileImpactEvent event) {
		RayTraceResult result = event.getRayTraceResult();
		Entity entity = event.getEntity();
		World world = entity.world;
		if(result.entityHit == null) {
            BlockPos blockpos = result.getBlockPos();
            IBlockState iblockstate = world.getBlockState(blockpos);
            if(iblockstate.getBlock() instanceof BlockBLTarget) {
            	((BlockBLTarget)iblockstate.getBlock()).onHit(world, blockpos, iblockstate, entity, result);
            }
            else if(iblockstate.getBlock() instanceof BlockSyrmoriteBars) {
            	if(BLRedstoneConfig.EXTRA_FEATURES.syrmoriteBarsAllowProjectiles)
            		event.setCanceled(true);
            }
		}
	}
	
}
