package com.joshiegemfinder.betweenlandsredstone;

import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityChestBetweenlandsTrapped;
import com.joshiegemfinder.betweenlandsredstone.blocks.dispenser.TileEntityScabystDispenser;
import com.joshiegemfinder.betweenlandsredstone.blocks.dispenser.TileEntityScabystDropper;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.TileEntityScabystPiston;
import com.joshiegemfinder.betweenlandsredstone.network.PlantTonicMessage;
import com.joshiegemfinder.betweenlandsredstone.proxy.IProxy;
import com.joshiegemfinder.betweenlandsredstone.util.CropHelper;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.common.BetweenlandsAPI;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.block.farming.BlockMiddleFruitBush;
import thebetweenlands.common.entity.projectiles.EntityAngryPebble;
import thebetweenlands.common.entity.projectiles.EntityPyradFlame;
import thebetweenlands.common.handler.OverworldItemHandler;
import thebetweenlands.common.item.herblore.ItemDentrothystVial;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.tools.ItemBLBucket;
import thebetweenlands.common.item.tools.ItemBucketInfusion;
import thebetweenlands.common.item.tools.ItemPestle;
import thebetweenlands.common.recipe.mortar.PestleAndMortarRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityAlembic;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.common.tile.TileEntityMortar;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION, updateJSON = "https://raw.githubusercontent.com/JoshieGemFinder/Betweenlands-Redstone/main/update.json", useMetadata = true)
public class Main
{
	public static final String MODID = "betweenlandsredstone";
	public static final String NAME = "Betweenlands Redstone";
	public static final String VERSION = "1.2.0";

//	public static final BehaviorProjectileDispense BLArrowBehaviour = new BehaviorProjectileDispense()
//	{
//		protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn)
//		{
//			EntityBLArrow entity = new EntityBLArrow(worldIn);
//			Item item = stackIn.getItem();
//			if(item instanceof ItemBLArrow) {
//				entity.setType(((ItemBLArrow)item).getType());
//			} else {
//				entity.setType(EnumArrowType.DEFAULT);
//			}
//			entity.setPosition(position.getX(), position.getY(), position.getZ());
//			entity.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
//			return entity;
//		}
//	};
	
	public static Logger logger;

	@SidedProxy(clientSide = "com.joshiegemfinder.betweenlandsredstone.proxy.ClientProxy", serverSide = "com.joshiegemfinder.betweenlandsredstone.proxy.CommonProxy")
	public static IProxy proxy;
	
	public static Main instance = null;
	
	public Main() {
		Main.instance = this;
	}

	protected static final Random RANDOM = new Random();
	
	public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	
	private static final GameProfile FAKE_PROFILE = new GameProfile(UUID.randomUUID(), "[BetweenlandsRedstone_Sock]");
	private static FakePlayer fakePlayer;
	
	public static FakePlayer getFakePlayer(WorldServer world) {
		if (fakePlayer == null) fakePlayer = new FakePlayer(world, FAKE_PROFILE);
		return fakePlayer;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(BLRedstoneDataFixers.class);
		
		ModBlocks.init();
		ModItems.init();
		ModEntities.init();

		GameRegistry.registerTileEntity(TileEntityScabystPiston.class, new ResourceLocation(MODID, "scabyst_piston_tileentity"));
		GameRegistry.registerTileEntity(TileEntityScabystDispenser.class, new ResourceLocation(MODID, "scabyst_dispenser_tileentity"));
		GameRegistry.registerTileEntity(TileEntityScabystDropper.class, new ResourceLocation(MODID, "scabyst_dropper_tileentity"));
		//named weedwood_chest_trapped instead of weedwood_chest_trapped_tileentity because item renderer
		GameRegistry.registerTileEntity(TileEntityChestBetweenlandsTrapped.class, new ResourceLocation(MODID, "weedwood_chest_trapped"));
		
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
		TileEntityScabystDispenser.registerFixesScabyst(fixer);
		TileEntityScabystDropper.registerFixesDropperScabyst(fixer);
		
		BLRedstoneDataFixers.registerDataFixers(fixer);
		
        ConfigManager.sync(MODID, Type.INSTANCE);
        
		BetweenlandsAPI.getInstance().registerPestleAndMortarRecipe(
				new PestleAndMortarRecipe(
						new ItemStack(ModItems.SCABYST_DUST, 8),
						new ItemStack(EnumItemMisc.SCABYST.getItem(), 1, ItemMisc.EnumItemMisc.SCABYST.getID())
				)
		);
		OverworldItemHandler.TORCH_WHITELIST.put(new ResourceLocation(MODID, "scabyst_torch_whitelist"), stack -> stack.getItem() == ModItems.SCABYST_TORCH);
		
		OverworldItemHandler.ROTTING_WHITELIST.put(new ResourceLocation(MODID, "white_pear_block_whitelist"), stack -> stack.getItem() == ModItems.WHITE_PEAR_BLOCK);
		
		
		registerDispenserBehaviours();
		
		proxy.init();
	}
	
