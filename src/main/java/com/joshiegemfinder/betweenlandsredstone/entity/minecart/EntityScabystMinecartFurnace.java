package com.joshiegemfinder.betweenlandsredstone.entity.minecart;

import com.joshiegemfinder.betweenlandsredstone.Main;
import com.joshiegemfinder.betweenlandsredstone.network.MinecartFacingMessage;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.block.container.BlockBLDualFurnace;
import thebetweenlands.common.block.container.BlockBLFurnace;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

@SuppressWarnings("deprecation")
public class EntityScabystMinecartFurnace extends EntityScabystMinecart implements IEntityAdditionalSpawnData {

    private static final DataParameter<Boolean> POWERED = EntityDataManager.<Boolean>createKey(EntityScabystMinecartFurnace.class, DataSerializers.BOOLEAN);
    private boolean dual = false;
    private EnumFacing facing = EnumFacing.NORTH;
    private int fuel;
    private boolean active = true;
    public double pushX = 0;
    public double pushZ = 0;
    private static final int fuelCount = 3600;

    public EntityScabystMinecartFurnace(World worldIn)
    {
        super(worldIn);
    }

    public EntityScabystMinecartFurnace(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
        
    }

    public EntityScabystMinecartFurnace(World worldIn, double x, double y, double z, boolean dual, EnumFacing facingIn)
    {
        super(worldIn, x, y, z);
        this.dual = dual;
        this.facing = facingIn;
        this.setRotation(facing.getHorizontalAngle(), this.rotationPitch);
    }

    public static void registerFixesMinecartFurnace(DataFixer fixer)
    {
        EntityMinecart.registerFixesMinecart(fixer, EntityScabystMinecartFurnace.class);
    }

	@Override
	public Type getTrueType() {
		if(isDual()) {
			return Type.DUAL_FURNACE;
		}
		return Type.FURNACE;
	}

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(POWERED, Boolean.valueOf(false));
    }

    @Override
    public String getName() {

        if (this.hasCustomName())
        {
            return this.getCustomNameTag();
        }
        
        if(isDual()) {
    		return I18n.translateToLocal("entity.scabyst_furnace_minecart_dual.name");
    	}
        
    	return super.getName();
    }
    
    @Override
    public void onUpdate()
    {
    	
    	EnumFacing prevFacing = facing;
    	
        super.onUpdate();
        
    	if(isDual() && facing != null && facing != prevFacing && !world.isRemote) {
    		//Main.logger.info("sending update facing message");
    		Main.NETWORK_CHANNEL.sendToAllTracking(new MinecartFacingMessage(facing, this.getUniqueID()), this);
    	}
        
        
        if (this.fuel > 0 && active)
        {
            --this.fuel;
            if(this.fuel > 0 && this.isDual()) { --this.fuel; }
        }

        if (this.fuel <= 0 || !active)
        {
            this.pushX = 0.0D;
            this.pushZ = 0.0D;
        } else if(this.isDual()) {
            this.pushX = Math.signum(facing.getFrontOffsetX());
            this.pushZ = Math.signum(facing.getFrontOffsetZ());
            this.setRotation(facing.getHorizontalAngle(), this.rotationPitch);
        }

        this.setMinecartPowered(this.fuel > 0);

        if (this.isMinecartPowered() && (this.rand.nextInt(4) == 0 || (this.isDual() && this.rand.nextInt(4) == 0)))
        {
            this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
        }

    }
    
    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
    	super.onActivatorRailPass(x, y, z, receivingPower);
    	
    	this.active = !receivingPower;
    	
    	if(!this.active) {
    		this.motionX = 0;
    		this.motionZ = 0;
    	}
    }

    //this is just for when its off the rails
    @Override
    protected double getMaximumSpeed()
    {
        return 0.2D;
    }
    
    private EnumRailDirection prevRailDir = null;
    
    @Override
    protected void moveAlongTrack(BlockPos pos, IBlockState state) {
    	super.moveAlongTrack(pos, state);

    	if(this.isDual()) {
        	EnumRailDirection railDir = state.getBlock() instanceof BlockRailBase ? ((BlockRailBase)state.getBlock()).getRailDirection(world, pos, state, null) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            
        	if(prevRailDir == null) {
        		prevRailDir = railDir;
        	}
        	
        	if(RailDirectionHelper.isCurved(railDir)) {
        		this.facing = RailDirectionHelper.followCurve(railDir, this.facing);
        	} else if(railDir != prevRailDir) {
        		this.facing = RailDirectionHelper.matchToRail(railDir, this.facing);
        	}
        	
    		prevRailDir = railDir;
    	}
		else {

	        double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;

	        if (d0 > 1.0E-4D && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D)
	        {
	            d0 = (double)MathHelper.sqrt(d0);
	            this.pushX /= d0;
	            this.pushZ /= d0;

	            if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0D)
	            {
	                this.pushX = 0.0D;
	                this.pushZ = 0.0D;
	            }
	            else
	            {
	                double d1 = d0 / this.getMaximumSpeed();
	                this.pushX *= d1;
	                this.pushZ *= d1;
	            }
	        }
		}
    }

    public void killMinecart(DamageSource source)
    {
        super.killMinecart(source);

        if (!source.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops"))
        {
        	if(this.isDual()) {
        		this.entityDropItem(new ItemStack(BlockRegistry.SULFUR_FURNACE_DUAL, 1), 0.0F);
        	} else {
        		this.entityDropItem(new ItemStack(BlockRegistry.SULFUR_FURNACE, 1), 0.0F);
        	}
        	
        	if(this.fuel > fuelCount) {
        		this.entityDropItem(EnumItemMisc.SULFUR.create(Math.floorDiv(fuel, fuelCount)), 0.0F);
        	}
        }
    }

    @Override
    protected void applyDrag()
    {
        double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        
        if (d0 > 1.0E-4D && this.active)
        {
            d0 = (double)MathHelper.sqrt(d0);
            this.pushX /= d0;
            this.pushZ /= d0;
			double d1 = 1.0D;
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.800000011920929D;
            this.motionX += this.pushX * d1;
            this.motionZ += this.pushZ * d1;
        }
        else
        {
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.9800000190734863D;
        }

        super.applyDrag();
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (super.processInitialInteract(player, hand)) return true;

        if (EnumItemMisc.SULFUR.isItemOf(itemstack) && this.fuel + fuelCount <= 32000)
        {
            if (!player.capabilities.isCreativeMode)
            {
                itemstack.shrink(1);
            }

            this.fuel += fuelCount;
        }

        if(!isDual()) {
          this.pushX = this.posX - player.posX;
          this.pushZ = this.posZ - player.posZ;
        }
        return true;
    }

    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Dual", this.dual);
        compound.setBoolean("Active", this.active);
        compound.setByte("Facing", (byte)this.facing.getHorizontalIndex());
        compound.setDouble("PushX", this.pushX);
        compound.setDouble("PushZ", this.pushZ);
        compound.setShort("Fuel", (short)this.fuel);
    }

    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.dual = compound.getBoolean("Dual");
        this.active = compound.getBoolean("Active");
        this.facing = EnumFacing.getHorizontal(compound.getByte("Facing"));
