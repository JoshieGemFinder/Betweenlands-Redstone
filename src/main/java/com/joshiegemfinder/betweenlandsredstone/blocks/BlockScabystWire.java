package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.util.Connector;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystColor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScabystWire extends BlockRedstoneWire {
	
	public BlockScabystWire(String name) {
		super();
		this.setSoundType(SoundType.STONE);
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(BetweenlandsRedstone.MODID, name));
		ModBlocks.BLOCKS.add(this);
		
		this.blocksNeedingUpdate = Blocks.REDSTONE_WIRE.blocksNeedingUpdate;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return this.canProvidePower();
	}
	
	protected boolean canProvidePower() {
		return Blocks.REDSTONE_WIRE.canProvidePower;
	}
	
	protected void setCanProvidePower(boolean canProvidePower) {
		Blocks.REDSTONE_WIRE.canProvidePower = canProvidePower;
	}
	
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
        state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
        state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
        state = state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
        return state;
    }
	
	@Override
	protected BlockRedstoneWire.EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction)
    {
        BlockPos blockpos = pos.offset(direction);
        IBlockState iblockstate = worldIn.getBlockState(pos.offset(direction));

        if (!BlockScabystWire.canConnectTo(worldIn.getBlockState(blockpos), direction, worldIn, blockpos) && (iblockstate.isNormalCube() || !BlockScabystWire.canConnectUpwardsTo(worldIn, blockpos.down())))
        {
            IBlockState iblockstate1 = worldIn.getBlockState(pos.up());

            if (!iblockstate1.isNormalCube())
            {
                boolean flag = worldIn.getBlockState(blockpos).isSideSolid(worldIn, blockpos, EnumFacing.UP) || worldIn.getBlockState(blockpos).getBlock() == Blocks.GLOWSTONE;

                if (flag && BlockScabystWire.canConnectUpwardsTo(worldIn, blockpos.up()))
                {
                    if (iblockstate.isBlockNormalCube())
                    {
                        return BlockRedstoneWire.EnumAttachPosition.UP;
                    }

                    return BlockRedstoneWire.EnumAttachPosition.SIDE;
                }
            }

            return BlockRedstoneWire.EnumAttachPosition.NONE;
        }
        else
        {
            return BlockRedstoneWire.EnumAttachPosition.SIDE;
        }
    }
	
	//TODO Change if making new class
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.SCABYST_DUST;
	}
    
	//gotta stay
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (!this.canProvidePower())
        {
            return 0;
        } else if(!Connector.canScabystConnect(blockAccess, pos, side)) {
        	return 0;
        }
        else
        {
            int i = ((Integer)blockState.getValue(POWER)).intValue();

            if (i == 0)
            {
                return 0;
            }
            else if (side == EnumFacing.UP)
            {
                return i;
            }
            else
            {
                EnumSet<EnumFacing> enumset = EnumSet.<EnumFacing>noneOf(EnumFacing.class);

                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                {
                    if (this.isPowerSourceAt(blockAccess, pos, enumfacing))
                    {
                        enumset.add(enumfacing);
                    }
                }

                if (side.getAxis().isHorizontal() && enumset.isEmpty())
                {
                    return i;
                }
                else if (enumset.contains(side) && !enumset.contains(side.rotateYCCW()) && !enumset.contains(side.rotateY()))
                {
                    return i;
                }
                else
                {
                	IBlockState state = getActualState(blockState, blockAccess, pos);
                	switch(side.getOpposite()) {
	            		case NORTH: 
	            			return state.getValue(BlockRedstoneWire.NORTH) == EnumAttachPosition.SIDE ? i : 0;
	            		case SOUTH:
	            			return state.getValue(BlockRedstoneWire.SOUTH) == EnumAttachPosition.SIDE ? i : 0;
	            		case EAST:
	            			return state.getValue(BlockRedstoneWire.EAST) == EnumAttachPosition.SIDE ? i : 0;
	            		case WEST:
	            			return state.getValue(BlockRedstoneWire.WEST) == EnumAttachPosition.SIDE ? i : 0;
            			default:
            				return 0;
                	}
                }
            }
        }
    }

    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return !this.canProvidePower() ? 0 : blockState.getWeakPower(blockAccess, pos, side);
    }
	
    //TODO Change if making new class
    private boolean isPowerSourceAt(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        BlockPos blockpos = pos.offset(side);
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        Block block = iblockstate.getBlock();
        
        boolean flag = iblockstate.isNormalCube();
        boolean flag1 = worldIn.getBlockState(pos.up()).isNormalCube();

        if (!flag1 && flag && BlockScabystWire.canConnectUpwardsTo(worldIn, blockpos.up()))
        {
            return true;
        }
        else if (BlockScabystWire.canConnectTo(iblockstate, side, worldIn, pos))
        {
            return true;
        }
        else if ((block == Blocks.POWERED_REPEATER || block == ModBlocks.POWERED_SCABYST_REPEATER) && iblockstate.getValue(BlockRedstoneDiode.FACING) == side)
        {
            return true;
        }
        else if (block == ModBlocks.TARGET_BLOCK && side.getAxis() != Axis.Y) {
        	return true;
        }
        else
        {
            return !flag && BlockScabystWire.canConnectUpwardsTo(worldIn, blockpos.down());
        }
    }
    
    @Override //necessary
	protected IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state)
	{
	    IBlockState iblockstate = state;
	    int i = ((Integer)state.getValue(POWER)).intValue();
	    int strength = 0;
	    strength = this.getMaxCurrentStrength(worldIn, pos2, strength);
	    this.setCanProvidePower(false);
	    int k = worldIn.isBlockIndirectlyGettingPowered(pos1);
	    this.setCanProvidePower(true);
	
	    if (k > 0 && k > strength - 1)
	    {
	        strength = k;
	    }
	
	    int l = 0;
	
	    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
	    {
	        BlockPos blockpos = pos1.offset(enumfacing);
	        boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();
	
	        if (flag)
	        {
	            l = this.getMaxCurrentStrength(worldIn, blockpos, l);
	        }
	
	        if (worldIn.getBlockState(blockpos).isNormalCube() && !worldIn.getBlockState(pos1.up()).isNormalCube())
	        {
	            if (flag && pos1.getY() >= pos2.getY())
	            {
	                l = this.getMaxCurrentStrength(worldIn, blockpos.up(), l);
	            }
	        }
	        else if (!worldIn.getBlockState(blockpos).isNormalCube() && flag && pos1.getY() <= pos2.getY())
	        {
	            l = this.getMaxCurrentStrength(worldIn, blockpos.down(), l);
	        }
	    }
	
	    if (l > strength)
	    {
	        strength = l - 1;
	    }
	    else if (strength > 0)
	    {
	        --strength;
	    }
	    else
	    {
	        strength = 0;
	    }
	
	    if (k > strength - 1)
	    {
	        strength = k;
	    }
	
	    if (i != strength)
	    {
	        state = state.withProperty(POWER, Integer.valueOf(strength));
	
	        if (worldIn.getBlockState(pos1) == iblockstate)
	        {
	            worldIn.setBlockState(pos1, state, 2);
	        }

	        this.blocksNeedingUpdate.add(pos1);
	
	        for (EnumFacing enumfacing1 : EnumFacing.values())
	        {
	            this.blocksNeedingUpdate.add(pos1.offset(enumfacing1));
	        }
	    }
	
	    return state;
	}
    