	private static void registerDispenserBehaviours() {

		// I got it included in the betweenlands
//		// I cannot believe this is something that isn't already a part of the betweenlands
//		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.ANGLER_TOOTH_ARROW, BLArrowBehaviour);
//		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.POISONED_ANGLER_TOOTH_ARROW, BLArrowBehaviour);
//		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.OCTINE_ARROW, BLArrowBehaviour);
//		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.BASILISK_ARROW, BLArrowBehaviour);
//		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.SLUDGE_WORM_ARROW, BLArrowBehaviour);
//		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.SHOCK_ARROW, BLArrowBehaviour);
//		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.CHIROMAW_BARB, BLArrowBehaviour);
		
		final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(EnumItemMisc.COMPOST.getItem(),  new BehaviorDefaultDispenseItem()
		{
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				if(stack.getItem() == EnumItemMisc.COMPOST.getItem() && stack.getItemDamage() == EnumItemMisc.COMPOST.getID()) {
					EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					World world = source.getWorld();
					BlockPos compostPos = source.getBlockPos().add(enumfacing.getDirectionVec());
					IBlockState state = world.getBlockState(compostPos);
					if(state.getBlock() instanceof BlockGenericDugSoil) {
						TileEntityDugSoil soil = BlockGenericDugSoil.getTile(world, compostPos);
						if(!soil.isComposted()) {
							soil.setCompost(30);
							stack.shrink(1);
							return stack;
						}
					}
				}
				return behaviourDefaultDispenseItem.dispense(source, stack);
			}
		});
		
