package com.joshiegemfinder.betweenlandsredstone.blocks.dispenser;

import com.joshiegemfinder.betweenlandsredstone.BLRedstoneConfig;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityScabystDropper extends TileEntityDropper implements ISyncIsEmpty {

	@Override
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.scabyst_dropper";
    }
	
    public static void registerFixesDropperScabyst(DataFixer fixer)
    {
        fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileEntityScabystDropper.class, new String[] {"Items"}));
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
    	return oldState.getBlock() != newSate.getBlock();
    }

    
    
    //Sync empty status
	protected boolean isEmptyClient = true;
	protected boolean prevEmpty = true;
    
    @Override
    public int addItemStack(ItemStack stack) {
    	int output = super.addItemStack(stack);
    	this.syncEmpty();
    	return output;
    }
    
    @Override
    public ItemStack decrStackSize(int index, int count) {
    	ItemStack stack = super.decrStackSize(index, count);
    	this.syncEmpty();
    	return stack;
    }
    
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
    	super.setInventorySlotContents(index, stack);
    	this.syncEmpty();
    }
    
    @Override
    public ItemStack removeStackFromSlot(int index) {
    	ItemStack stack = super.removeStackFromSlot(index);
    	this.syncEmpty();
    	return stack;
    }
    
    protected void syncEmpty() {
    	//config: have the server simply lie to the client
    	//like this so a client can't change the config on a server where it's not intended
    	final boolean isEmpty = this.isEmpty() || !BLRedstoneConfig.EXTRA_FEATURES.dispenserHiding;
    	if(isEmpty == this.prevEmpty) {
    		return;
    	}
		this.prevEmpty = isEmpty;
		this.world.addBlockEvent(this.getPos(), this.getBlockType(), 201, isEmpty ? 0 : 1);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
    	return new SPacketUpdateTileEntity(pos, this.getBlockMetadata(), this.getUpdateTag());
    }
    
//  @Override
//  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
//  	boolean isEmptyClient = this.isEmptyClient;
//  	super.onDataPacket(net, pkt);
//  	this.handleUpdateTag(pkt.getNbtCompound());
//  	if(this.isEmptyClient != isEmptyClient) {
//  		this.world.addBlockEvent(pos, this.getBlockType(), 201, this.isEmptyClient ? 0 : 1);
//  	}
//  }
    
    @Override
    public NBTTagCompound getUpdateTag() {
    	NBTTagCompound tag = super.getUpdateTag();
    	tag.setBoolean("IsEmpty", this.isEmpty());
    	return tag;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void handleUpdateTag(NBTTagCompound tag) {
    	this.isEmptyClient = tag.getBoolean("IsEmpty");
    	super.handleUpdateTag(tag);
    }
	
	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		this.prevEmpty = this.isEmpty();
	}

	@Override
	public boolean isEmptyClient() {
		if(this.world.isRemote) {
			return this.isEmptyClient;
		} else {
			return this.isEmpty();
		}
	}

	@Override
	public void setEmptyClient(boolean isEmpty) { //only on client
		this.isEmptyClient = isEmpty;
	}
}
