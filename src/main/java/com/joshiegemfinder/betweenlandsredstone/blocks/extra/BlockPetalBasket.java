package com.joshiegemfinder.betweenlandsredstone.blocks.extra;

import java.lang.ref.WeakReference;
import java.util.List;

import com.joshiegemfinder.betweenlandsredstone.util.ISidedInventoryProvider;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockPetalBasket extends Block implements ISidedInventoryProvider {

	
	//eh, suboptimal, but vanilla does it in later versions and it's also probably not a big deal
	static class BasketInventory implements ISidedInventory {

		// hey, i'm as upset about this as you are, but this is all vanilla's fault
		// the hopper calls .grow on this when inserting items
		// originally i was going to extend itemstack, but this is probably good enough
		// hopefully no mod is poorly coded enough that this'll cause items to vanish
		// also, most mods won't even recognise ISidedInventoryProvider, so we've got that
		private WeakReference<ItemStack> stack = null;
		
		public final World world;
		public final BlockPos pos;
		
		public BasketInventory(World world, BlockPos pos) {
			this.world = world;
			this.pos = pos;
		}
		
		public IBlockState getState() {
			return this.world.getBlockState(pos);
		}
		
		@Override
		public void clear() {
			world.setBlockState(pos, getState().withProperty(PETAL_COUNT, 0));
			stack = null;
		}

		@Override
		public void closeInventory(EntityPlayer player) {}

		@Override
		public void openInventory(EntityPlayer player) {}

		@Override
		public ItemStack decrStackSize(int index, int amount) {
			IBlockState state = this.getState();
			int initialCount = state.getValue(PETAL_COUNT);
			if(initialCount == 0) {
				return ItemStack.EMPTY;
			}
			if(initialCount <= amount) {
				world.setBlockState(pos, state.withProperty(PETAL_COUNT, 0));
				stack = null;
				return new ItemStack(ItemRegistry.WEEPING_BLUE_PETAL, initialCount);
			}
			world.setBlockState(pos, state.withProperty(PETAL_COUNT, Math.min(initialCount - amount, this.getInventoryStackLimit())));
			if(stack != null && stack.get() != null) {
				stack.get().setCount(initialCount - amount);
			}
			return new ItemStack(ItemRegistry.WEEPING_BLUE_PETAL, amount);
		}

		@Override
		public int getField(int arg0) {
			return 0;
		}

		@Override
		public int getFieldCount() {
			return 0;
		}

		@Override
		public int getInventoryStackLimit() {
			return 15;
		}

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override	// sooo the hopper calls .grow on this when inserting items
		public ItemStack getStackInSlot(int index) {
			if(stack == null || stack.get() == null) {
				stack = new WeakReference<ItemStack>(new ItemStack(ItemRegistry.WEEPING_BLUE_PETAL, this.getState().getValue(PETAL_COUNT)));
			}
			
			return stack.get();
		}

		@Override
		public boolean isEmpty() {
			return this.getState().getValue(PETAL_COUNT) == 0;
		}

		@Override
		public boolean isItemValidForSlot(int arg0, ItemStack arg1) {
			return arg0 == 0 && arg1.getItem() == ItemRegistry.WEEPING_BLUE_PETAL;
		}

		@Override
		public boolean isUsableByPlayer(EntityPlayer player) {
			return false;
		}

		@Override
		public void markDirty() {
			IBlockState state = this.getState();
			if(stack != null && stack.get() != null) {
				IBlockState newState = state.withProperty(PETAL_COUNT, MathHelper.clamp(stack.get().getCount(), 0, 15));
				this.world.setBlockState(pos, newState, 3);
			} else {
				this.world.notifyBlockUpdate(pos, state, state, 3);
			}
		}

		@Override
		public ItemStack removeStackFromSlot(int arg0) {
			IBlockState state = this.getState();
			this.world.setBlockState(pos, state.withProperty(PETAL_COUNT, 0));
			stack = null;
			return new ItemStack(ItemRegistry.WEEPING_BLUE_PETAL, state.getValue(PETAL_COUNT));
		}

		@Override
		public void setField(int arg0, int arg1) {}

		@Override
		public void setInventorySlotContents(int arg0, ItemStack arg1) {
//			assert(arg1.getItem() == ItemRegistry.WEEPING_BLUE_PETAL, new IllegalArgumentException("Item must be a petal!"));
			assert(arg1.getItem() == ItemRegistry.WEEPING_BLUE_PETAL);
			assert(0 <= arg0 && arg0 <= 15);
			stack = new WeakReference<ItemStack>(arg1);
			this.world.setBlockState(pos, this.getState().withProperty(PETAL_COUNT, arg1.getCount()));
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TextComponentTranslation(getState().getBlock().getUnlocalizedName());
		}

		@Override
		public String getName() {
			return getState().getBlock().getLocalizedName();
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public boolean canExtractItem(int arg0, ItemStack arg1, EnumFacing arg2) {
			return arg0 == 0 && getState().getValue(PETAL_COUNT) > 0;
		}

		@Override
		public boolean canInsertItem(int arg0, ItemStack arg1, EnumFacing arg2) {
			return this.isItemValidForSlot(arg0, arg1) && getState().getValue(PETAL_COUNT) < this.getInventoryStackLimit();
		}

		private static final int[] SLOTS = new int[] {0};
		
		@Override
		public int[] getSlotsForFace(EnumFacing arg0) {
			return SLOTS;
		}
		
	}
	
	public static final PropertyInteger PETAL_COUNT = PropertyInteger.create("petal_count", 0, 15);

	public BlockPetalBasket(Material materialIn) {
		super(materialIn);
		
		this.setSoundType(SoundType.CLOTH);
		this.setHardness(1.0f);
		
		this.setDefaultState(this.getDefaultState().withProperty(PETAL_COUNT, 0));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {PETAL_COUNT});
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(PETAL_COUNT, meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(PETAL_COUNT).intValue();
	}
	
//	@Override
//	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
//		InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistry.WEEPING_BLUE_PETAL, state.getValue(PETAL_COUNT), 0));
//		super.breakBlock(worldIn, pos, state);
//	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		drops.add(new ItemStack(ItemRegistry.WEEPING_BLUE_PETAL, state.getValue(PETAL_COUNT), 0));
	}
	
	public void dropPetalItem(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		if(playerIn != null && playerIn.isCreative()) {
			//need some sort of sound to provide feedback to the player
			worldIn.playSound(null, playerIn.posX, playerIn.posY + 0.5, playerIn.posZ,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.2F, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			return;
		}
		ItemHandlerHelper.giveItemToPlayer(playerIn, new ItemStack(ItemRegistry.WEEPING_BLUE_PETAL));
	}
	
	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		if(!worldIn.isRemote) {
			IBlockState state = worldIn.getBlockState(pos);
			int petalCount = state.getValue(PETAL_COUNT);
			if(petalCount > 0) {
				this.dropPetalItem(worldIn, pos, playerIn);
				worldIn.setBlockState(pos, state.withProperty(PETAL_COUNT, petalCount - 1));
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		int petalCount = state.getValue(PETAL_COUNT);
		
		if(playerIn.isSneaking()) {
			if(petalCount > 0) {
				if(!worldIn.isRemote) {
					this.dropPetalItem(worldIn, pos, playerIn);
					worldIn.setBlockState(pos, state.withProperty(PETAL_COUNT, petalCount - 1));
				}
				return true;
			}
			return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		}
		
		ItemStack held = playerIn.getHeldItem(hand);
		
		if(petalCount < 15 && !held.isEmpty() && held.getItem() == ItemRegistry.WEEPING_BLUE_PETAL) {
			worldIn.setBlockState(pos, state.withProperty(PETAL_COUNT, petalCount + 1));
			worldIn.playSound(null, playerIn.posX, playerIn.posY + 0.5, playerIn.posZ,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.2F, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			if(playerIn == null || !playerIn.isCreative())
				held.shrink(1);
			return true;
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
		return state.getValue(PETAL_COUNT).intValue();
	}

	@Override
	public ISidedInventory getContainer(World worldIn, BlockPos pos, IBlockState state) {
		return new BasketInventory(worldIn, pos);
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(I18n.translateToLocal("tooltip.betweenlandsredstone.petal_basket.0"));
		tooltip.add(I18n.translateToLocal("tooltip.betweenlandsredstone.petal_basket.1"));
		super.addInformation(stack, player, tooltip, advanced);
	}
}
