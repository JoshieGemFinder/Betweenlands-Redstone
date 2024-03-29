package com.joshiegemfinder.betweenlandsredstone.entity.minecart;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.joshiegemfinder.betweenlandsredstone.ModBlocks;
import com.joshiegemfinder.betweenlandsredstone.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityScabystMinecart extends EntityMinecart {

    private static final DataParameter<Integer> ROLLING_AMPLITUDE = EntityDataManager.<Integer>createKey(EntityMinecart.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ROLLING_DIRECTION = EntityDataManager.<Integer>createKey(EntityMinecart.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE = EntityDataManager.<Float>createKey(EntityMinecart.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> DISPLAY_TILE = EntityDataManager.<Integer>createKey(EntityMinecart.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DISPLAY_TILE_OFFSET = EntityDataManager.<Integer>createKey(EntityMinecart.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SHOW_BLOCK = EntityDataManager.<Boolean>createKey(EntityMinecart.class, DataSerializers.BOOLEAN);
    private boolean isInReverse;
    private static final int[][][] MATRIX = new int[][][] {{{0, 0, -1}, {0, 0, 1}}, {{ -1, 0, 0}, {1, 0, 0}}, {{ -1, -1, 0}, {1, 0, 0}}, {{ -1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, { -1, 0, 0}}, {{0, 0, -1}, { -1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
    private int turnProgress;
    private double minecartX;
    private double minecartY;
    private double minecartZ;
    private double minecartYaw;
    private double minecartPitch;
    @SideOnly(Side.CLIENT)
    private double velocityX;
    @SideOnly(Side.CLIENT)
    private double velocityY;
    @SideOnly(Side.CLIENT)
    private double velocityZ;

    /* Forge: Minecart Compatibility Layer Integration. */
    public static float defaultMaxSpeedAirLateral = 0.4f;
    public static float defaultMaxSpeedAirVertical = -1f;
    public static double defaultDragAir = 0.94999998807907104D;
    protected boolean canUseRail = true;
    protected boolean canBePushed = true;
    private static net.minecraftforge.common.IMinecartCollisionHandler collisionHandler = null;

    /* Instance versions of the above physics properties */
    @SuppressWarnings("unused")
	private float currentSpeedRail = getMaxCartSpeedOnRail();
    protected float maxSpeedAirLateral = defaultMaxSpeedAirLateral;
    protected float maxSpeedAirVertical = defaultMaxSpeedAirVertical;
    protected double dragAir = defaultDragAir;

    public EntityScabystMinecart(World worldIn)
    {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.7F);
    }

    public static EntityScabystMinecart create(World worldIn, double x, double y, double z, EntityScabystMinecart.Type typeIn, EnumFacing direction)
    {
        switch (typeIn)
        {
            case CHEST:
                return new EntityScabystMinecartChest(worldIn, x, y, z);
            case FURNACE:
                return new EntityScabystMinecartFurnace(worldIn, x, y, z, false, direction);
            case DUAL_FURNACE:
                return new EntityScabystMinecartFurnace(worldIn, x, y, z, true, direction);
//            case TNT:
//                return new EntityMinecartTNT(worldIn, x, y, z);
//            case SPAWNER:
//                return new EntityMinecartMobSpawner(worldIn, x, y, z);
            case HOPPER:
                return new EntityScabystMinecartHopper(worldIn, x, y, z);
//            case COMMAND_BLOCK:
//                return new EntityMinecartCommandBlock(worldIn, x, y, z);
            default:
                return new EntityScabystMinecartEmpty(worldIn, x, y, z);
        }
    }

    public static EntityScabystMinecart create(World worldIn, double x, double y, double z, EntityScabystMinecart.Type typeIn) {
    	return create(worldIn, x, y, z, typeIn, EnumFacing.NORTH);
    }
    
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataManager.register(ROLLING_AMPLITUDE, Integer.valueOf(0));
        this.dataManager.register(ROLLING_DIRECTION, Integer.valueOf(1));
        this.dataManager.register(DAMAGE, Float.valueOf(0.0F));
        this.dataManager.register(DISPLAY_TILE, Integer.valueOf(0));
        this.dataManager.register(DISPLAY_TILE_OFFSET, Integer.valueOf(6));
        this.dataManager.register(SHOW_BLOCK, Boolean.valueOf(false));
    }

    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        if (getCollisionHandler() != null) return getCollisionHandler().getCollisionBox(this, entityIn);
        return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox()
    {
        if (getCollisionHandler() != null) return getCollisionHandler().getBoundingBox(this);
        return null;
    }

    public boolean canBePushed()
    {
        return canBePushed;
    }

    public EntityScabystMinecart(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    public double getMountedYOffset()
    {
        return 0.0D;
    }

    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!this.world.isRemote && !this.isDead)
        {
            if (this.isEntityInvulnerable(source))
            {
                return false;
            }
            else
            {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.markVelocityChanged();
                this.setDamage(this.getDamage() + amount * 10.0F);
                boolean flag = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer)source.getTrueSource()).capabilities.isCreativeMode;

                if (flag || this.getDamage() > 40.0F)
                {
                    this.removePassengers();

                    if (flag && !this.hasCustomName())
                    {
                        this.setDead();
                    }
                    else
                    {
                        this.killMinecart(source);
                    }
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    public void killMinecart(DamageSource source)
    {
        this.setDead();

        if (this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            ItemStack itemstack = new ItemStack(ModItems.SCABYST_MINECART, 1);

            if (this.hasCustomName())
            {
                itemstack.setStackDisplayName(this.getCustomNameTag());
            }

            this.entityDropItem(itemstack, 0.0F);
        }
    }

    @SideOnly(Side.CLIENT)
    public void performHurtAnimation()
    {
        this.setRollingDirection(-this.getRollingDirection());
        this.setRollingAmplitude(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
    }

    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    public EnumFacing getAdjustedHorizontalFacing()
    {
        return this.isInReverse ? this.getHorizontalFacing().getOpposite().rotateY() : this.getHorizontalFacing().rotateY();
    }

    public void onUpdate()
    {
        if (this.getRollingAmplitude() > 0)
        {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }

        if (this.getDamage() > 0.0F)
        {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.posY < -64.0D)
        {
            this.outOfWorld();
        }

        if (!this.world.isRemote && this.world instanceof WorldServer)
        {
            this.world.profiler.startSection("portal");
            MinecraftServer minecraftserver = this.world.getMinecraftServer();
            int i = this.getMaxInPortalTime();

            if (this.inPortal)
            {
                if (minecraftserver.getAllowNether())
                {
                    if (!this.isRiding() && this.portalCounter++ >= i)
                    {
                        this.portalCounter = i;
                        this.timeUntilPortal = this.getPortalCooldown();
                        int j;

                        if (this.world.provider.getDimensionType().getId() == -1)
                        {
                            j = 0;
                        }
                        else
                        {
                            j = -1;
                        }

                        this.changeDimension(j);
                    }

                    this.inPortal = false;
                }
            }
            else
            {
                if (this.portalCounter > 0)
                {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0)
                {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0)
            {
                --this.timeUntilPortal;
            }

            this.world.profiler.endSection();
        }

        if (this.world.isRemote)
        {
            if (this.turnProgress > 0)
            {
                double d4 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
                double d5 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
                double d6 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
                double d1 = MathHelper.wrapDegrees(this.minecartYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
                --this.turnProgress;
                this.setPosition(d4, d5, d6);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                this.setPosition(this.posX, this.posY, this.posZ);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (!this.hasNoGravity())
            {
                this.motionY -= 0.03999999910593033D;
            }

            int k = MathHelper.floor(this.posX);
            int l = MathHelper.floor(this.posY);
            int i1 = MathHelper.floor(this.posZ);

            if (BlockRailBase.isRailBlock(this.world, new BlockPos(k, l - 1, i1)))
            {
                --l;
            }

            BlockPos blockpos = new BlockPos(k, l, i1);
            IBlockState iblockstate = this.world.getBlockState(blockpos);

            if (canUseRail() && BlockRailBase.isRailBlock(iblockstate))
            {
                this.moveAlongTrack(blockpos, iblockstate);

                if (iblockstate.getBlock() == Blocks.ACTIVATOR_RAIL || iblockstate.getBlock() == ModBlocks.SCABYST_ACTIVATOR_RAIL)
                {
                    this.onActivatorRailPass(k, l, i1, ((Boolean)iblockstate.getValue(BlockRailPowered.POWERED)).booleanValue());
                }
            }
            else
            {
                this.moveDerailedMinecart();
            }

            this.doBlockCollisions();
            this.rotationPitch = 0.0F;
            double d0 = this.prevPosX - this.posX;
            double d2 = this.prevPosZ - this.posZ;

            if (d0 * d0 + d2 * d2 > 0.001D)
            {
                this.rotationYaw = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI);

                if (this.isInReverse)
                {
                    this.rotationYaw += 180.0F;
                }
            }

            double d3 = (double)MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw);

            if (d3 < -170.0D || d3 >= 170.0D)
            {
                this.rotationYaw += 180.0F;
                this.isInReverse = !this.isInReverse;
            }

            this.setRotation(this.rotationYaw, this.rotationPitch);

            AxisAlignedBB box;
            if (getCollisionHandler() != null) box = getCollisionHandler().getMinecartCollisionBox(this);
            else                               box = this.getEntityBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D);

            if (canBeRidden() && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D)
            {
                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, box, EntitySelectors.getTeamCollisionPredicate(this));

                if (!list.isEmpty())
                {
                    for (int j1 = 0; j1 < list.size(); ++j1)
                    {
                        Entity entity1 = list.get(j1);

                        if (!(entity1 instanceof EntityPlayer) && !(entity1 instanceof EntityIronGolem) && !(entity1 instanceof EntityMinecart) && !this.isBeingRidden() && !entity1.isRiding())
                        {
                            entity1.startRiding(this);
                        }
                        else
                        {
                            entity1.applyEntityCollision(this);
                        }
                    }
                }
            }
            else
            {
                for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, box))
                {
                    if (!this.isPassenger(entity) && entity.canBePushed() && entity instanceof EntityMinecart)
                    {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            this.handleWaterMovement();
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartUpdateEvent(this, this.getCurrentRailPosition()));
        }
    }

    protected double getMaximumSpeed()
    {
        return 0.4D;
    }

    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
    {
    }

    protected void moveDerailedMinecart()
    {
        double d0 = onGround ? this.getMaximumSpeed() : getMaxSpeedAirLateral();
        this.motionX = MathHelper.clamp(this.motionX, -d0, d0);
        this.motionZ = MathHelper.clamp(this.motionZ, -d0, d0);

        double moveY = motionY;
        if(getMaxSpeedAirVertical() > 0 && motionY > getMaxSpeedAirVertical())
        {
            moveY = getMaxSpeedAirVertical();
            if(Math.abs(motionX) < 0.3f && Math.abs(motionZ) < 0.3f)
            {
                moveY = 0.15f;
                motionY = moveY;
            }
        }

        if (this.onGround)
        {
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        }

        this.move(MoverType.SELF, this.motionX, moveY, this.motionZ);

        if (!this.onGround)
        {
            this.motionX *= getDragAir();
            this.motionY *= getDragAir();
            this.motionZ *= getDragAir();
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected void moveAlongTrack(BlockPos pos, IBlockState state)
    {
        this.fallDistance = 0.0F;
        Vec3d vec3d = this.getPos(this.posX, this.posY, this.posZ);
        this.posY = (double)pos.getY();
        boolean flag = false;
        boolean flag1 = false;
        BlockRailBase blockrailbase = (BlockRailBase)state.getBlock();

        if (blockrailbase == Blocks.GOLDEN_RAIL || blockrailbase == ModBlocks.SCABYST_POWERED_RAIL)
        {
            flag = ((Boolean)state.getValue(BlockRailPowered.POWERED)).booleanValue();
            flag1 = !flag;
        }

        double slopeAdjustment = getSlopeAdjustment();
        BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = blockrailbase.getRailDirection(world, pos, state, this);

        switch (blockrailbase$enumraildirection)
        {
            case ASCENDING_EAST:
                this.motionX -= slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_WEST:
                this.motionX += slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_NORTH:
                this.motionZ += slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_SOUTH:
                this.motionZ -= slopeAdjustment;
                ++this.posY;
        }

        int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
        double d1 = (double)(aint[1][0] - aint[0][0]);
        double d2 = (double)(aint[1][2] - aint[0][2]);
        double d3 = Math.sqrt(d1 * d1 + d2 * d2);
        double d4 = this.motionX * d1 + this.motionZ * d2;

        if (d4 < 0.0D)
        {
            d1 = -d1;
            d2 = -d2;
        }

        double d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if (d5 > 2.0D)
        {
            d5 = 2.0D;
        }

        this.motionX = d5 * d1 / d3;
        this.motionZ = d5 * d2 / d3;
        Entity entity = this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);

        if (entity instanceof EntityLivingBase)
        {
            double d6 = (double)((EntityLivingBase)entity).moveForward;

            if (d6 > 0.0D)
            {
                double d7 = -Math.sin((double)(entity.rotationYaw * 0.017453292F));
                double d8 = Math.cos((double)(entity.rotationYaw * 0.017453292F));
                double d9 = this.motionX * this.motionX + this.motionZ * this.motionZ;

                if (d9 < 0.01D)
                {
                    this.motionX += d7 * 0.1D;
                    this.motionZ += d8 * 0.1D;
                    flag1 = false;
                }
            }
        }

        if (flag1 && shouldDoRailFunctions())
        {
            double d17 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d17 < 0.03D)
            {
                this.motionX *= 0.0D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.0D;
            }
            else
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.5D;
            }
        }

        double d18 = (double)pos.getX() + 0.5D + (double)aint[0][0] * 0.5D;
        double d19 = (double)pos.getZ() + 0.5D + (double)aint[0][2] * 0.5D;
        double d20 = (double)pos.getX() + 0.5D + (double)aint[1][0] * 0.5D;
        double d21 = (double)pos.getZ() + 0.5D + (double)aint[1][2] * 0.5D;
        d1 = d20 - d18;
        d2 = d21 - d19;
        double d10;

        if (d1 == 0.0D)
        {
            this.posX = (double)pos.getX() + 0.5D;
            d10 = this.posZ - (double)pos.getZ();
        }
        else if (d2 == 0.0D)
        {
            this.posZ = (double)pos.getZ() + 0.5D;
            d10 = this.posX - (double)pos.getX();
        }
        else
        {
            double d11 = this.posX - d18;
            double d12 = this.posZ - d19;
            d10 = (d11 * d1 + d12 * d2) * 2.0D;
        }

        this.posX = d18 + d1 * d10;
        this.posZ = d19 + d2 * d10;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.moveMinecartOnRail(pos);

        if (aint[0][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[0][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[0][2])
        {
            this.setPosition(this.posX, this.posY + (double)aint[0][1], this.posZ);
        }
        else if (aint[1][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[1][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[1][2])
        {
            this.setPosition(this.posX, this.posY + (double)aint[1][1], this.posZ);
        }

        this.applyDrag();
        Vec3d vec3d1 = this.getPos(this.posX, this.posY, this.posZ);

        if (vec3d1 != null && vec3d != null)
        {
            double d14 = (vec3d.y - vec3d1.y) * 0.05D;
            d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d5 > 0.0D)
            {
                this.motionX = this.motionX / d5 * (d5 + d14);
                this.motionZ = this.motionZ / d5 * (d5 + d14);
            }

            this.setPosition(this.posX, vec3d1.y, this.posZ);
        }

        int j = MathHelper.floor(this.posX);
        int i = MathHelper.floor(this.posZ);

        if (j != pos.getX() || i != pos.getZ())
        {
            d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = d5 * (double)(j - pos.getX());
            this.motionZ = d5 * (double)(i - pos.getZ());
        }


        if(shouldDoRailFunctions())
        {
            ((BlockRailBase)state.getBlock()).onMinecartPass(world, this, pos);
        }

        if (flag && shouldDoRailFunctions())
        {
            double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d15 > 0.01D)
            {
                double d16 = 0.06D;
                this.motionX += this.motionX / d15 * d16;
                this.motionZ += this.motionZ / d15 * d16;
            }
            else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.EAST_WEST)
            {
                if (this.world.getBlockState(pos.west()).isNormalCube())
                {
                    this.motionX = 0.02D;
                }
                else if (this.world.getBlockState(pos.east()).isNormalCube())
                {
                    this.motionX = -0.02D;
                }
            }
            else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH)
            {
                if (this.world.getBlockState(pos.north()).isNormalCube())
                {
                    this.motionZ = 0.02D;
                }
                else if (this.world.getBlockState(pos.south()).isNormalCube())
                {
                    this.motionZ = -0.02D;
                }
            }
        }
    }

    protected void applyDrag()
    {
        if (this.isBeingRidden())
        {
            this.motionX *= 0.996999979019165D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.996999979019165D;
        }
        else
        {
            this.motionX *= 0.9599999785423279D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.9599999785423279D;
        }
    }

    public void setPosition(double x, double y, double z)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0F;
        float f1 = this.height;
        this.setEntityBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public Vec3d getPosOffset(double x, double y, double z, double offset)
    {
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);

        if (BlockRailBase.isRailBlock(this.world, new BlockPos(i, j - 1, k)))
        {
            --j;
        }

        IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));

        if (BlockRailBase.isRailBlock(iblockstate))
        {
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = ((BlockRailBase)iblockstate.getBlock()).getRailDirection(world, new BlockPos(i, j, k), iblockstate, this);
            y = (double)j;

            if (blockrailbase$enumraildirection.isAscending())
            {
                y = (double)(j + 1);
            }

            int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
            double d0 = (double)(aint[1][0] - aint[0][0]);
            double d1 = (double)(aint[1][2] - aint[0][2]);
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            d0 = d0 / d2;
            d1 = d1 / d2;
            x = x + d0 * offset;
            z = z + d1 * offset;

            if (aint[0][1] != 0 && MathHelper.floor(x) - i == aint[0][0] && MathHelper.floor(z) - k == aint[0][2])
            {
                y += (double)aint[0][1];
            }
            else if (aint[1][1] != 0 && MathHelper.floor(x) - i == aint[1][0] && MathHelper.floor(z) - k == aint[1][2])
            {
                y += (double)aint[1][1];
            }

            return this.getPos(x, y, z);
        }
        else
        {
            return null;
        }
    }

    @Nullable
    public Vec3d getPos(double p_70489_1_, double p_70489_3_, double p_70489_5_)
    {
        int i = MathHelper.floor(p_70489_1_);
        int j = MathHelper.floor(p_70489_3_);
        int k = MathHelper.floor(p_70489_5_);

        if (BlockRailBase.isRailBlock(this.world, new BlockPos(i, j - 1, k)))
        {
            --j;
        }

        IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));

        if (BlockRailBase.isRailBlock(iblockstate))
        {
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = ((BlockRailBase)iblockstate.getBlock()).getRailDirection(world, new BlockPos(i, j, k), iblockstate, this);
            int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
            double d0 = (double)i + 0.5D + (double)aint[0][0] * 0.5D;
            double d1 = (double)j + 0.0625D + (double)aint[0][1] * 0.5D;
            double d2 = (double)k + 0.5D + (double)aint[0][2] * 0.5D;
            double d3 = (double)i + 0.5D + (double)aint[1][0] * 0.5D;
            double d4 = (double)j + 0.0625D + (double)aint[1][1] * 0.5D;
            double d5 = (double)k + 0.5D + (double)aint[1][2] * 0.5D;
            double d6 = d3 - d0;
            double d7 = (d4 - d1) * 2.0D;
            double d8 = d5 - d2;
            double d9;

            if (d6 == 0.0D)
            {
                d9 = p_70489_5_ - (double)k;
            }
            else if (d8 == 0.0D)
            {
                d9 = p_70489_1_ - (double)i;
            }
            else
            {
                double d10 = p_70489_1_ - d0;
                double d11 = p_70489_5_ - d2;
                d9 = (d10 * d6 + d11 * d8) * 2.0D;
            }

            p_70489_1_ = d0 + d6 * d9;
            p_70489_3_ = d1 + d7 * d9;
            p_70489_5_ = d2 + d8 * d9;

            if (d7 < 0.0D)
            {
                ++p_70489_3_;
            }

            if (d7 > 0.0D)
            {
                p_70489_3_ += 0.5D;
            }

            return new Vec3d(p_70489_1_, p_70489_3_, p_70489_5_);
        }
        else
        {
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        return this.hasDisplayTile() ? axisalignedbb.grow((double)Math.abs(this.getDisplayTileOffset()) / 16.0D) : axisalignedbb;
    }

    public static void registerFixesMinecart(DataFixer fixer, Class<?> name)
    {
    }

    @SuppressWarnings("deprecation")
	protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if (compound.getBoolean("CustomDisplayTile"))
        {
            Block block;

            if (compound.hasKey("DisplayTile", 8))
            {
                block = Block.getBlockFromName(compound.getString("DisplayTile"));
            }
            else
            {
                block = Block.getBlockById(compound.getInteger("DisplayTile"));
            }

            int i = compound.getInteger("DisplayData");
            this.setDisplayTile(block == null ? Blocks.AIR.getDefaultState() : block.getStateFromMeta(i));
            this.setDisplayTileOffset(compound.getInteger("DisplayOffset"));
        }
    }

    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        if (this.hasDisplayTile())
        {
            compound.setBoolean("CustomDisplayTile", true);
            IBlockState iblockstate = this.getDisplayTile();
            ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(iblockstate.getBlock());
            compound.setString("DisplayTile", resourcelocation == null ? "" : resourcelocation.toString());
            compound.setInteger("DisplayData", iblockstate.getBlock().getMetaFromState(iblockstate));
            compound.setInteger("DisplayOffset", this.getDisplayTileOffset());
        }
    }

    public double inverseHalfDecimal(double x) {
    	return 1d - ((1d - x) * 0.5D);
    }
    
    public void applyEntityCollision(Entity entityIn)
    {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartCollisionEvent(this, entityIn));
        if (getCollisionHandler() != null)
        {
            getCollisionHandler().onEntityCollision(this, entityIn);
            return;
        }
        if (!this.world.isRemote)
        {
            if (!entityIn.noClip && !this.noClip)
            {
                if (!this.isPassenger(entityIn))
                {
                    double d0 = entityIn.posX - this.posX;
                    double d1 = entityIn.posZ - this.posZ;
                    double d2 = d0 * d0 + d1 * d1;

                    if (d2 >= 9.999999747378752E-5D)
                    {
                        d2 = (double)MathHelper.sqrt(d2);
                        d0 = d0 / d2;
                        d1 = d1 / d2;
                        double d3 = 1.0D / d2;

                        if (d3 > 1.0D)
                        {
                            d3 = 1.0D;
                        }

                        d0 = d0 * d3;
                        d1 = d1 * d3;
                        d0 = d0 * 0.10000000149011612D;
                        d1 = d1 * 0.10000000149011612D;
                        d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                        d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
                        d0 = d0 * 0.5D;
                        d1 = d1 * 0.5D;

                        if (entityIn instanceof EntityMinecart)
                        {
                            double d4 = entityIn.posX - this.posX;
                            double d5 = entityIn.posZ - this.posZ;
                            Vec3d vec3d = (new Vec3d(d4, 0.0D, d5)).normalize();
                            Vec3d vec3d1 = (new Vec3d((double)MathHelper.cos(this.rotationYaw * 0.017453292F), 0.0D, (double)MathHelper.sin(this.rotationYaw * 0.017453292F))).normalize();
                            double d6 = Math.abs(vec3d.dotProduct(vec3d1));

                            if (d6 < 0.800000011920929D)
                            {
                                return;
                            }

                            double d7 = entityIn.motionX + this.motionX;
                            double d8 = entityIn.motionZ + this.motionZ;

                            if (((EntityMinecart)entityIn).isPoweredCart() && !isPoweredCart())
                            {
                                this.motionX *= 0.20000000298023224D;
                                this.motionZ *= 0.20000000298023224D;
                                this.addVelocity(entityIn.motionX - d0, 0.0D, entityIn.motionZ - d1);
                                entityIn.motionX *= 0.949999988079071D;
                                entityIn.motionZ *= 0.949999988079071D;
                            }
                            else if (!((EntityMinecart)entityIn).isPoweredCart() && isPoweredCart())
                            {
                                entityIn.motionX *= 0.20000000298023224D;
                                entityIn.motionZ *= 0.20000000298023224D;
                                entityIn.addVelocity(this.motionX + d0, 0.0D, this.motionZ + d1);
                                this.motionX *= 0.949999988079071D;
                                this.motionZ *= 0.949999988079071D;
                            }
                            else
                            {
                                d7 = d7 / 2.0D;
                                d8 = d8 / 2.0D;
                                this.motionX *= 0.20000000298023224D;
                                this.motionZ *= 0.20000000298023224D;
                                this.addVelocity(d7 - d0, 0.0D, d8 - d1);
                                entityIn.motionX *= 0.20000000298023224D;
                                entityIn.motionZ *= 0.20000000298023224D;
                                entityIn.addVelocity(d7 + d0, 0.0D, d8 + d1);
                            }
                        }
                        else
                        {
                            this.addVelocity(-d0, 0.0D, -d1);
                            entityIn.addVelocity(d0 / 4.0D, 0.0D, d1 / 4.0D);
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.minecartX = x;
        this.minecartY = y;
        this.minecartZ = z;
        this.minecartYaw = (double)yaw;
        this.minecartPitch = (double)pitch;
        this.turnProgress = posRotationIncrements + 2;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    public void setDamage(float damage)
    {
        this.dataManager.set(DAMAGE, Float.valueOf(damage));
    }

    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        this.velocityX = this.motionX;
        this.velocityY = this.motionY;
        this.velocityZ = this.motionZ;
    }

    public float getDamage()
    {
        return ((Float)this.dataManager.get(DAMAGE)).floatValue();
    }

    public void setRollingAmplitude(int rollingAmplitude)
    {
        this.dataManager.set(ROLLING_AMPLITUDE, Integer.valueOf(rollingAmplitude));
    }

    public int getRollingAmplitude()
    {
        return ((Integer)this.dataManager.get(ROLLING_AMPLITUDE)).intValue();
    }

    public void setRollingDirection(int rollingDirection)
    {
        this.dataManager.set(ROLLING_DIRECTION, Integer.valueOf(rollingDirection));
    }

    public int getRollingDirection()
    {
        return ((Integer)this.dataManager.get(ROLLING_DIRECTION)).intValue();
    }

    public abstract EntityScabystMinecart.Type getTrueType();

    public EntityMinecart.Type getType() {
    	return this.getTrueType().getType();
    }

    public IBlockState getDisplayTile()
    {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(((Integer)this.getDataManager().get(DISPLAY_TILE)).intValue());
    }

    public IBlockState getDefaultDisplayTile()
    {
        return Blocks.AIR.getDefaultState();
    }

    public int getDisplayTileOffset()
    {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : ((Integer)this.getDataManager().get(DISPLAY_TILE_OFFSET)).intValue();
    }

    public int getDefaultDisplayTileOffset()
    {
        return 6;
    }

    public void setDisplayTile(IBlockState displayTile)
    {
        this.getDataManager().set(DISPLAY_TILE, Integer.valueOf(Block.getStateId(displayTile)));
        this.setHasDisplayTile(true);
    }

    public void setDisplayTileOffset(int displayTileOffset)
    {
        this.getDataManager().set(DISPLAY_TILE_OFFSET, Integer.valueOf(displayTileOffset));
        this.setHasDisplayTile(true);
    }

    public boolean hasDisplayTile()
    {
        return ((Boolean)this.getDataManager().get(SHOW_BLOCK)).booleanValue();
    }

    public void setHasDisplayTile(boolean showBlock)
    {
        this.getDataManager().set(SHOW_BLOCK, Boolean.valueOf(showBlock));
    }
    
    @Override
    public boolean processInitialInteract(EntityPlayer player, net.minecraft.util.EnumHand hand)
    {
        return net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartInteractEvent(this, player, hand));
    }

    public int getTurnProgress() {
    	return this.turnProgress;
    }
    
    /* =================================== FORGE START ===========================================*/
    protected BlockPos getCurrentRailPosition()
    {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);

        if (BlockRailBase.isRailBlock(this.world, new BlockPos(x, y - 1, z))) y--;
        return new BlockPos(x, y, z);
    }

    protected double getMaxSpeed()
    {
        if (!canUseRail()) return getMaximumSpeed();
        BlockPos pos = this.getCurrentRailPosition();
        IBlockState state = this.world.getBlockState(pos);
        if (!BlockRailBase.isRailBlock(state)) return getMaximumSpeed();

        float railMaxSpeed = ((BlockRailBase)state.getBlock()).getRailMaxSpeed(world, this, pos);
        return Math.min(railMaxSpeed, getCurrentCartSpeedCapOnRail());
    }

    /**
     * Moved to allow overrides.
     * This code handles minecart movement and speed capping when on a rail.
     */
    public void moveMinecartOnRail(BlockPos pos)
    {
        double mX = this.motionX;
        double mZ = this.motionZ;

        if (this.isBeingRidden())
        {
            mX *= 0.75D;
            mZ *= 0.75D;
        }

        double max = this.getMaxSpeed();
        mX = MathHelper.clamp(mX, -max, max);
        mZ = MathHelper.clamp(mZ, -max, max);
        this.move(MoverType.SELF, mX, 0.0D, mZ);
    }

    /**
     * Gets the current global Minecart Collision handler if none
     * is registered, returns null
     * @return The collision handler or null
     */
    @Nullable
    public static net.minecraftforge.common.IMinecartCollisionHandler getCollisionHandler()
    {
        return collisionHandler;
    }

    /**
     * Sets the global Minecart Collision handler, overwrites any
     * that is currently set.
     * @param handler The new handler
     */
    public static void setCollisionHandler(net.minecraftforge.common.IMinecartCollisionHandler handler)
    {
        collisionHandler = handler;
    }

    /**
     * This function returns an ItemStack that represents this cart.
     * This should be an ItemStack that can be used by the player to place the cart,
     * but is not necessary the item the cart drops when destroyed.
     * @return An ItemStack that can be used to place the cart.
     */
    //TODO
    public ItemStack getCartItem()
    {
        if (this instanceof EntityScabystMinecartFurnace)
        {
        	if(((EntityScabystMinecartFurnace)this).isDual()) {
                return new ItemStack(ModItems.SCABYST_DUAL_FURNACE_MINECART);
        	} else {
                return new ItemStack(ModItems.SCABYST_FURNACE_MINECART);
        	}
        }
        else if (this instanceof EntityScabystMinecartChest)
        {
            return new ItemStack(ModItems.SCABYST_CHEST_MINECART);
        }/*
        else if (this instanceof EntityMinecartTNT)
        {
            return new ItemStack(Items.TNT_MINECART);
        }*/
        else if (this instanceof EntityScabystMinecartHopper)
        {
            return new ItemStack(ModItems.SCABYST_HOPPER_MINECART);
        }/*
        else if (this instanceof EntityMinecartCommandBlock)
        {
            return new ItemStack(Items.COMMAND_BLOCK_MINECART);
        }*/
        return new ItemStack(ModItems.SCABYST_MINECART);
    }

    public float getMaxSpeedAirLateral()
    {
        return maxSpeedAirLateral;
    }

    public void setMaxSpeedAirLateral(float value)
    {
        maxSpeedAirLateral = value;
    }

    public float getMaxSpeedAirVertical()
    {
        return maxSpeedAirVertical;
    }

    public void setMaxSpeedAirVertical(float value)
    {
        maxSpeedAirVertical = value;
    }

    public double getDragAir()
    {
        return dragAir;
    }

    public void setDragAir(double value)
    {
        dragAir = value;
    }

    public double getSlopeAdjustment()
    {
        return 0.0078125D;
    }

    /**
     * Called from Detector Rails to retrieve a redstone power level for comparators.
     */
    public int getComparatorLevel()
    {
        return -1;
    }

    /* =================================== FORGE END ===========================================*/


    public static enum Type
    {
        RIDEABLE(0, "MinecartRideable", EntityMinecart.Type.RIDEABLE),
        CHEST(1, "MinecartChest", EntityMinecart.Type.CHEST),
        FURNACE(2, "MinecartFurnace", EntityMinecart.Type.FURNACE),
        DUAL_FURNACE(7, "MinecartDualFurnace", EntityMinecart.Type.FURNACE),
        TNT(3, "MinecartTNT", EntityMinecart.Type.TNT),
        SPAWNER(4, "MinecartSpawner", EntityMinecart.Type.SPAWNER),
        HOPPER(5, "MinecartHopper", EntityMinecart.Type.HOPPER),
        COMMAND_BLOCK(6, "MinecartCommandBlock", EntityMinecart.Type.COMMAND_BLOCK);

        private static final Map<Integer, EntityScabystMinecart.Type> BY_ID = Maps.<Integer, EntityScabystMinecart.Type>newHashMap();
        private final int id;
        private final String name;
        private final EntityMinecart.Type weak;

        private Type(int idIn, String nameIn, EntityMinecart.Type weakType)
        {
            this.id = idIn;
            this.name = nameIn;
            this.weak = weakType;
        }

        public int getId()
        {
            return this.id;
        }

        public String getName()
        {
            return this.name;
        }
        
        public EntityMinecart.Type getType() {
        	return this.weak;
        }

        @SideOnly(Side.CLIENT)
        public static EntityScabystMinecart.Type getById(int idIn)
        {
        	EntityScabystMinecart.Type entityminecart$type = BY_ID.get(Integer.valueOf(idIn));
            return entityminecart$type == null ? RIDEABLE : entityminecart$type;
        }

        static
        {
            for (EntityScabystMinecart.Type entityminecart$type : values())
            {
                BY_ID.put(Integer.valueOf(entityminecart$type.getId()), entityminecart$type);
            }
        }
    }
}
