package com.joshiegemfinder.betweenlandsredstone.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class AttenuatedSound extends PositionedSound implements net.minecraft.client.audio.ITickableSound {

	protected final float attenuationDistance;
	protected final float originalVolume;
	
	protected boolean donePlaying = false;
	
	public AttenuatedSound(BlockPos pos, SoundEvent soundEvent, SoundCategory category, float volume, float pitch, float attenuationDistance) {
		this(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, soundEvent, category, volume, pitch, attenuationDistance);
	}
	
	public AttenuatedSound(float xPosF, float yPosF, float zPosF, SoundEvent soundEvent, SoundCategory category, float volume, float pitch, float attenuationDistance) {
		super(soundEvent, category);

		this.attenuationType = AttenuationType.NONE;
		this.xPosF = xPosF;
		this.yPosF = yPosF;
		this.zPosF = zPosF;
		this.volume = volume;
		this.pitch = pitch;
		
		this.originalVolume = volume;
		this.attenuationDistance = attenuationDistance;
	}
	
	protected float getVolumeMultiplier() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();
		if(view != null) {
			double distance = view.getDistance(this.xPosF, this.yPosF, this.zPosF);
			if(distance < attenuationDistance) {
				return (float)(attenuationDistance - distance) / attenuationDistance;
			}
		}
		return 0;
	}

	@Override
	public void update() {

		Entity view = Minecraft.getMinecraft().getRenderViewEntity();
		
		if(view == null || view.getDistance(this.xPosF, this.yPosF, this.zPosF) > this.attenuationDistance) {
			this.repeat = false;
			this.donePlaying = true;
			this.volume = 0;
		} else {
			this.volume = this.originalVolume * this.getVolumeMultiplier();
		}
		
	}

	@Override
	public boolean isDonePlaying() {
		return this.donePlaying;
	}
	
	@Override
	public float getVolume() {
		return this.volume;
	}
}
