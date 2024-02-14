package com.joshiegemfinder.betweenlandsredstone.gui;

import com.joshiegemfinder.betweenlandsredstone.BLRedstoneConfig;
import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityCrafter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiCrafter extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(BetweenlandsRedstone.MODID, "textures/gui/container/crafter.png");
	public static final ResourceLocation TEXTURE_NOTPRETTY = new ResourceLocation(BetweenlandsRedstone.MODID, "textures/gui/container/crafter_notpretty.png");
	
	private final TileEntityCrafter tileentitycrafter;
	private final InventoryPlayer playerinventory;
	
	public GuiCrafter(InventoryPlayer player, TileEntityCrafter crafter) {
		super(new ContainerCrafter(player, crafter));
		this.playerinventory = player;
		this.tileentitycrafter = crafter;
		
		this.xSize = 176;
		this.ySize = 166;
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String displayName = this.tileentitycrafter.getDisplayName().getUnformattedText();
		if(!BLRedstoneConfig.RENDERING.prettyCrafterTextures) {
			this.fontRenderer.drawString(displayName, (this.xSize - this.fontRenderer.getStringWidth(displayName)) / 2, 6, 0x404040);
			this.fontRenderer.drawString(this.playerinventory.getDisplayName().getUnformattedText(), 7, 72, 0x404040);
		} else {
			this.fontRenderer.drawString(displayName, (this.xSize - this.fontRenderer.getStringWidth(displayName)) / 2, 7, 0x202020);
			this.fontRenderer.drawString(this.playerinventory.getDisplayName().getUnformattedText(), 7, 72, 0xE7E7E7);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BLRedstoneConfig.RENDERING.prettyCrafterTextures ? TEXTURE : TEXTURE_NOTPRETTY);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		for(int i = 0; i < 9; ++i) {
			SlotCrafter slot = (SlotCrafter)this.inventorySlots.inventorySlots.get(i);
			if(slot.isDisabled()) {
				this.drawTexturedModalRect(this.guiLeft + slot.xPos - 1, this.guiTop + slot.yPos - 1, this.xSize, 0, 18, 18);
			}
		}
		
		this.drawTexturedModalRect(this.guiLeft + 97, this.guiTop + 35, this.xSize, this.tileentitycrafter.isTriggered() ? 18 + 16 : 18, 16, 16);
	}
	
	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType clickType) {
		super.handleMouseClick(slotIn, slotId, mouseButton, clickType);

		if(slotIn instanceof SlotCrafter && ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && 
			playerinventory.getItemStack().isEmpty() && this.inventorySlots.inventorySlots.get(slotId).getStack().isEmpty())) {
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
	}
}
