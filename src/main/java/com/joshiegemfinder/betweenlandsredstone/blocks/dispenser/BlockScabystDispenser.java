package com.joshiegemfinder.betweenlandsredstone.blocks.dispenser;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BlockStateContainerHelper;

public class BlockScabystDispenser extends BlockDispenser implements IModelInterface, IHidingDispenser {

	public static final PropertyBool HIDDEN = PropertyBool.create("hidden");

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
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomStateMapper(this, DispenserStateMapper.INSTANCE);
	}

	@Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
		TileEntityScabystDispenser tile = new TileEntityScabystDispenser();
        return tile;
    }

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer(super.createBlockState(), HIDDEN);
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
		super.updateTick(worldIn, pos, state, random);

		this.updateHidingState(state, worldIn, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighbourBlock,
			BlockPos neighborPos) {
		//have to c&p this because of the setBlockState flags
        boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
        boolean flag1 = ((Boolean)state.getValue(TRIGGERED)).booleanValue();

        if (flag && !flag1)
        {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)), 2);
        }
        else if (!flag && flag1)
        {
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)), 2);
        }
        

		this.updateHidingState(state, worldIn, pos);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		IBlockState actualState = super.getActualState(state, worldIn, pos);
		
		return actualState.withProperty(HIDDEN, this.shouldDispenserBeHidden(actualState, worldIn, pos));
	}
	
	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int eventID,
			int eventParam) {
		if(eventID == 201) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof ISyncIsEmpty) {
				((ISyncIsEmpty)te).setEmptyClient(eventParam == 0);
			}
			
			this.updateHidingState(state, world, pos);
			world.markBlockRangeForRenderUpdate(pos, pos);
			
			return true;
		}
		
		return super.eventReceived(state, world, pos, eventID, eventParam);
	}

	@Override
	public void updateHidingState(IBlockState state, World worldIn, BlockPos pos) {

		boolean alreadyHidden = state.getValue(HIDDEN).booleanValue();
		boolean shouldBeHidden = this.shouldDispenserBeHidden(state, worldIn, pos);
		
		if(alreadyHidden != shouldBeHidden) {
			worldIn.setBlockState(pos, state.withProperty(HIDDEN, shouldBeHidden), 2 | 16);
			worldIn.markBlockRangeForRenderUpdate(pos, pos);
		}
		
	}
	
	
}
