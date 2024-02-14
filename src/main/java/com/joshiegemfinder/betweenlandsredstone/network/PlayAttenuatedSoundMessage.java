package com.joshiegemfinder.betweenlandsredstone.network;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayAttenuatedSoundMessage implements IMessage {

	public static class PlayAttenuatedSoundMessageHandler extends SidedMessageHandler<PlayAttenuatedSoundMessage, IMessage> {

		public PlayAttenuatedSoundMessageHandler() {
			super(Side.CLIENT);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public IMessage handle(PlayAttenuatedSoundMessage message, MessageContext ctx) {

			if(message.soundEvent == null || message.category == null) {
				BetweenlandsRedstone.logger.info("Attenuated Sound message handler aborted due to invalid sound event/category");
				return null;
			}

			Minecraft.getMinecraft().addScheduledTask(() -> {
				BetweenlandsRedstone.proxy.playAttenuatedSound(message.xPosF, message.yPosF, message.zPosF, message.soundEvent, message.category, message.volume, message.pitch, message.attenuationDistance);
			});
			
			return null;
		}
		
	}
	
	public PlayAttenuatedSoundMessage(){}

	private float xPosF;
	private float yPosF;
	private float zPosF;
	private SoundEvent soundEvent;
	private SoundCategory category;
	private float volume;
	private float pitch;
	private float attenuationDistance;
	
	public PlayAttenuatedSoundMessage(BlockPos pos, SoundEvent soundEvent, SoundCategory category, float volume, float pitch, float attenuationDistance) {
		this(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, soundEvent, category, volume, pitch, attenuationDistance);
	}
	
	public PlayAttenuatedSoundMessage(float xPosF, float yPosF, float zPosF, SoundEvent soundEvent, SoundCategory category, float volume, float pitch, float attenuationDistance) {
		this.xPosF = xPosF;
		this.yPosF = yPosF;
		this.zPosF = zPosF;
		this.soundEvent = soundEvent;
		this.category = category;
		this.volume = volume;
		this.pitch = pitch;
		this.attenuationDistance = attenuationDistance;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(xPosF);
		buf.writeFloat(yPosF);
		buf.writeFloat(zPosF);
		buf.writeInt(SoundEvent.REGISTRY.getIDForObject(soundEvent));
		buf.writeInt(category.ordinal());
		buf.writeFloat(volume);
		buf.writeFloat(pitch);
		buf.writeFloat(attenuationDistance);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		xPosF = buf.readFloat();
		yPosF = buf.readFloat();
		zPosF = buf.readFloat();
		soundEvent = SoundEvent.REGISTRY.getObjectById(buf.readInt());
		category = SoundCategory.values()[buf.readInt()];
		volume = buf.readFloat();
		pitch = buf.readFloat();
		attenuationDistance = buf.readFloat();
	}

}
