package com.joshiegemfinder.betweenlandsredstone.util;

import java.util.List;

import com.joshiegemfinder.betweenlandsredstone.Main;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.tile.TileEntityDugSoil;

public class CropHelper {

	public static final void harvestAndUpdateSoil(World world, BlockPos pos, int compost) {
		IBlockState stateDown = world.getBlockState(pos.down());
		if(stateDown.getBlock() instanceof BlockGenericDugSoil) {
			TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, pos.down());
			if(te != null && te.isComposted()) {
				te.setCompost(Math.max(te.getCompost() - compost, 0));
				if(((BlockGenericDugSoil)stateDown.getBlock()).isPurified(world, pos.down(), stateDown)) {
					te.setPurifiedHarvests(te.getPurifiedHarvests() + 1);
				}
			}
		}
	}
	
	public static final void harvestCrop(BlockGenericCrop crop, World worldIn, BlockPos pos, IBlockState state, int fortune, boolean replant, int replantAge) {
		float chance = 1;
        if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
        {
            List<ItemStack> drops = crop.getDrops(worldIn, pos, state, fortune); // use the old method until it gets removed, for backward compatibility
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(drops, worldIn, pos, state, fortune, chance, false, Main.getFakePlayer((WorldServer) worldIn));

            if(replant) {
                boolean seedCreated = false;
                ItemStack seedDrop = crop.getSeedDrop(worldIn, pos, worldIn.rand);
                for (ItemStack drop : drops)
                {
                	if(!seedCreated && drop.isItemEqual(seedDrop)) {
                		seedCreated = true;
                		drop.shrink(1);
                	}
                    if (worldIn.rand.nextFloat() <= chance && !drop.isEmpty())
                    {
                    	BlockGenericCrop.spawnAsEntity(worldIn, pos, drop);
                    }
                }
                if(seedCreated) {
                	worldIn.setBlockState(pos, state.withProperty(BlockGenericCrop.AGE, Math.min(replantAge, 15)));
                }
            } else {
                for (ItemStack drop : drops)
                {
                    if (worldIn.rand.nextFloat() <= chance)
                    {
                    	BlockGenericCrop.spawnAsEntity(worldIn, pos, drop);
                    }
                }
            }
        }
	}
	
	public static final void harvestCrop(BlockGenericCrop crop, World worldIn, BlockPos pos, IBlockState state, int fortune, boolean replant) {
		harvestCrop(crop, worldIn, pos, state, fortune, replant, 0);
	}
	
	public static final void harvestCrop(BlockGenericCrop crop, World worldIn, BlockPos pos, IBlockState state, int fortune, int replantAge) {
		harvestCrop(crop, worldIn, pos, state, fortune, true, replantAge);
	}
	
	public static final void harvestCrop(BlockGenericCrop crop, World worldIn, BlockPos pos, IBlockState state, int fortune) {
		harvestCrop(crop, worldIn, pos, state, fortune, false, 0);
	}
	
	public static class PlantTonicHelper {

		public static final NBTTagCompound getNBT(ItemStack stack) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) {
				compound = new NBTTagCompound();
				compound.setInteger("usages", 0);
				stack.setTagCompound(compound);
			}
			return compound;
		}

		public static final void setUsages(ItemStack stack, int usage) {
			getNBT(stack).setInteger("usages", Math.max(usage, 0));
		}

		public static final int getUsages(ItemStack stack) {
			return getNBT(stack).getInteger("usages");
		}

		@SideOnly(Side.CLIENT)
		public static void doPlantTonic(World worldIn, BlockPos pos, boolean playSound) {

			BlockPos tonicPos = pos;
			IBlockState state = worldIn.getBlockState(tonicPos);

			if(state.getBlock() instanceof IPlantable || state.getBlock() instanceof IFarmablePlant) {
				while(worldIn.getBlockState(tonicPos).getBlock() instanceof IPlantable || worldIn.getBlockState(tonicPos).getBlock() instanceof IFarmablePlant) {
					tonicPos = tonicPos.down();
				}
			}

			state = worldIn.getBlockState(tonicPos);
			
			if(state.getBlock() instanceof BlockGenericDugSoil && BlockGenericDugSoil.getTile(worldIn, tonicPos) != null) {
				boolean cured = false;

				for(int xo = -2; xo <= 2; xo++) {
					for(int yo = -2; yo <= 2; yo++) {
						for(int zo = -2; zo <= 2; zo++) {
							BlockPos offsetPos = tonicPos.add(xo, yo, zo);
							TileEntityDugSoil te = BlockGenericDugSoil.getTile(worldIn, offsetPos);
							if(te != null && te.getDecay() > 0) {
								cured = true;
								ItemDye.spawnBonemealParticles(worldIn, offsetPos.up(), 6);
							}
						}
					}
				}

				if(cured && playSound) {
					worldIn.playSound(null, tonicPos.getX() + 0.5F, tonicPos.getY() + 0.5F, tonicPos.getZ() + 0.5F, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1, 1);
				}
			}
		}
		

		@SideOnly(Side.CLIENT)
		public static void doPlantTonic(World worldIn, BlockPos pos) {
			doPlantTonic(worldIn, pos, true);
		}
	}
}
