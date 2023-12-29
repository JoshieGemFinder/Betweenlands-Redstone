package com.joshiegemfinder.betweenlandsredstone.handler;

import java.lang.reflect.Method;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@SuppressWarnings("deprecation")
@EventBusSubscriber
public class HoneyJumpHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onJump(LivingJumpEvent event) {
		if(event.getEntityLiving() != null && event.getEntityLiving().world != null/* && event.getEntityLiving().world.isRemote*/) {
			EntityLivingBase entity = event.getEntityLiving();
			World world = entity.world;
			BlockPos pos = new BlockPos(entity);
			BlockPos posDown = pos.down();
			if(
					(world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() == ModBlocks.SCABYST_SLIME_2)
					||
					(world.isBlockLoaded(posDown) && world.getBlockState(posDown).getBlock() == ModBlocks.SCABYST_SLIME_2)
			) {
				//this the most accurate way we can get (and modify) this
				double f = 0.42F;
				try {
					Method getJumpHeight = ReflectionHelper.findMethod(EntityLivingBase.class, "getJumpUpwardsMotion", "func_175134_bD");
					f = (float)getJumpHeight.invoke(entity);
				} catch(Exception e) {
					e.printStackTrace();
					BetweenlandsRedstone.logger.info("Error getting default jump height for entity {}. This shouldn't happen in normal gameplay.", EntityList.getEntityString(entity));
				}
				f = f * 0.5F;

		        if (entity.isPotionActive(MobEffects.JUMP_BOOST))
		        {
		            f += (double)((float)(entity.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
		        }
//				entity.motionY = Math.min(entity.motionY, f * 0.5f);
				entity.motionY = f;
			}
		}
	}
	
}
