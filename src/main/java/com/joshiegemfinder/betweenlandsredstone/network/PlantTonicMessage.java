package com.joshiegemfinder.betweenlandsredstone.network;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.util.CropHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PlantTonicMessage implements IMessage {

	public static class PlantTonicMessageHandler implements IMessageHandler<PlantTonicMessage, IMessage> {

		@Override
		public IMessage onMessage(PlantTonicMessage message, MessageContext ctx) {
			
			BlockPos pos = message.pos;
			
			Minecraft mc = Minecraft.getMinecraft();

			if(pos == null) {
				Main.logger.info("Plant tonic message handler aborted due to null pos");
				return null;
			}
			
			mc.addScheduledTask(() -> {
				World world = mc.world;
				if(world != null && world.isRemote) {
					CropHelper.PlantTonicHelper.doPlantTonic(world, pos, message.playSound);
				}
			});
			
			return null;
		}
		
	}
	
	public PlantTonicMessage(){}

	private BlockPos pos;
	private boolean playSound;
	public PlantTonicMessage(BlockPos pos, boolean playSound) {
		this.pos = pos;
		this.playSound = playSound;
	}
	
	public PlantTonicMessage(BlockPos pos) {
		this.pos = pos;
		this.playSound = true;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeBoolean(playSound);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		this.pos = new BlockPos(x, y, z);
		this.playSound = buf.readBoolean();
	}

}
