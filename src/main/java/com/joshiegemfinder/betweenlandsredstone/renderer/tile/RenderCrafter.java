package com.joshiegemfinder.betweenlandsredstone.renderer.tile;

import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityCrafter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;

public class RenderCrafter extends TileEntitySpecialRenderer<TileEntityCrafter> {

	// Credit to the BL devs, who I took most of this code from
	
	@Override
	public void render(TileEntityCrafter crafter, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		//only shift to the center of the block
		GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
		EnumFacing facing = crafter.getFacing();
		//the default rotations are broken, resorting to hard-coding
		if(facing.getAxis() == Axis.Z) {
			GlStateManager.rotate(facing == EnumFacing.SOUTH ? 0.0F : 180.0F, 0.0F, 1.0F, 0.0F);
		} else if(facing.getAxis() == Axis.X) {
			GlStateManager.rotate(facing == EnumFacing.EAST ? 90.0F : 270.0F, 0.0F, 1.0F, 0.0F);
		} else {
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(facing == EnumFacing.DOWN ? 90.0F : 270.0F, 1.0F, 0.0F, 0.0F);
		}
		// now shift up the rest of the way
		GlStateManager.translate(0, 0.375D, 0);
		
		//rest of the item transformations
		GlStateManager.scale(0.25F, 0.25F, 0.25F);
		GlStateManager.translate(-1.5F, -0.0F, -1.0F);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		float prevLGTX = OpenGlHelper.lastBrightnessX;
		float prevLGTY = OpenGlHelper.lastBrightnessY;
		BlockPos pos = crafter.getPos();
		int bright = crafter.getWorld().getCombinedLight(pos.up(), 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, bright % 65536, bright / 65536);

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				ItemStack stack = crafter.slots.get(column * 3 + row);
				if (!stack.isEmpty()) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(row * 0.75F, 0.0D, column * 0.75F);
					GlStateManager.translate(0.75F, 0.52F, 0.25F);
					GlStateManager.scale(0.5F, 0.5F, 0.5F);
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
					RenderHelper.disableStandardItemLighting();
					RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
					renderItem.renderItem(stack, renderItem.getItemModelWithOverrides(stack, null, null));
					GlStateManager.popMatrix();
				}
			}
		}
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLGTX, prevLGTY);

		GlStateManager.popMatrix();
	}
	
}