		IBehaviorDispenseItem bucketFallback = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(ItemRegistry.BL_BUCKET);
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.BL_BUCKET, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				ItemBLBucket bucket = (ItemBLBucket) stack.getItem();
				FluidStack fluid = bucket.getFluid(stack);
				if(fluid != null && fluid.amount > 0) {
					EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					World world = source.getWorld();
					BlockPos fillPos = source.getBlockPos().add(enumfacing.getDirectionVec());
					TileEntity tile = world.getTileEntity(fillPos);
					if(tile instanceof IFluidHandler) {
						int used = ((IFluidHandler) tile).fill(fluid, true);
						if(used > 0) {
							ItemStack empty = bucket.getEmpty(stack);
							return new ItemStack(empty.getItem(), stack.getCount(), empty.getItemDamage());
						}
					}
				}
				return bucketFallback.dispense(source, stack);
			}
		});
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.PESTLE, new BehaviorDefaultDispenseItem() {
			 public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			 {
				 if(stack.getItem() instanceof ItemPestle) {
					 EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					 World world = source.getWorld();
					 BlockPos mortarPos = source.getBlockPos().add(enumfacing.getDirectionVec());
					 if(world.getTileEntity(mortarPos) instanceof TileEntityMortar) {
					 	TileEntityMortar mortar = (TileEntityMortar)world.getTileEntity(mortarPos);
					 	mortar.func_70299_a(1, stack);
					 	return ItemStack.EMPTY;
					 }
				 }
				 return behaviourDefaultDispenseItem.dispense(source, stack);
			 }
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.DENTROTHYST_VIAL, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				if(stack.getItem() instanceof ItemDentrothystVial && (stack.getItemDamage() == 0 || stack.getItemDamage() == 2)) {
					EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					World world = source.getWorld();
				 	BlockPos alembicPos = source.getBlockPos().add(enumfacing.getDirectionVec());
				 	if(world.getTileEntity(alembicPos) instanceof TileEntityAlembic) {
				 		TileEntityAlembic alembic = (TileEntityAlembic)world.getTileEntity(alembicPos);
				 		if(alembic.hasFinished()) {
				 			ItemStack result = alembic.getElixir(stack.getItemDamage() == 0 ? 0 : 1);
				 			
				 			stack.shrink(1);
				 			

			                if (stack.isEmpty())
			                {
			                    return result;
			                }
			                else
			                {
			                    if (((TileEntityDispenser)source.getBlockTileEntity()).addItemStack(result) < 0)
			                    {
			                        behaviourDefaultDispenseItem.dispense(source, result);
			                    }

			                    return stack;
			                }
				 		}
				 	}
				}
				return behaviourDefaultDispenseItem.dispense(source, stack);
			}
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.BL_BUCKET_INFUSION, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				if(stack.getItem() instanceof ItemBucketInfusion) {
					EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					World world = source.getWorld();
				 	BlockPos alembicPos = source.getBlockPos().add(enumfacing.getDirectionVec());
				 	if(world.getTileEntity(alembicPos) instanceof TileEntityAlembic) {
				 		TileEntityAlembic alembic = (TileEntityAlembic)world.getTileEntity(alembicPos);
				 		if(!alembic.isFull()) {
				 			alembic.addInfusion(stack);
				 			return ItemBucketInfusion.getEmptyBucket(stack);
				 		}
				 	}
				}
				return behaviourDefaultDispenseItem.dispense(source, stack);
			}
		});
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.ANGRY_PEBBLE, new BehaviorProjectileDispense() {
			@Override
			protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
				worldIn.playSound(null, position.getX(), position.getY(), position.getZ(), SoundRegistry.SORRY, SoundCategory.PLAYERS, 0.7F, 0.8F);
				EntityAngryPebble entity = new EntityAngryPebble(worldIn);
				entity.setPosition(position.getX(), position.getY(), position.getZ());
				return entity;
			}
			
		});
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.PYRAD_FLAME, new BehaviorDefaultDispenseItem() {
		    public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
		    {
		        World world = source.getWorld();
		        Random rand = Main.RANDOM;
		        IPosition pos = BlockDispenser.getDispensePosition(source);
		        EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
		        Vec3d look = new Vec3d(enumfacing.getDirectionVec());

		        world.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
		        
				for(int i = 0; i < rand.nextInt(6) + 1; i++) {
			        EntityPyradFlame flame = this.getProjectileEntity(world, pos, look);
			        world.spawnEntity(flame);
				}
		        
				playDispenseSound(source);
				
		        stack.shrink(1);
		        return stack;
		    }

		    protected EntityPyradFlame getProjectileEntity(World worldIn, IPosition position, Vec3d dir) {
		        Random rand = Main.RANDOM;
				float f = 0.05F;
		    	EntityPyradFlame flame = new EntityPyradFlame(worldIn, position.getX(), position.getY(), position.getZ(), dir.x + rand.nextGaussian() * (double)f + rand.nextGaussian() * 0.4D, dir.y + rand.nextGaussian() * 0.4D, dir.z + rand.nextGaussian() * (double)f + rand.nextGaussian() * 0.4D);
		    	return flame;
		    }

		    protected void playDispenseSound(IBlockSource source)
		    {
		        source.getWorld().playEvent(1002, source.getBlockPos(), 0);
		    }
		});
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.SYRMORITE_SHEARS, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				World world = source.getWorld();
				EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
				BlockPos harvestPos = source.getBlockPos().add(enumfacing.getDirectionVec());
				IBlockState state = world.getBlockState(harvestPos);
				if(state.getBlock() instanceof BlockGenericCrop) {
					BlockGenericCrop crop = (BlockGenericCrop)world.getBlockState(harvestPos).getBlock();
					if(state.getValue(BlockGenericCrop.AGE) >= 15) {
						stack.damageItem(1, Main.getFakePlayer((WorldServer) world));
						int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
						if(crop instanceof BlockMiddleFruitBush) {
							CropHelper.harvestCrop(crop, world, harvestPos, state, fortune, false);
							world.setBlockState(harvestPos, state.withProperty(BlockGenericCrop.AGE, 8));
						} else {
							CropHelper.harvestCrop(crop, world, harvestPos, state, fortune, true);
						}
						CropHelper.harvestAndUpdateSoil(world, harvestPos, 10);
					}
					return stack;
				}
				return behaviourDefaultDispenseItem.dispense(source, stack);
			}
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.BL_BUCKET_PLANT_TONIC,  new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
				World world = source.getWorld();
				BlockPos tonicPos = source.getBlockPos().add(enumfacing.getDirectionVec());
				IBlockState state = world.getBlockState(tonicPos);

				if(state.getBlock() instanceof IPlantable || state.getBlock() instanceof IFarmablePlant) {
					while(world.getBlockState(tonicPos).getBlock() instanceof IPlantable || world.getBlockState(tonicPos).getBlock() instanceof IFarmablePlant) {
						tonicPos = tonicPos.down();
					}
				} else if(!(state.getBlock() instanceof BlockGenericDugSoil) && world.getBlockState(tonicPos.down()).getBlock() instanceof BlockGenericDugSoil) {
					return stack;
				}

				state = world.getBlockState(tonicPos);
				
				if(state.getBlock() instanceof BlockGenericDugSoil && BlockGenericDugSoil.getTile(world, tonicPos) != null) {
					NETWORK_CHANNEL.sendToAllTracking(new PlantTonicMessage(tonicPos, false), new TargetPoint(world.provider.getDimension(), tonicPos.getX(), tonicPos.getY(), tonicPos.getZ(), 4));
					
					boolean cured = false;

					for(int xo = -2; xo <= 2; xo++) {
						for(int yo = -2; yo <= 2; yo++) {
							for(int zo = -2; zo <= 2; zo++) {
								BlockPos offsetPos = tonicPos.add(xo, yo, zo);
								TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, offsetPos);
								if(te != null && te.getDecay() > 0) {
									cured = true;
									te.setDecay(0);
								}
							}
						}
					}

					if(cured) {
						if(!world.isRemote) {
							CropHelper.PlantTonicHelper.setUsages(stack, CropHelper.PlantTonicHelper.getUsages(stack) + 1);
							if(CropHelper.PlantTonicHelper.getUsages(stack) >= 3) {
								return ItemRegistry.BL_BUCKET.getEmpty(new ItemStack(ItemRegistry.BL_BUCKET, 1, stack.getMetadata()));
							}
						}

						world.playSound(null, tonicPos.getX() + 0.5F, tonicPos.getY() + 0.5F, tonicPos.getZ() + 0.5F, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1, 1);
					}
				} else {
					return behaviourDefaultDispenseItem.dispense(source, stack);
				}
				return stack;
			}
		});
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.ASPECTRUS_SEEDS, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
				World world = source.getWorld();
			 	BlockPos dirtPos = source.getBlockPos().offset(enumfacing);
			 	BlockPos fencePos = dirtPos.up();
			 	IBlockState dirt = world.getBlockState(dirtPos);
			 	IBlockState fence = world.getBlockState(fencePos);
			 	if(dirt.getBlock() instanceof BlockGenericDugSoil && fence.getBlock() == BlockRegistry.RUBBER_TREE_PLANK_FENCE) {
					if(!dirt.getValue(BlockGenericDugSoil.COMPOSTED)) {
						return stack;
					}
			 		for(EnumFacing dir : EnumFacing.HORIZONTALS) {
						if(BlockRegistry.RUBBER_TREE_PLANK_FENCE.canBeConnectedTo(world, fencePos, dir)) {
							return stack;
						}
					}
			 		world.setBlockState(fencePos, BlockRegistry.ASPECTRUS_CROP.getDefaultState());
			 		stack.shrink(1);
			 		return stack;
			 	}
				return behaviourDefaultDispenseItem.dispense(source, stack);
			}
		});
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}
	
    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID))
        {
            ConfigManager.sync(MODID, Type.INSTANCE);
        }
    }
}
