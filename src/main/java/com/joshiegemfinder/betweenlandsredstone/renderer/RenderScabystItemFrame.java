package com.joshiegemfinder.betweenlandsredstone.renderer;

import com.joshiegemfinder.betweenlandsredstone.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderItemFrame;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderScabystItemFrame extends RenderItemFrame {
	
    private static final ResourceLocation MAP_BACKGROUND_TEXTURES = new ResourceLocation("textures/map/map_background.png");
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ModelResourceLocation itemFrameModel = Reference.ITEM_FRAME_MODEL_NORMAL;
    private final ModelResourceLocation mapModel = Reference.ITEM_FRAME_MODEL_MAP;
    private final RenderItem itemRenderer;

    public static final IRenderFactory<EntityItemFrame> FACTORY = RenderScabystItemFrame::new;

	public RenderScabystItemFrame(RenderManager renderManagerIn) {
		super(renderManagerIn, Minecraft.getMinecraft().getRenderItem());
		itemRenderer = Minecraft.getMinecraft().getRenderItem();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void doRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        BlockPos blockpos = entity.getHangingPosition();
        double d0 = (double)blockpos.getX() - entity.posX + x;
        double d1 = (double)blockpos.getY() - entity.posY + y;
        double d2 = (double)blockpos.getZ() - entity.posZ + z;
        GlStateManager.translate(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);
        GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.renderModel(entity, mc);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        GlStateManager.translate(0.0F, 0.0F, 0.4375F);
        this.renderItem(entity);
        GlStateManager.popMatrix();
        super.renderName(entity, x + (double)((float)entity.facingDirection.getFrontOffsetX() * 0.3F), y - 0.25D, z + (double)((float)entity.facingDirection.getFrontOffsetZ() * 0.3F));
    }
	
	protected void renderModel(EntityItemFrame entity, Minecraft mc) {
		BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        IBakedModel ibakedmodel;
		@SuppressWarnings("unused")
		ItemStack displayStack = entity.getDisplayedItem();

		ModelResourceLocation location = this.itemFrameModel;
		
		if (entity.getDisplayedItem().getItem() instanceof net.minecraft.item.ItemMap)
        {
            location = this.mapModel;
        }
		
        ibakedmodel = modelmanager.getModel(location);
        
//		Main.logger.info("ibakedmodel is default = {}, location is null = {}", ibakedmodel == modelmanager.getMissingModel(), location == null);
		
		blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@SideOnly(Side.CLIENT)
    private void renderItem(EntityItemFrame itemFrame)
    {
        ItemStack itemstack = itemFrame.getDisplayedItem();

        if (!itemstack.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            boolean flag = itemstack.getItem() instanceof net.minecraft.item.ItemMap;
            int i = flag ? itemFrame.getRotation() % 4 * 2 : itemFrame.getRotation();
            GlStateManager.rotate((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);

            net.minecraftforge.client.event.RenderItemInFrameEvent event = new net.minecraftforge.client.event.RenderItemInFrameEvent(itemFrame, this);
            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
            {
            if (flag)
            {
                this.renderManager.renderEngine.bindTexture(MAP_BACKGROUND_TEXTURES);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                float f = 0.0078125F;
                GlStateManager.scale(f, f, f);
                GlStateManager.translate(-64.0F, -64.0F, 0.0F);
                MapData mapdata = ((net.minecraft.item.ItemMap) itemstack.getItem()).getMapData(itemstack, itemFrame.world);
                GlStateManager.translate(0.0F, 0.0F, -1.0F);

                if (mapdata != null)
                {
                    this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
                }
            }
            else
            {
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                GlStateManager.pushAttrib();
                RenderHelper.enableStandardItemLighting();
                this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popAttrib();
            }
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
