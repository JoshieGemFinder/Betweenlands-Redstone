package com.joshiegemfinder.betweenlandsredstone.items;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.config.properties.ItemDecayFoodProperty.DecayFoodStats;
import thebetweenlands.common.handler.FoodSicknessHandler;
import thebetweenlands.common.handler.OverworldItemHandler;
import thebetweenlands.common.network.clientbound.MessageShowFoodSicknessLine;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemFoodBlock extends ItemFood implements IModelInterface {
	
	public static class CustomFoodSicknessHandler {
		
		protected static Method addSicknessMessageMethod;
		
		public static void addSicknessMessage(EntityPlayer player, ItemStack item, FoodSickness sickness) {
			try {
		    	if(addSicknessMessageMethod == null) {
		    		addSicknessMessageMethod = FoodSicknessHandler.class.getDeclaredMethod("addSicknessMessage", EntityPlayer.class, ItemStack.class, FoodSickness.class);
		    	}
		    	addSicknessMessageMethod.setAccessible(true);
		    	addSicknessMessageMethod.invoke(null, player, item, sickness);
	    	} catch(Exception e) {
	    		BetweenlandsRedstone.logger.info("Error triggering addSicknessMessage");
	    	}
		}
		
		public static void onUseItem(EntityPlayer player, World worldIn, Item item, int sicknessIncrease) {
			ItemStack itemStack = new ItemStack(item);
			IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
			if(cap != null) {

				FoodSickness lastSickness = cap.getLastSickness();

				int prevFoodHatred = cap.getFoodHatred(item);
				FoodSickness currentSickness = cap.getSickness(item);

				if(player.world.isRemote) {
					if(currentSickness != lastSickness && lastSickness == FoodSickness.SICK) {
						addSicknessMessage(player, itemStack, currentSickness);
					}
				}

				if(currentSickness == FoodSickness.SICK) {
					if(item instanceof ItemFood) {
						int foodLevel = ((ItemFood)item).getHealAmount(itemStack);
						double foodLoss = 1.0D / 3.0D * 2.0;

						if(player.world.isRemote) {
							//Remove all gained food on client side and wait for sync
							player.getFoodStats().addStats(-Math.min(MathHelper.ceil(foodLevel * foodLoss), foodLevel), 0.0F);
						} else {
							int minFoodGain = player.world.rand.nextInt(4) == 0 ? 1 : 0;
							player.getFoodStats().addStats(-Math.min(MathHelper.ceil(foodLevel * foodLoss), Math.max(foodLevel - minFoodGain, 0)), 0.0F);
						}
					}

					DecayFoodStats decayFoodStats = OverworldItemHandler.getDecayFoodStats(itemStack);
					if(decayFoodStats != null) {
						IDecayCapability decayCap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
						if(decayCap != null) {
							int decayLevel = decayFoodStats.decay;
							DecayStats decayStats = decayCap.getDecayStats();
							double decayLoss = 1.0D / 3.0D * 2.0;

							if (player.world.isRemote) {
								//Remove all gained decay on client side and wait for sync
								decayStats.addStats(-Math.min(MathHelper.ceil(decayLevel * decayLoss), decayLevel), 0.0F);
							} else {
								int minDecayGain = player.world.rand.nextInt(4) == 0 ? 1 : 0;
								decayStats.addStats(-Math.min(MathHelper.ceil(decayLevel * decayLoss), Math.max(decayLevel - minDecayGain, 0)), 0.0F);
							}
						}
					}

					if(!player.world.isRemote) {
						cap.increaseFoodHatred(item, sicknessIncrease, 0);
					}
				} else {
					if(!player.world.isRemote) {
						cap.increaseFoodHatred(item, sicknessIncrease, prevFoodHatred <= 2 * 5 ? 4 : 3);
					}
				}

				FoodSickness newSickness = cap.getSickness(item);

				if(!player.world.isRemote && player instanceof EntityPlayerMP) {
					if(newSickness != lastSickness) {
						TheBetweenlands.networkWrapper.sendTo(new MessageShowFoodSicknessLine(itemStack, newSickness), (EntityPlayerMP) player);
					}
				}

				cap.setLastSickness(newSickness);
			}
		}
	}
	
    protected final Block block;
    public final boolean foodSickness;
    public final Item sicknessItem;
    public final int sicknessIncrease;
    
	public ItemFoodBlock(Block block, String name, int amount, float saturation, boolean isWolfFood, boolean foodSickness, Item customSicknessItem, int customSicknessIncrease) {
		super(amount, saturation, isWolfFood);
		this.block = block;
		this.foodSickness = foodSickness;
		if(customSicknessItem == null) {
			this.sicknessItem = this;
		} else {
			this.sicknessItem = customSicknessItem;
		}
		this.sicknessIncrease = customSicknessIncrease;

        setUnlocalizedName(name);
        setRegistryName(name);

        ModItems.ITEMS.add(this);
	}
    
	public ItemFoodBlock(Block block, String name, int amount, float saturation, boolean isWolfFood, boolean foodSickness, Item customSicknessItem) {
		this(block, name, amount, saturation, isWolfFood, foodSickness, customSicknessItem, 5);
	}
    
	public ItemFoodBlock(Block block, String name, int amount, float saturation, boolean isWolfFood, boolean foodSickness) {
		this(block, name, amount, saturation, isWolfFood, foodSickness, null, 5);
	}
    
	public ItemFoodBlock(Block block, String name, int amount, float saturation, boolean isWolfFood) {
		this(block, name, amount, saturation, isWolfFood, false, null, 5);
	}

	@Override
	public void registerModels() {
		BetweenlandsRedstone.proxy.registerItemRenderer(this, 0, "inventory");
	}

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, player))
        {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
                iblockstate1 = worldIn.getBlockState(pos);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {

    	ItemStack eatResult = super.onItemUseFinish(stack, worldIn, entityLiving);
    	
        if(entityLiving instanceof EntityPlayer && this.foodSickness && FoodSicknessHandler.isFoodSicknessEnabled(worldIn)) {
        	CustomFoodSicknessHandler.onUseItem((EntityPlayer)entityLiving, worldIn, this.sicknessItem, this.sicknessIncrease);
        }
        
    	return eatResult;
    }
    
    public static boolean setTileEntityNBT(World worldIn, @Nullable EntityPlayer player, BlockPos pos, ItemStack stackIn)
    {
        MinecraftServer minecraftserver = worldIn.getMinecraftServer();

        if (minecraftserver == null)
        {
            return false;
        }
        else
        {
            NBTTagCompound nbttagcompound = stackIn.getSubCompound("BlockEntityTag");

            if (nbttagcompound != null)
            {
                TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity != null)
                {
                    if (!worldIn.isRemote && tileentity.onlyOpsCanSetNbt() && (player == null || !player.canUseCommandBlock()))
                    {
                        return false;
                    }

                    NBTTagCompound nbttagcompound1 = tileentity.writeToNBT(new NBTTagCompound());
                    NBTTagCompound nbttagcompound2 = nbttagcompound1.copy();
                    nbttagcompound1.merge(nbttagcompound);
                    nbttagcompound1.setInteger("x", pos.getX());
                    nbttagcompound1.setInteger("y", pos.getY());
                    nbttagcompound1.setInteger("z", pos.getZ());

                    if (!nbttagcompound1.equals(nbttagcompound2))
                    {
                        tileentity.readFromNBT(nbttagcompound1);
                        tileentity.markDirty();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos))
        {
            side = EnumFacing.UP;
        }
        else if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        return worldIn.mayPlace(this.block, pos, false, side, player);
    }

    public String getUnlocalizedName(ItemStack stack)
    {
        return this.block.getUnlocalizedName();
    }

    public String getUnlocalizedName()
    {
        return this.block.getUnlocalizedName();
    }

    public CreativeTabs getCreativeTab()
    {
        return this.block.getCreativeTabToDisplayOn();
    }

    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            this.block.getSubBlocks(tab, items);
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        this.block.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public Block getBlock()
    {
        return this.getBlockRaw() == null ? null : this.getBlockRaw().delegate.get();
    }

    private Block getBlockRaw()
    {
        return this.block;
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if (!world.setBlockState(pos, newState, 11)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block)
        {
            setTileEntityNBT(world, player, pos, stack);
            this.block.onBlockPlacedBy(world, pos, state, player, stack);

            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
        }

        return true;
    }
}
