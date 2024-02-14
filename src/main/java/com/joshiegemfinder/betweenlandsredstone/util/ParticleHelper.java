package com.joshiegemfinder.betweenlandsredstone.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ParticleHelper {
	
	@SideOnly(Side.CLIENT)
	public static void spawnFlexibleParticle(World worldIn, EnumParticleTypes type, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int maxAge, float scale) {
		Minecraft mc = Minecraft.getMinecraft();
		int particleSetting = mc.gameSettings.particleSetting;
		if(particleSetting == 1) {
			if(worldIn.rand.nextInt(3) == 0) return;
		} else if(particleSetting == 2) {
			return;
		}
		
		if(mc.getRenderViewEntity().getDistanceSq(x, y, z) > 16 * 16)
			return;
		
//		ParticleCloud particle

		// net.minecraft.client.particle.ParticleManager;
		
		Particle particle = mc.effectRenderer.spawnEffectParticle(type.getParticleID(), x, y, z, xSpeed, ySpeed, zSpeed, 0);
		particle.multipleParticleScaleBy(scale);
		particle.setMaxAge(maxAge);
	}
	
}
