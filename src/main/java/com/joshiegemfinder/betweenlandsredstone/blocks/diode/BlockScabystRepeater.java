package com.joshiegemfinder.betweenlandsredstone.blocks.diode;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystColor;

import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScabystRepeater extends BlockRedstoneRepeater {
	
	public static final PropertyBool LOCKED = PropertyBool.create("locked");
    public static final PropertyInteger DELAY = PropertyInteger.create("delay", 1, 4);
    
	public BlockScabystRepeater(String name, boolean powered) {
		super(powered);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DELAY, Integer.valueOf(1)).withProperty(LOCKED, Boolean.valueOf(false)));
		this.setHardness(0.0F);
		this.setSoundType(SoundType.WOOD);
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(Main.MODID, name));
		ModBlocks.BLOCKS.add(this);
	}

	@Override
	protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
		return BlockScabystDiode.calculateInputStrength(worldIn, pos, state);
	}
	
	@Override
	protected int getPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return BlockScabystDiode.getPowerOnSide(this, worldIn, pos, side);
	}
    
  	protected IBlockState getPoweredState(IBlockState unpoweredState)
	{
	    Integer integer = (Integer)unpoweredState.getValue(DELAY);
	    Boolean obool = (Boolean)unpoweredState.getValue(LOCKED);
	    EnumFacing enumfacing = (EnumFacing)unpoweredState.getValue(FACING);
	    return ModBlocks.POWERED_SCABYST_REPEATER.getDefaultState().withProperty(FACING, enumfacing).withProperty(DELAY, integer).withProperty(LOCKED, obool);
	}

  	protected IBlockState getUnpoweredState(IBlockState poweredState)
    {
        Integer integer = (Integer)poweredState.getValue(DELAY);
        Boolean obool = (Boolean)poweredState.getValue(LOCKED);
        EnumFacing enumfacing = (EnumFacing)poweredState.getValue(FACING);
        return ModBlocks.UNPOWERED_SCABYST_REPEATER.getDefaultState().withProperty(FACING, enumfacing).withProperty(DELAY, integer).withProperty(LOCKED, obool);
    }

  	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
  		return ModItems.SCABYST_REPEATER;
  	}
  	
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		 return new ItemStack(this);
	}
	
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		 return new ItemStack(this);
	}
	
	@Override
    public boolean isAlternateInput(IBlockState state)
    {
        return BlockScabystDiode.isDiode(state);
    }
	
    @SideOnly(Side.CLIENT)
	//TODO change if making a new class
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (this.isRepeaterPowered)
        {
            EnumFacing enumfacing = (EnumFacing)stateIn.getValue(FACING);
            double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            float f = -5.0F;

            if (rand.nextBoolean())
            {
                f = (float)(((Integer)stateIn.getValue(DELAY)).intValue() * 2 - 1);
            }

            f = f / 16.0F;
            double d3 = (double)(f * (float)enumfacing.getFrontOffsetX());
            double d4 = (double)(f * (float)enumfacing.getFrontOffsetZ());
            

			double red = ScabystColor.shades[15].getR() / 255.0F;
			double green = ScabystColor.shades[15].getG() / 255.0F;
			double blue = ScabystColor.shades[15].getB() / 255.0F;
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0 + d3, d1, d2 + d4, red, green, blue);
        }
    }

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side.getAxis() == state.getValue(FACING).getAxis() && super.canConnectRedstone(state, world, pos, side);
	}
}
