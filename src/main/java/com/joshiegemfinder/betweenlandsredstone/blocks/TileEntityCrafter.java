package com.joshiegemfinder.betweenlandsredstone.blocks;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import com.joshiegemfinder.betweenlandsredstone.compat.GameStagesCompat;
import com.joshiegemfinder.betweenlandsredstone.gui.ContainerCrafter;
import com.joshiegemfinder.betweenlandsredstone.gui.InventoryCrafter;
import com.joshiegemfinder.betweenlandsredstone.gui.InventoryCrafterResult;
import com.joshiegemfinder.betweenlandsredstone.util.InventoryUtils;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileEntityCrafter extends TileEntity implements ISidedInventory, net.minecraft.util.ITickable {
	public static final int DISPENSE_SPEED = 4;
	
	protected static class ItemHandlerCrafter extends SidedInvWrapper {

		TileEntityCrafter tile;
		
		public ItemHandlerCrafter(TileEntityCrafter inv, EnumFacing side) {
			super(inv, side);
			this.tile = inv;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if(stack.isEmpty())
				return ItemStack.EMPTY;
			
			int totalAvailableSpace = 0;
			PriorityQueue<Integer> maxHeap = !simulate ? new PriorityQueue<Integer>(9, Comparator.reverseOrder()) : null;
			
			for(int i = 0; i < 9; ++i) {
				if(tile.disabledSlots[i]) {
					if(!simulate) {
						maxHeap.add(-i);
					}
					continue;
				}
				ItemStack stackExisting = tile.slots.get(i);
				boolean canStack = stackExisting.isEmpty() || ItemHandlerHelper.canItemStacksStack(stackExisting, stack);
				if(canStack) {
					int space = this.getSlotLimit(i) - stackExisting.getCount();
					totalAvailableSpace += space;
					if(!simulate) {
						maxHeap.add(space * 10 - i);
					}
				}
			}
			
			if(simulate) {
				if(totalAvailableSpace >= stack.getCount()) {
					return ItemStack.EMPTY;
				} else {
					return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - totalAvailableSpace);
				}
			}
			
			// not allowed to modify the original stack
			stack = stack.copy();
			
			while(totalAvailableSpace > 0 && !stack.isEmpty()) {
				int maxAvailableSpaceSlot = maxHeap.poll();
				int secondAvailableSpaceSlot = maxHeap.peek();

				if(maxAvailableSpaceSlot <= 0)
					return stack;
				
				int maxSlot = (9 - ((9 + maxAvailableSpaceSlot) % 10)) % 9;
				int secondMaxSlot = (9 - ((9 + secondAvailableSpaceSlot) % 10)) % 9;

				int maxAvailableSpace = (maxAvailableSpaceSlot + maxSlot) / 10;
				int secondAvailableSpace = (secondAvailableSpaceSlot + secondMaxSlot) / 10;
				
				if(maxAvailableSpace <= 0) {
					maxAvailableSpace = 0;
					break;
				}
				
				if(secondAvailableSpace <= 0) {
					secondAvailableSpace = 0;
				}
				
				int amountToInsert = maxAvailableSpace - secondAvailableSpace;
//				BetweenlandsRedstone.logger.info("max: {} ({}, {}) second: {} ({}, {}) diff: {}", maxAvailableSpaceSlot, maxAvailableSpace, maxSlot, secondAvailableSpaceSlot, secondAvailableSpace, secondMaxSlot, amountToInsert);
				if(amountToInsert == 0) {
					amountToInsert = 1;
				}
//				System.out.println(amountToInsert);
//				throw new RuntimeException(String.valueOf(amountToInsert));
				
				ItemStack stackExisting = tile.slots.get(maxSlot);
				if(stack.getCount() <= amountToInsert) {
					amountToInsert = stack.getCount();
				}
//				BetweenlandsRedstone.logger.info("adjusted count: {}", amountToInsert);

				if(stackExisting.isEmpty()) {
					stackExisting = ItemHandlerHelper.copyStackWithSize(stack, amountToInsert);
					this.tile.slots.set(maxSlot, stackExisting);
				} else {
					stackExisting.grow(amountToInsert);
				}
				stack.shrink(amountToInsert);
				totalAvailableSpace -= amountToInsert;
				
				int newRemainingAmount = this.getSlotLimit(maxSlot) - stackExisting.getCount();
						
				maxHeap.add(newRemainingAmount * 10 - maxSlot);
				
//				int minSlotCount = Integer.MAX_VALUE;
//				int minSlot = -1;
//				for(int i = 0; i < 9; ++i) {
////					int space = availableSpace[i];
////					if(space < maxAvailableSpace) {
////						continue;
////					}
//
//					if(tile.isItemValidForSlot(i, stack)) {
//						ItemStack stackExisting = tile.slots.get(i);
//						if(stackExisting.getCount() < minSlotCount) {
//							minSlot = i;
//							minSlotCount = stackExisting.getCount();
//						}
////						if(stackExisting.isEmpty()) {
////							this.tile.slots.set(i, ItemHandlerHelper.copyStackWithSize(stack, 1));
////						} else {
////							stackExisting.grow(1);
////						}
////						stack.shrink(1);
////						--totalAvailableSpace;
//					}
//				}
//				if(minSlot == -1)
//					break;
//				else {
//					ItemStack stackExisting = tile.slots.get(minSlot);
//					if(stackExisting.isEmpty()) {
//						this.tile.slots.set(minSlot, ItemHandlerHelper.copyStackWithSize(stack, 1));
//					} else {
//						stackExisting.grow(1);
//					}
//					stack.shrink(1);
//					--totalAvailableSpace;
//				}
				
			}

			this.tile.onCraftMatrixChanged();
			this.tile.markDirty();

			if(stack.isEmpty())
				return ItemStack.EMPTY;

			return stack;
		}
	}
	
	private final int[] accessibleSlots = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};

    public NonNullList<ItemStack> slots = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
    public ItemStack resultStack = ItemStack.EMPTY;
    public boolean[] disabledSlots = new boolean[9];
    private boolean isCrafting = false;
    private int craftingTicksRemaining = 0;
    private boolean triggered = false;

    protected boolean isLoading = false;
    
    private InventoryCrafter craftMatrix;
    public InventoryCrafterResult outputInventory;

    private boolean hasRecipe = false;
    private IRecipe currentRecipe = null;

    // Gamestages compat
    public Set<String> crafterStages = new HashSet<String>();
    
	public TileEntityCrafter() {
		this.craftMatrix = new InventoryCrafter(this);
		this.outputInventory = new InventoryCrafterResult(this);
	}

	// ACTUAL STUFF
	
	public boolean isCrafting() {
		return isCrafting;
	}

	public int getComparatorPower() {
		int power = 0;
		for(int i = 0; i < 9; ++i) {
			if(disabledSlots[i] || !slots.get(i).isEmpty()) {
				++power;
			}
		}
		return power;
	}
	
	public void setSlotDisabled(int slot, boolean disabled) {
		if(this.disabledSlots[slot] != disabled) {
			this.disabledSlots[slot] = disabled;
			this.markDirty();
			this.world.updateComparatorOutputLevel(pos, this.blockType);
		}
	}
	
	protected IRecipe getRecipeBypass(IRecipe recipe) {
		if(Loader.isModLoaded("gamestages")) {
			return GameStagesCompat.getCrafterRecipeBypass(recipe, this);
		}
		
		return recipe;
	}
	
	public void onCraftMatrixChanged() {
		if(this.isLoading)
			return;
		
		if(this.world != null && this.pos != null) { // items have changed, send updated item list to clients
			IBlockState state = this.world.getBlockState(pos);
			this.world.notifyBlockUpdate(pos, state, state, 2);
		}
		
		IRecipe recipe = CraftingManager.findMatchingRecipe(this.craftMatrix, this.world);
		if(recipe != null) {
			ItemStack craftingStack = recipe.getCraftingResult(this.craftMatrix);
			if(craftingStack.isEmpty()) {
				IRecipe potentialRecipe = this.getRecipeBypass(recipe);
				if(potentialRecipe != null) {
					recipe = potentialRecipe;
					craftingStack = potentialRecipe.getCraftingResult(this.craftMatrix);
				}
			}
			if(!craftingStack.isEmpty()) {
				this.hasRecipe = true;
				this.currentRecipe = recipe;
				this.resultStack = craftingStack;
				
				this.markDirty();
				
				return;
			}
		}
		this.hasRecipe = false;
		this.currentRecipe = null;
		this.resultStack = ItemStack.EMPTY;
		
		this.markDirty();
	}
	
	public void tryToCraft() {
		if(!this.hasRecipe) {
//			BetweenlandsRedstone.NETWORK_CHANNEL.sendToAllAround(new PlayAttenuatedSoundMessage(pos, ModSounds.BLOCK_CRAFTER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F, 3), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 6));
			this.world.addBlockEvent(this.pos, this.getBlockType(), 51, 0);
		} else if(!this.isCrafting || this.craftingTicksRemaining <= 2) {
//			BetweenlandsRedstone.NETWORK_CHANNEL.sendToAllAround(new PlayAttenuatedSoundMessage(pos, ModSounds.BLOCK_CRAFTER_SUCCEED, SoundCategory.BLOCKS, 1.0F, 1.0F, 5), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 10));
			this.world.addBlockEvent(this.pos, this.getBlockType(), 51, 1);
			
			EnumFacing dispenseFacing = world.getBlockState(pos).getValue(BlockDirectional.FACING);
			
			boolean playEjectParticles = false;
			
			if(
				InventoryUtils.ejectItem(this.world, this.pos, dispenseFacing, this.resultStack, DISPENSE_SPEED)
			)
				playEjectParticles = true;

			NonNullList<ItemStack> updatedMatrix = this.currentRecipe.getRemainingItems(this.craftMatrix);
			for(int i = 0; i < 9; ++i) { // treats it like it was only done once, will return ItemStack.EMPTY if one of the items was used up
				ItemStack currentStackInSlot = slots.get(i);
				ItemStack resultStackInSlot = updatedMatrix.get(i);
				if(!currentStackInSlot.isEmpty() && ItemHandlerHelper.canItemStacksStack(currentStackInSlot, resultStackInSlot)) { // if possible, stack them
					currentStackInSlot.grow(resultStackInSlot.getCount() - 1);
				} else {
					currentStackInSlot.shrink(1);
					if(currentStackInSlot.isEmpty()) {
						slots.set(i, resultStackInSlot);
					} else if(!resultStackInSlot.isEmpty()) {
						boolean ejected = 
								InventoryUtils.ejectItem(this.world, this.pos, dispenseFacing, resultStackInSlot, DISPENSE_SPEED);
						if(ejected)
							playEjectParticles = true;
					}
				}
			}

			IBlockState oldState = world.getBlockState(pos);
			IBlockState newState = oldState.withProperty(BlockCrafter.CRAFTING, true);

			this.isCrafting = true;
			this.craftingTicksRemaining = 6;
			this.world.setBlockState(pos, newState, 3);
			
			if(playEjectParticles && !world.isRemote) {
				this.world.addBlockEvent(pos, this.getBlockType(), 92, 0);
//                this.world.getMinecraftServer().getPlayerList().sendToAllNearExcept((EntityPlayer)null, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 64.0D, this.world.provider.getDimension(), new SPacketBlockAction(pos, this.getBlockType(), 92, 0));
			}
			
			this.onCraftMatrixChanged();
			this.markDirty();
		}
	}

	@Override
	public void update() {
//		this.triggered = world.isBlockPowered(pos);
		if(!world.isRemote && this.craftingTicksRemaining > 0) {
			this.craftingTicksRemaining--;
			if(this.craftingTicksRemaining == 0) {
				this.isCrafting = false;
				this.world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCrafter.CRAFTING, false));
			}
		}
		
