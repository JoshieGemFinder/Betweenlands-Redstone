package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystWorldWrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScabystDispenser extends BlockDispenser implements IModelInterface, IScabystBlock {

	public BlockScabystDispenser(String name) {
		super();

		this.setCreativeTab(CreativeTabs.REDSTONE);
		this.setHardness(3.5F);
		this.setSoundType(SoundType.STONE);
		
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(Main.MODID, name));
		ModBlocks.BLOCKS.add(this);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return new ItemStack(ModItems.SCABYST_DISPENSER);
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ModItems.SCABYST_DISPENSER);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.SCABYST_DISPENSER;
	}
	
	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, TRIGGERED});
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomStateMapper(this, (new StateMap.Builder()).ignore(TRIGGERED).build());
	}

	@Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
		TileEntityScabystDispenser tile = new TileEntityScabystDispenser();
//		tile.setCustomName(I18n.translateToLocal("container.scabyst_dispenser"));
        return tile;
    }

	@Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        boolean flag = ScabystWorldWrapper.isBlockScabystPowered(worldIn, pos) || ScabystWorldWrapper.isBlockScabystPowered(worldIn, pos.up());
        boolean flag1 = ((Boolean)state.getValue(TRIGGERED)).booleanValue();

        if (flag && !flag1)
        {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)), 4);
        }
        else if (!flag && flag1)
        {
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)), 4);
        }
    }
}
