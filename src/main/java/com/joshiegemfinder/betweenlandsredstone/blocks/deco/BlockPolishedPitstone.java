package com.joshiegemfinder.betweenlandsredstone.blocks.deco;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockPolishedPitstone extends Block {

	public BlockPolishedPitstone(String name) {
		super(Material.ROCK);
		
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setSoundType(SoundType.STONE);
		setHardness(1.5F);
		setResistance(10.0F);

		setUnlocalizedName(name);
		setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		 return new ItemStack(this);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.POLISHED_PITSONE;
	}
}
