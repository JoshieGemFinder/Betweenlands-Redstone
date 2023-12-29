package com.joshiegemfinder.betweenlandsredstone.items;

import java.util.List;

import com.joshiegemfinder.betweenlandsredstone.BetweenlandsRedstone;
import com.joshiegemfinder.betweenlandsredstone.ModItems;
import com.joshiegemfinder.betweenlandsredstone.blocks.shared.IModelInterface;
import com.joshiegemfinder.betweenlandsredstone.entity.minecart.EntityScabystMinecart;
import com.joshiegemfinder.betweenlandsredstone.entity.minecart.RailDirectionHelper;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScabystMinecart extends Item implements IModelInterface {

    private static final IBehaviorDispenseItem MINECART_DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem()
    {
        private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
            World world = source.getWorld();
            double spawnX = source.getX() + (double)enumfacing.getFrontOffsetX() * 1.125D;
            double spawnY = Math.floor(source.getY()) + (double)enumfacing.getFrontOffsetY();
            double spawnZ = source.getZ() + (double)enumfacing.getFrontOffsetZ() * 1.125D;
            BlockPos blockpos = source.getBlockPos().offset(enumfacing);
            IBlockState iblockstate = world.getBlockState(blockpos);
            BlockRailBase.EnumRailDirection railDir = iblockstate.getBlock() instanceof BlockRailBase ? ((BlockRailBase)iblockstate.getBlock()).getRailDirection(world, blockpos, iblockstate, null) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            double d3;
            
            EnumFacing direction = RailDirectionHelper.matchToRail(railDir, source.getBlockState().getValue(BlockDispenser.FACING));
            
            if (BlockRailBase.isRailBlock(iblockstate))
            {
                if (railDir.isAscending())
                {
                    d3 = 0.6D;
                }
                else
                {
                    d3 = 0.1D;
                }
                
            }
            else
            {
                if (iblockstate.getMaterial() != Material.AIR || !BlockRailBase.isRailBlock(world.getBlockState(blockpos.down())))
                {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }

                IBlockState iblockstate1 = world.getBlockState(blockpos.down());
                BlockRailBase.EnumRailDirection blockrailbase$enumraildirection1 = iblockstate1.getBlock() instanceof BlockRailBase ? ((BlockRailBase)iblockstate1.getBlock()).getRailDirection(world, blockpos.down(), iblockstate1, null) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;

                if (enumfacing != EnumFacing.DOWN && blockrailbase$enumraildirection1.isAscending())
                {
                    d3 = -0.4D;
                }
                else
                {
                    d3 = -0.9D;
                }
            }
            
            
            EntityScabystMinecart entityminecart = EntityScabystMinecart.create(world, spawnX, spawnY + d3, spawnZ, ((ItemScabystMinecart)stack.getItem()).minecartType, direction);

            if (stack.hasDisplayName())
            {
                entityminecart.setCustomNameTag(stack.getDisplayName());
            }

            world.spawnEntity(entityminecart);
            stack.shrink(1);
            return stack;
        }
        protected void playDispenseSound(IBlockSource source)
        {
            source.getWorld().playEvent(1000, source.getBlockPos(), 0);
        }
    };
    
    private final EntityScabystMinecart.Type minecartType;

    public ItemScabystMinecart(String name, EntityScabystMinecart.Type typeIn)
    {
        this.maxStackSize = 1;
        this.minecartType = typeIn;
        this.setCreativeTab(CreativeTabs.TRANSPORTATION);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, MINECART_DISPENSER_BEHAVIOR);
        
        
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        
        ModItems.ITEMS.add(this);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (!BlockRailBase.isRailBlock(iblockstate))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            ItemStack itemstack = player.getHeldItem(hand);

            if (!worldIn.isRemote)
            {
                BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getBlock() instanceof BlockRailBase ? ((BlockRailBase)iblockstate.getBlock()).getRailDirection(worldIn, pos, iblockstate, null) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                
                EnumFacing cartDir = EnumFacing.getFacingFromVector((float)player.posX - pos.getX(), 0f, (float)player.posZ - pos.getZ());
                
                if(player.isSneaking()) {
                	cartDir = cartDir.getOpposite();
                }
                
                cartDir = RailDirectionHelper.matchToRail(blockrailbase$enumraildirection, cartDir);
                
                double d0 = 0.0D;

                if (blockrailbase$enumraildirection.isAscending())
                {
                    d0 = 0.5D;
                }

                EntityScabystMinecart entityminecart = EntityScabystMinecart.create(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.0625D + d0, (double)pos.getZ() + 0.5D, this.minecartType, cartDir);

                if (itemstack.hasDisplayName())
                {
                    entityminecart.setCustomNameTag(itemstack.getDisplayName());
                }

                worldIn.spawnEntity(entityminecart);
            }

            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
        }
    }

	@Override
	public void registerModels() {
		BetweenlandsRedstone.proxy.registerItemRenderer(this, 0, "inventory");
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(stack.getItem() == this) {
			if(this.minecartType == EntityScabystMinecart.Type.DUAL_FURNACE) {
				tooltip.add("Will automatically face towards you.");
				tooltip.add("Sneak while placing to place facing away from you.");
			}
//			if(this.minecartType == Type.FURNACE) {
			//	tooltip.add("WARNING: Buggy, don't complain to me about issues when colliding with other minecarts.");
			//	tooltip.add("Especially with vanilla minecarts.");
			//}
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
}