//		if(world.isRemote && this.isCrafting != world.getBlockState(pos).getValue(BlockCrafter.CRAFTING)) {
//			this.world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCrafter.CRAFTING, this.isCrafting));
//		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		ItemStackHelper.saveAllItems(compound, slots);
		
		byte[] disabledSlotsBytes = new byte[9];
		for(int i = 0; i < 9; ++i) {
			disabledSlotsBytes[i] = (byte) (disabledSlots[i] ? 1 : 0);
		}
		compound.setByteArray("disabledSlots", disabledSlotsBytes);
		
		compound.setBoolean("isCrafting", this.isCrafting);
		
		compound.setByte("craftingTicksRemaining", (byte) this.craftingTicksRemaining);
		compound.setBoolean("triggered", this.triggered);
		
		if(crafterStages.size() > 0) {
			NBTTagList list = new NBTTagList();
			for(String str : crafterStages) {
				list.appendTag(new NBTTagString(str));
			}
			compound.setTag("CrafterStages", list);
		}
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.isLoading = true;
		
		super.readFromNBT(compound);
		
		slots.clear(); // necessary, won't update slots being emptied otherwise
		ItemStackHelper.loadAllItems(compound, slots);

		byte[] disabledSlotsBytes = compound.getByteArray("disabledSlots");
		for(int i = 0; i < 9; ++i) {
			disabledSlots[i] = disabledSlotsBytes[i] != 0;
		}
		
		this.isCrafting = compound.getBoolean("isCrafting");

		this.craftingTicksRemaining = compound.getByte("craftingTicksRemaining");
		this.triggered = compound.getBoolean("triggered");

		this.isLoading = false;
		
		this.onCraftMatrixChanged();
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		
		ItemStackHelper.saveAllItems(tag, slots); // for the renderer

		byte[] disabledSlotsBytes = new byte[9];
		for(int i = 0; i < 9; ++i) {
			disabledSlotsBytes[i] = (byte) (disabledSlots[i] ? 1 : 0);
		}
		tag.setByteArray("disabledSlots", disabledSlotsBytes);

		tag.setBoolean("triggered", triggered);
		tag.setBoolean("isCrafting", isCrafting);
		
		if(tag.hasKey("CrafterStages", NBT.TAG_LIST)) {
			crafterStages.clear();
			NBTTagList list = tag.getTagList("CrafterStages", NBT.TAG_STRING);
			for(NBTBase stage : list) {
				if(stage != null && stage instanceof NBTTagString) {
					crafterStages.add(((NBTTagString)stage).getString());
				}
			}
		}
		
		return tag;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
		
		world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCrafter.CRAFTING, tag.getBoolean("isCrafting")));
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		this.handleUpdateTag(pkt.getNbtCompound());
	}
	
	
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) new ItemHandlerCrafter(this, facing);
		}
		return super.getCapability(capability, facing);
	}
	
	
	// GENERIC STUFF
	
	@Override
	public String getName() {
		return "tile.crafter.name";
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(this.getName());
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock() && newState.getBlock() != this.blockType;
	}
	
	// INVENTORY IMPLEMENTATION

	@Override
	public void clear() {
		slots.clear();
	}

	@Override
	public void closeInventory(EntityPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = ItemStackHelper.getAndSplit(this.slots, index, count);
		this.onCraftMatrixChanged();
		return stack;
	}

	@Override
	public int getField(int index) {
		if(index < 9)
			return this.disabledSlots[index] ? 1 : 0;
		else if(index == 9)
			return this.triggered ? 1 : 0;
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 10;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return this.slots.size();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return this.slots.get(index);
	}

	@Override
	public boolean isEmpty() {
        for (ItemStack itemstack : this.slots) {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(disabledSlots[slot])
			return false;
		ItemStack existingStack = slots.get(slot);
		if(!existingStack.isEmpty() && !ItemHandlerHelper.canItemStacksStack(existingStack, stack))
			return false;
		return existingStack.getCount() < this.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if(Loader.isModLoaded("gamestages")) {
			GameStagesCompat.addCrafterStages(this, player);
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = ItemStackHelper.getAndRemove(this.slots, index);
		this.onCraftMatrixChanged();
		return stack;
	}

	@Override
	public void setField(int index, int value) {
		assert(index > 9 || (value == 0 || value == 1));
		if(index < 9) {
			this.disabledSlots[index] = value != 0;
		} else if(index == 9) {
			this.triggered = value != 0;
		} else {
			this.craftingTicksRemaining = value;
		}
		this.markDirty();
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
        this.slots.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

		this.onCraftMatrixChanged();
	}

	
	@Override
	public boolean canExtractItem(int index, ItemStack arg1, EnumFacing arg2) {
		return true;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack insertingStack, EnumFacing arg2) {
		if(disabledSlots[index] || !this.isItemValidForSlot(index, insertingStack))
			return false;
		
		final ItemStack currentStack = slots.get(index);
		final int count = currentStack.getCount();
		
		for(int i = 0; i < 9; ++i) {
			ItemStack stack = slots.get(i);
			if(!disabledSlots[i] && stack.getCount() < count && (stack.isEmpty() || ItemHandlerHelper.canItemStacksStack(insertingStack, stack)))
				return false;
		}
		return currentStack.isEmpty() || (ItemHandlerHelper.canItemStacksStack(currentStack, insertingStack));
	}

	@Override
	public int[] getSlotsForFace(EnumFacing arg0) {
		return accessibleSlots;
	}

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerCrafter(playerInventory, this);
    }

	public boolean isTriggered() {
		return this.world.isRemote ? this.world.getBlockState(this.pos).getValue(BlockCrafter.TRIGGERED) : this.triggered;
	}

	public void setTriggered(boolean isPowered) {
		this.triggered = isPowered;
		this.markDirty();
	}

	@SideOnly(Side.CLIENT)
	public EnumFacing getFacing() {
		return this.getWorld().getBlockState(this.getPos()).getValue(BlockDirectional.FACING);
	}

}
