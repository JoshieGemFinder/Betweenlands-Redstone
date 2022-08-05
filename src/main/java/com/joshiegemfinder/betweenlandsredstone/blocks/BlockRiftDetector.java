package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;
import com.joshiegemfinder.betweenlandsredstone.util.Discriminator;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;

import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.event.EventRift;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class BlockRiftDetector extends BlockDaylightDetector implements IModelInterface, IScabystBlock {

	private final boolean inverted;
	
	public BlockRiftDetector(boolean inverted, String name) {
		super(inverted);
		this.inverted = inverted;
        this.setSoundType(SoundType.STONE);
		
		this.setRegistryName(name);
		this.setUnlocalizedName(name);
		
		ModBlocks.BLOCKS.add(this);
	}
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (playerIn.isAllowEdit())
        {
            if (worldIn.isRemote)
            {
                return true;
            }
            else
            {
                if (this.inverted)
                {
                    worldIn.setBlockState(pos, ModBlocks.RIFT_DETECTOR.getDefaultState().withProperty(POWER, state.getValue(POWER)), 4);
                    ModBlocks.RIFT_DETECTOR.updatePower(worldIn, pos);
                }
                else
                {
                    worldIn.setBlockState(pos, ModBlocks.RIFT_DETECTOR_INVERTED.getDefaultState().withProperty(POWER, state.getValue(POWER)), 4);
                    ModBlocks.RIFT_DETECTOR_INVERTED.updatePower(worldIn, pos);
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }
	
	@Override
    public void updatePower(World worldIn, BlockPos pos)
    {
		if(worldIn.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {

            IBlockState iblockstate = worldIn.getBlockState(pos);
            
    		EventRift rift = BetweenlandsWorldStorage.forWorld(worldIn).getEnvironmentEventRegistry().rift;

//    		float power = 15f * rift.getVisibility(Minecraft.getMinecraft().getRenderPartialTicks());
    		float power = 15f * rift.getVisibility(1.0F);
    		int i = MathHelper.clamp((int)Math.ceil(power), 0, 15);
    		
    		if(this.inverted) {
    			//use floor instead of ceil since inverted
    			i = 15 - MathHelper.clamp((int)Math.floor(power), 0, 15);;
    		}
    		
            if (((Integer)iblockstate.getValue(POWER)).intValue() != i)
            {
                worldIn.setBlockState(pos, iblockstate.withProperty(POWER, Integer.valueOf(i)), 3);
            }
		}
		else {
			super.updatePower(worldIn, pos);
		}
    }

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		 return new ItemStack(this);
	}

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ModItems.RIFT_DETECTOR;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModItems.RIFT_DETECTOR);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomStateMapper(this, (new StateMap.Builder()).ignore(POWER).build());
	}
	
	@Override
	public int getScabystWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return super.getWeakPower(blockState, blockAccess, pos, side);
	}
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return IScabystBlock.super.getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return super.canConnectRedstone(state, world, pos, side) && Discriminator.canScabystConnect(world, pos, side);
	}
}
