package com.joshiegemfinder.betweenlandsredstone.network;

import java.util.UUID;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.entity.minecart.EntityScabystMinecartFurnace;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MinecartFacingMessage implements IMessage {

	public static class MinecartFacingMessageHandler implements IMessageHandler<MinecartFacingMessage, IMessage> {

		@Override
		public IMessage onMessage(MinecartFacingMessage message, MessageContext ctx) {
			
			EnumFacing facing = message.facing;
			UUID uuid = message.uuid;
			
			Minecraft mc = Minecraft.getMinecraft();
//			Main.logger.info("minecart message handler start");

//			Main.logger.info("facing is {}", facing);
//			Main.logger.info("uuid is {}", uuid);

			if(facing == null) {
				Main.logger.info("minecart message handler aborted due to null facing");
				return null;
			}
			
			if(uuid == null) {
				Main.logger.info("minecart message handler aborted due to null uuid");
				return null;
			}
			
			mc.addScheduledTask(() -> {
//				Main.logger.info("minecart message main thread start");
				World world = mc.world;
				if(world != null && world.isRemote) {
//					Main.logger.info("minecart message recieved in valid world and on client");
					for(Entity entity : world.loadedEntityList) {
						if(entity.getUniqueID().equals(uuid)) {
//							Main.logger.info("entity found");
							if(entity instanceof EntityScabystMinecartFurnace) {
//								Main.logger.info("entity instanceof minecart");
								EntityScabystMinecartFurnace minecart = (EntityScabystMinecartFurnace)entity;
								minecart.setFacing(facing);
//								Main.logger.info("entity facing set");
								break;
							}
						}
					}
				}
			});
			
			return null;
		}
		
	}
	
	public MinecartFacingMessage(){}
	
	private EnumFacing facing;
	private UUID uuid;
	public MinecartFacingMessage(EnumFacing facing, UUID uuid) {
		this.facing = facing;
		this.uuid = uuid;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(facing != null);
		if(facing != null && uuid != null) {
//			Main.logger.info("setting minecart facing to {}", facing);
//			Main.logger.info("setting minecart facing index to {}", facing.getHorizontalIndex());
			buf.writeInt(facing.getHorizontalIndex());
			String uuid1 = this.uuid.toString();
//			Main.logger.info("setting minecart uuid to {}", uuid1);
			int length = uuid1.length();
			buf.writeInt(length);
			for(int i = 0; i < length; i++) {
				buf.writeChar(uuid1.charAt(i));
			}
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
//		Main.logger.info("minecart message recieved");
		if(buf.readBoolean()) {
//			Main.logger.info("minecart message claims to be right");
			EnumFacing facing1 = EnumFacing.getHorizontal(buf.readInt());
//			Main.logger.info("minecart message facing is {}", facing);
			String uuid1 = "";
			int length = buf.readInt();
			for(int i = 0; i < length; i++) {
				uuid1 = uuid1 + buf.readChar();
			}
//			Main.logger.info("minecart message is valid");
			UUID entityUUID = UUID.fromString(uuid1);
			this.uuid = entityUUID;
			this.facing = facing1;
		}
	}

}