//        setFacing(facing);
        this.fuel = compound.getShort("Fuel");
        this.pushX = compound.getDouble("PushX");
        this.pushZ = compound.getDouble("PushZ");
    }

    protected boolean isMinecartPowered()
    {
        return ((Boolean)this.dataManager.get(POWERED)).booleanValue();
    }

    protected void setMinecartPowered(boolean p_94107_1_)
    {
        this.dataManager.set(POWERED, Boolean.valueOf(p_94107_1_));
    }

    public void setFacing(EnumFacing facing)
    {
//    	Main.logger.info("setfacing called with facing {}", facing);
        this.facing = facing;
    }

    public boolean isDual() {
    	return this.dual;
    }
    
    @Override
    public IBlockState getDefaultDisplayTile()
    {
    	EnumFacing facing = this.facing;
    	
    	if(facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
    		facing = facing.rotateY();
    	}
    	
    	if(isDual()) {
    		return (this.isMinecartPowered() ? BlockRegistry.SULFUR_FURNACE_DUAL_ACTIVE : BlockRegistry.SULFUR_FURNACE_DUAL).getDefaultState().withProperty(BlockBLDualFurnace.FACING, facing.getOpposite());
    	} else {
    		return (this.isMinecartPowered() ? BlockRegistry.SULFUR_FURNACE_ACTIVE : BlockRegistry.SULFUR_FURNACE).getDefaultState().withProperty(BlockBLFurnace.FACING, facing.getOpposite());
    	}
    }

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeBoolean(this.dual);
		buffer.writeByte(this.facing.getHorizontalIndex());
		buffer.writeBoolean(this.active);
		buffer.writeInt(this.fuel);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.dual = additionalData.readBoolean();
		this.facing = EnumFacing.getHorizontal(additionalData.readByte());
		this.active = additionalData.readBoolean();
		this.fuel = additionalData.readInt();
	}
}
