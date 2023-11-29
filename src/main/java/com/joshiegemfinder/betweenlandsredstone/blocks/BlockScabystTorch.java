package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.List;
import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.util.Connector;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystColor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityStalker;

public class BlockScabystTorch extends BlockRedstoneTorch {
	
	private final boolean isOn;
	
	public BlockScabystTorch(String name, boolean isOn) {
		//BlockTorch
	    super(isOn);
//	    this.setCreativeTab(null);
//	    this.setSoundType(SoundType.WOOD);
//	    //BlockRedstoneTorch
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

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        boolean flag = this.shouldBeOff(worldIn, pos, state);
        @SuppressWarnings({ "rawtypes", "unchecked" })
		List<BlockRedstoneTorch.Toggle> list = (List)BlockRedstoneTorch.toggles.get(worldIn);

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
    
	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return super.canConnectRedstone(state, world, pos, side) && Connector.canScabystConnect(world, pos, side); // don't connect to vanilla redstone dust bc yes
	}
	
	//stop stalkers munching on blocks
    @Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		if(entity instanceof EntityStalker) {
			return false;
		}
		return super.canEntityDestroy(state, world, pos, entity);
	}
}
