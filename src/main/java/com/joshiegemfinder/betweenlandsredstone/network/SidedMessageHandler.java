package com.joshiegemfinder.betweenlandsredstone.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SidedMessageHandler<REQ extends IMessage, REPLY extends IMessage> implements IMessageHandler<REQ, REPLY> {

	public final Side side;
	
	public SidedMessageHandler(Side sideFor) {
		this.side = sideFor;
	}

	@Override
	public REPLY onMessage(REQ message, MessageContext ctx) {
		if(ctx.side == side) {
			return this.handle(message, ctx);
		}
		return null;
	}
	
	protected REPLY handle(REQ message, MessageContext ctx) {
		return null;
	}
	
}
