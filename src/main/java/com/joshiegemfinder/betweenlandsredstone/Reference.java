package com.joshiegemfinder.betweenlandsredstone;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Reference {
	
    public static final String MODID = Main.MODID;
    public static final String NAME = Main.NAME;
    public static final String VERSION = Main.VERSION;

    @SideOnly(Side.CLIENT)
    public static final ResourceLocation ITEM_FRAME_MODEL = new ResourceLocation(Main.MODID, "scabyst_item_frame");
    @SideOnly(Side.CLIENT)
    public static final ModelResourceLocation ITEM_FRAME_MODEL_NORMAL = new ModelResourceLocation(ITEM_FRAME_MODEL, "normal");
    @SideOnly(Side.CLIENT)
    public static final ModelResourceLocation ITEM_FRAME_MODEL_MAP = new ModelResourceLocation(ITEM_FRAME_MODEL, "map");
    
	public static final String exclusivityGameRule = "scabystExclusive";
	
}