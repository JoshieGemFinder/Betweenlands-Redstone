package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.util.IScabystBlock;
import com.joshiegemfinder.betweenlandsredstone.util.ScabystWorldWrapper;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockScabystLight extends Block implements IScabystBlock {

    private final boolean isOn;
    
	public BlockScabystLight(String name, boolean isOn) {
		super(Material.REDSTONE_LIGHT);
        this.isOn = isOn;

        if (isOn)
        {
            this.setLightLevel(1.0F);
        }

		this.setHardness(0.3F);
		this.setSoundType(SoundType.GLASS);
		this.setUnlocalizedName(name);
		this.setRegistryName(new ResourceLocation(Main.MODID, name));
		ModBlocks.BLOCKS.add(this);
	}
	

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
        	boolean powered = ScabystWorldWrapper.isBlockScabystPowered(worldIn, pos);
            if (this.isOn && !powered)
            {
                worldIn.setBlockState(pos, ModBlocks.SCABYST_LAMP.getDefaultState(), 2);
            }
            else if (!this.isOn && powered)
            {
                worldIn.setBlockState(pos, ModBlocks.LIT_SCABYST_LAMP.getDefaultState(), 2);
            }
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
        	boolean powered = ScabystWorldWrapper.isBlockScabystPowered(worldIn, pos);
            if (this.isOn && !powered)
            {
                worldIn.scheduleUpdate(pos, this, 4);
            }
            else if (!this.isOn && powered)
            {
                worldIn.setBlockState(pos, ModBlocks.LIT_SCABYST_LAMP.getDefaultState(), 2);
            }
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (this.isOn && !ScabystWorldWrapper.isBlockScabystPowered(worldIn, pos))
            {
                worldIn.setBlockState(pos, ModBlocks.SCABYST_LAMP.getDefaultState(), 2);
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ModItems.SCABYST_LAMP;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModItems.SCABYST_LAMP);
    }

    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        return new ItemStack(ModItems.SCABYST_LAMP);
    }
	
}
