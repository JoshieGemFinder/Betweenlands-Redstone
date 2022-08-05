package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.util.Discriminator;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystColor;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystWorldWrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScabystTorch extends BlockTorch implements IScabystBlock {

	
	public BlockScabystTorch(String name, boolean isOn) {
		//BlockTorch
	    super();
	    this.setCreativeTab(null);
	    this.setSoundType(SoundType.WOOD);
	    //BlockRedstoneTorch
	    this.isOn = isOn;
	    
	    //BlockScabystTorch
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(Main.MODID, name));
		ModBlocks.BLOCKS.add(this);
	}
	
	//Change Stuff here
	
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
	    return new ItemStack(ModItems.SCABYST_TORCH);
	}
	
	//TODO change this if making a new class
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		 return new ItemStack(this);
	}
	
	//TODO change this if making a new class
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.SCABYST_TORCH;
	}
	
	//TODO change this if making a new class
	public boolean isAssociatedBlock(Block other)
	{
	   return other == ModBlocks.UNLIT_SCABYST_TORCH || other == ModBlocks.SCABYST_TORCH;
	}
	
	@SuppressWarnings("unused")
	@SideOnly(Side.CLIENT)
	//TODO change this if making a new class
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
	    if (this.isOn)
	    {
	        double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d1 = (double)pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        EnumFacing enumfacing = (EnumFacing)stateIn.getValue(FACING);
	
	        if (enumfacing.getAxis().isHorizontal())
	        {
	            EnumFacing enumfacing1 = enumfacing.getOpposite();
	            double d3 = 0.27D;
	            d0 += 0.27D * (double)enumfacing1.getFrontOffsetX();
	            d1 += 0.22D;
	            d2 += 0.27D * (double)enumfacing1.getFrontOffsetZ();
	        }
	        
	        double red = ScabystColor.shades[15].getR() / 255.0F;
			double green = ScabystColor.shades[15].getG() / 255.0F;
			double blue = ScabystColor.shades[15].getB() / 255.0F;
	
	        worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, red, green, blue);
	    }
	}
	
	//RedstoneTorch implementation
	
	private static final Map<World, List<BlockScabystTorch.Toggle>> toggles = new java.util.WeakHashMap<World, List<Toggle>>(); // FORGE - fix vanilla MC-101233
    private final boolean isOn;

    private boolean isBurnedOut(World worldIn, BlockPos pos, boolean turnOff)
    {
        if (!toggles.containsKey(worldIn))
        {
            toggles.put(worldIn, Lists.newArrayList());
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
		List<BlockScabystTorch.Toggle> list = (List)toggles.get(worldIn);

        if (turnOff)
        {
            list.add(new BlockScabystTorch.Toggle(pos, worldIn.getTotalWorldTime()));
        }

        int i = 0;

        for (int j = 0; j < list.size(); ++j)
        {
        	BlockScabystTorch.Toggle blockredstonetorch$toggle = list.get(j);

            if (blockredstonetorch$toggle.pos.equals(pos))
            {
                ++i;

                if (i >= 8)
                {
                    return true;
                }
            }
        }

        return false;
    }
    
    public int tickRate(World worldIn)
    {
        return 2;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.isOn)
        {
            for (EnumFacing enumfacing : EnumFacing.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.isOn)
        {
            for (EnumFacing enumfacing : EnumFacing.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
        }
    }

    public int getScabystWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return this.isOn && blockState.getValue(FACING) != side ? 15 : 0;
    }

    private boolean shouldBeOff(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = ((EnumFacing)state.getValue(FACING)).getOpposite();
        return ScabystWorldWrapper.isSideScabystPowered(worldIn, pos.offset(enumfacing), enumfacing);
    }

    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
    }

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        boolean flag = this.shouldBeOff(worldIn, pos, state);
        @SuppressWarnings({ "rawtypes", "unchecked" })
		List<BlockScabystTorch.Toggle> list = (List)toggles.get(worldIn);

        while (list != null && !list.isEmpty() && worldIn.getTotalWorldTime() - (list.get(0)).time > 60L)
        {
            list.remove(0);
        }

        if (this.isOn)
        {
            if (flag)
            {
            	//TODO Change these or they get converted to the wrong thing
                worldIn.setBlockState(pos, ModBlocks.UNLIT_SCABYST_TORCH.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);

                if (this.isBurnedOut(worldIn, pos, true))
                {
                    worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                    for (int i = 0; i < 5; ++i)
                    {
                        double d0 = (double)pos.getX() + rand.nextDouble() * 0.6D + 0.2D;
                        double d1 = (double)pos.getY() + rand.nextDouble() * 0.6D + 0.2D;
                        double d2 = (double)pos.getZ() + rand.nextDouble() * 0.6D + 0.2D;
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }

                    worldIn.scheduleUpdate(pos, worldIn.getBlockState(pos).getBlock(), 160);
                }
            }
        }
        else if (!flag && !this.isBurnedOut(worldIn, pos, false))
        {
            worldIn.setBlockState(pos, ModBlocks.SCABYST_TORCH.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.onNeighborChangeInternal(worldIn, pos, state))
        {
            if (this.isOn == this.shouldBeOff(worldIn, pos, state))
            {
                worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            }
        }
    }
    
    public int getScabystStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.DOWN ? getScabystWeakPower(blockState, blockAccess, pos, side) : 0;
    }
    
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return IScabystBlock.super.getWeakPower(blockState, blockAccess, pos, side);
    }
    
    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return IScabystBlock.super.getStrongPower(blockState, blockAccess, pos, side);
    }

    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }
    
	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return super.canConnectRedstone(state, world, pos, side) && Discriminator.canScabystConnect(world, pos, side);
	}
    
    static class Toggle
    {
        BlockPos pos;
        long time;

        public Toggle(BlockPos pos, long time)
        {
            this.pos = pos;
            this.time = time;
        }
    }
	
}