//    @Override
//    protected int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength)
//    {
//    	if (worldIn.getBlockState(pos).getBlock() == Blocks.REDSTONE_WIRE) {
//    		return 0;
//    	}
//    	
//        if (worldIn.getBlockState(pos).getBlock() != this)
//        {
//            return strength;
//        }
//        else
//        {
//            int i = ((Integer)worldIn.getBlockState(pos).getValue(POWER)).intValue();
//            return i > strength ? i : strength;
//        }
//    }
    
    protected static boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos)
    {
        return BlockScabystWire.canConnectTo(worldIn.getBlockState(pos), null, worldIn, pos);
    }
    
    //TODO Change if making new class
    protected static boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side, IBlockAccess world, BlockPos pos)
    {
        Block block = blockState.getBlock();

        if(block == Blocks.REDSTONE_WIRE) {
        	return false;
        }
        else if (block == ModBlocks.SCABYST_WIRE)
        {
            return true;
        }
        else if (
        		Blocks.UNPOWERED_REPEATER.isSameDiode(blockState) ||
        		ModBlocks.UNPOWERED_SCABYST_REPEATER.isSameDiode(blockState)
    		)
        {
            EnumFacing enumfacing = (EnumFacing)blockState.getValue(BlockRedstoneRepeater.FACING);
            return enumfacing == side || enumfacing.getOpposite() == side;
        }
        else if (block == Blocks.OBSERVER || block == ModBlocks.SCABYST_OBSERVER)
        {
            return side == blockState.getValue(BlockObserver.FACING);
        }
        else
        {
            return blockState.getBlock().canConnectRedstone(blockState, world, pos, side);
        }
    }
    
    //TODO Change if making new class
  	@SideOnly(Side.CLIENT)
  	@Override
  	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
  		int power = stateIn.getValue(POWER);
  		
  		if(power != 0) {
  			double posX = (double) pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
  			double posY = (double) ((float) pos.getY() + 0.0625F);
  			double posZ = (double) pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
  	
  			double red = ScabystColor.shades[power].getR() / 255.0F;
  			double green = ScabystColor.shades[power].getG() / 255.0F;
  			double blue = ScabystColor.shades[power].getB() / 255.0F;
  			
  			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, posX, posY, posZ, red, green, blue);
  		}
  	}
    
    //TODO Change if making new class
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		 return new ItemStack(ModItems.SCABYST_DUST);
	}
    
	//TODO Change if making new class
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		 return new ItemStack(ModItems.SCABYST_DUST);
	}

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    	if(side == null) {
        	return super.canConnectRedstone(state, world, pos, side);
    	}
    	return world.getBlockState(pos.offset(side.getOpposite())).getBlock() != Blocks.REDSTONE_WIRE && super.canConnectRedstone(state, world, pos, side);
    }
}
