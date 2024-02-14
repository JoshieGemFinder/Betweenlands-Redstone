package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Random;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.ModSounds;
import com.joshiegemfinder.betweenlandsredstone.network.PlayAttenuatedSoundMessage;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thebetweenlands.common.entity.mobs.EntityStalker;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockScabystBulb extends Block {

	public static enum DecayState implements IStringSerializable {
		NORMAL(0, "normal"),
		SLUDGY(1, "sludgy"),
		DETERIORATED(2, "deteriorated"),
		DECAYED(3, "decayed");

		private final int index;
		private final String name;
		
		private DecayState(int index, String name) {
			this.index = index;
			this.name = name;
		}

	    public int getIndex()
	    {
	        return this.index;
	    }
		
		@Override
		public String getName() {
			return this.name;
		}

	    public static DecayState byIndex(int index)
	    {
	    	switch(index) {
	    	case 0:
	    		return NORMAL;
	    	case 1:
	    		return SLUDGY;
	    	case 2:
	    		return DETERIORATED;
	    	case 3:
	    		return DECAYED;
	    	default:
	    		return null;
	    	}
	    }
	}

	public static final PropertyBool LIT = PropertyBool.create("lit");
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	public static final IProperty<DecayState> DECAY_STATE = PropertyEnum.<BlockScabystBulb.DecayState>create("decay_state", DecayState.class);
	
	public BlockScabystBulb(Material materialIn) {
		super(materialIn);
		
//		this.setSoundType(ModSounds.BULB);
		
		this.setDefaultState(this.getDefaultState().withProperty(LIT, false).withProperty(POWERED, false).withProperty(DECAY_STATE, DecayState.NORMAL));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i |= state.getValue(LIT) ? 0b00 : 0b01;
		i |= state.getValue(POWERED) ? 0b00 : 0b10;
		i |= state.getValue(DECAY_STATE).getIndex() << 2;
		return i;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = this.getDefaultState();
		state = state.withProperty(LIT, (meta & 1) != 0);
		state = state.withProperty(POWERED, (meta & 0b10) != 0);
		state = state.withProperty(DECAY_STATE, DecayState.byIndex(meta >> 2));
		return state;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		if(world.isBlockPowered(pos)) {
			state = state.withProperty(POWERED, true).withProperty(LIT, true);
		}
		return state;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{LIT, POWERED, DECAY_STATE});
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if(!state.getValue(LIT)) {
			return 0;
		}
		DecayState decayState = state.getValue(DECAY_STATE);
		switch(decayState) {
		case NORMAL:
			return 15;
		case SLUDGY:
			return 12;
		case DETERIORATED:
			return 8;
		case DECAYED:
			return 4;
		}
		return super.getLightValue(state, world, pos);
	}
	
	public void playToggleSound(World worldIn, BlockPos pos, SoundEvent sound) {
		if(!worldIn.isRemote)
			BetweenlandsRedstone.NETWORK_CHANNEL.sendToAllAround(new PlayAttenuatedSoundMessage(pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F, 6), new TargetPoint(worldIn.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 12));
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		
		if(state.getValue(LIT)) {
			playToggleSound(worldIn, pos, ModSounds.BLOCK_BULB_TURN_ON);
		}
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		boolean isPowered = worldIn.isBlockPowered(pos);
		boolean wasPowered = state.getValue(POWERED);
		
		if(isPowered != wasPowered) {
			IBlockState newState = state.withProperty(POWERED, isPowered);
			if(isPowered) {
				boolean isLit = !state.getValue(LIT);
				newState = newState.withProperty(LIT, isLit);
				
				if(!worldIn.isRemote) {
					playToggleSound(worldIn, pos, isLit ? ModSounds.BLOCK_BULB_TURN_ON : ModSounds.BLOCK_BULB_TURN_OFF);
				}
			}
			
			worldIn.setBlockState(pos, newState);
		}
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return blockState.getValue(LIT) ? 15 : 0;
	}
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
    		EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	if(playerIn != null & hand != null) {
    		ItemStack stack = playerIn.getHeldItem(hand);
    		DecayState decayState = state.getValue(DECAY_STATE);
    		if(stack.getItem() == ItemRegistry.SLUDGE_BALL && decayState != DecayState.DECAYED) {
    			if(!playerIn.isCreative()) stack.shrink(1);
    			worldIn.setBlockState(pos, state.withProperty(DECAY_STATE, DecayState.byIndex(decayState.index + 1)));
    			worldIn.playSound(null, pos, ModSounds.BLOCK_BULB_SMEAR, SoundCategory.BLOCKS, 1.0F, 0.2F);
    			return true;
    		}

    		if(stack.getItem().getToolClasses(stack).contains("axe") && decayState != DecayState.NORMAL) {
    			stack.damageItem(1, playerIn);
    			worldIn.setBlockState(pos, state.withProperty(DECAY_STATE, DecayState.byIndex(decayState.index - 1)));
    			worldIn.playSound(null, pos, ModSounds.BLOCK_BULB_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    			return true;
    		}
    	}
    	return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }


    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ModItems.SCABYST_BULB;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModItems.SCABYST_BULB, 1, state.getValue(DECAY_STATE).getIndex());
    }
    
    //stop stalkers munching on blocks
    @Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		if(entity instanceof EntityStalker) {
			return false;
		}
		return super.canEntityDestroy(state, world, pos, entity);
	}
    
    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
        items.add(new ItemStack(this, 1, 2));
        items.add(new ItemStack(this, 1, 3));
    }
}
