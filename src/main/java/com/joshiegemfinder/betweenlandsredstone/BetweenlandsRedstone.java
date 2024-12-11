package com.joshiegemfinder.betweenlandsredstone;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityChestBetweenlandsTrapped;
import com.joshiegemfinder.betweenlandsredstone.blocks.TileEntityCrafter;
import com.joshiegemfinder.betweenlandsredstone.blocks.dispenser.TileEntityScabystDispenser;
import com.joshiegemfinder.betweenlandsredstone.blocks.dispenser.TileEntityScabystDropper;
import com.joshiegemfinder.betweenlandsredstone.blocks.piston.TileEntityScabystPiston;
import com.joshiegemfinder.betweenlandsredstone.datafixers.DataFixerConfigDisabledBlocks;
import com.joshiegemfinder.betweenlandsredstone.datafixers.DataFixerItemFrame;
import com.joshiegemfinder.betweenlandsredstone.gui.BetweenlandsRedstoneGuiHandler;
import com.joshiegemfinder.betweenlandsredstone.network.MinecartFacingMessage;
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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap.BehaviorDispenseOptional;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.common.BetweenlandsAPI;
import thebetweenlands.common.block.container.BlockSteepingPot;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.block.farming.BlockMiddleFruitBush;
import thebetweenlands.common.entity.projectiles.EntityAngryPebble;
import thebetweenlands.common.entity.projectiles.EntityPyradFlame;
import thebetweenlands.common.entity.projectiles.EntitySilkyPebble;
import thebetweenlands.common.handler.OverworldItemHandler;
import thebetweenlands.common.inventory.InventorySilkBundle;
import thebetweenlands.common.inventory.container.ContainerSilkBundle;
import thebetweenlands.common.item.herblore.ItemDentrothystVial;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.misc.ItemOctineIngot;
import thebetweenlands.common.item.tools.ItemBucketInfusion;
import thebetweenlands.common.item.tools.ItemPestle;
import thebetweenlands.common.recipe.mortar.PestleAndMortarRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityAlembic;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.common.tile.TileEntityMortar;
import thebetweenlands.common.tile.TileEntitySteepingPot;

@Mod(modid = BetweenlandsRedstone.MODID, name = BetweenlandsRedstone.NAME, version = BetweenlandsRedstone.VERSION, updateJSON = "https://raw.githubusercontent.com/JoshieGemFinder/Betweenlands-Redstone/main/update.json", useMetadata = true)
public class BetweenlandsRedstone
{
	public static final String MODID = "betweenlandsredstone";
	public static final String NAME = "Betweenlands Redstone";
	public static final String VERSION = "1.4.0";
	
	
	
	public static Logger logger;

	@SidedProxy(clientSide = "com.joshiegemfinder.betweenlandsredstone.proxy.ClientProxy", serverSide = "com.joshiegemfinder.betweenlandsredstone.proxy.CommonProxy")
	public static IProxy proxy;
	
	public static BetweenlandsRedstone instance = null;
	
	public BetweenlandsRedstone() {
		BetweenlandsRedstone.instance = this;
	}

	protected static final Random RANDOM = new Random();
	
	public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	
	private static final GameProfile FAKE_PROFILE = new GameProfile(UUID.randomUUID(), "[BetweenlandsRedstone_SockPlayer]");
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
		MinecraftForge.EVENT_BUS.register(DataFixerItemFrame.class);
		MinecraftForge.EVENT_BUS.register(DataFixerConfigDisabledBlocks.class);
		
		ModBlocks.init();
		ModItems.init();
		ModEntities.init();

		GameRegistry.registerTileEntity(TileEntityScabystPiston.class, new ResourceLocation(MODID, "scabyst_piston_tileentity"));
		GameRegistry.registerTileEntity(TileEntityScabystDispenser.class, new ResourceLocation(MODID, "scabyst_dispenser_tileentity"));
		GameRegistry.registerTileEntity(TileEntityScabystDropper.class, new ResourceLocation(MODID, "scabyst_dropper_tileentity"));
		//named weedwood_chest_trapped instead of weedwood_chest_trapped_tileentity because item renderer
		GameRegistry.registerTileEntity(TileEntityChestBetweenlandsTrapped.class, new ResourceLocation(MODID, "weedwood_chest_trapped"));
		if(BLRedstoneConfig.EXTRA_FEATURES.registerCrafter)
			GameRegistry.registerTileEntity(TileEntityCrafter.class, new ResourceLocation(MODID, "crafter"));
		
		proxy.preInit();
		
//		if(Loader.isModLoaded("gamestages")) {
//			new GamestagesCompatibility(this);
//		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{

		BetweenlandsRedstone.NETWORK_CHANNEL.registerMessage(MinecartFacingMessage.MinecartFacingMessageHandler.class, MinecartFacingMessage.class, 0, Side.CLIENT);
		BetweenlandsRedstone.NETWORK_CHANNEL.registerMessage(PlantTonicMessage.PlantTonicMessageHandler.class, PlantTonicMessage.class, 1, Side.CLIENT);
		
		CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
		TileEntityScabystDispenser.registerFixesScabyst(fixer);
		TileEntityScabystDropper.registerFixesDropperScabyst(fixer);
		
		DataFixerItemFrame.registerDataFixers(fixer);
		
        ConfigManager.sync(MODID, Type.INSTANCE);
        
        if(!BLRedstoneConfig.GENERAL.disableMortarRecipe) {
			BetweenlandsAPI.getInstance().registerPestleAndMortarRecipe(
					new PestleAndMortarRecipe(
							new ItemStack(ModItems.SCABYST_DUST, 8),
							new ItemStack(EnumItemMisc.SCABYST.getItem(), 1, ItemMisc.EnumItemMisc.SCABYST.getID())
					)
			);
        }
		OverworldItemHandler.TORCH_WHITELIST.put(new ResourceLocation(MODID, "scabyst_torch_whitelist"), stack -> stack.getItem() == ModItems.SCABYST_TORCH);
		
		OverworldItemHandler.ROTTING_WHITELIST.put(new ResourceLocation(MODID, "white_pear_block_whitelist"), stack -> BLRedstoneConfig.EXTRA_FEATURES.registerWhitePearBlock && (stack.getItem() == ModItems.WHITE_PEAR_BLOCK));
		
		BetweenlandsRedstoneGuiHandler guihandler = new BetweenlandsRedstoneGuiHandler();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, guihandler);
//		guihandler.re
		
		registerDispenserBehaviours();
		
		proxy.init();
	}
	
	private static void registerDispenserBehaviours() {

		// ============ stuff that gets shot ============
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.ANGRY_PEBBLE, new BehaviorProjectileDispense() {
			@Override
			protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
				worldIn.playSound(null, position.getX(), position.getY(), position.getZ(), SoundRegistry.SORRY, SoundCategory.PLAYERS, 0.7F, 0.8F);
				EntityAngryPebble entity = new EntityAngryPebble(worldIn);
				entity.setPosition(position.getX(), position.getY(), position.getZ());
				return entity;
			}
		});
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.SILKY_PEBBLE, new BehaviorProjectileDispense() {
			@Override
			protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
				worldIn.playSound(null, position.getX(), position.getY(), position.getZ(), SoundRegistry.SILKY_PEBBLE_THROW, SoundCategory.PLAYERS, 0.7F, 0.8F);
				EntitySilkyPebble entity = new EntitySilkyPebble(worldIn);
				entity.setPosition(position.getX(), position.getY(), position.getZ());
				return entity;
			}
		});
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.PYRAD_FLAME, new BehaviorDefaultDispenseItem() {
		    public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
		    {
		        World world = source.getWorld();
		        Random rand = BetweenlandsRedstone.RANDOM;
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
		        Random rand = BetweenlandsRedstone.RANDOM;
				float f = 0.05F;
		    	EntityPyradFlame flame = new EntityPyradFlame(worldIn, position.getX(), position.getY(), position.getZ(), dir.x + rand.nextGaussian() * (double)f + rand.nextGaussian() * 0.4D, dir.y + rand.nextGaussian() * 0.4D, dir.z + rand.nextGaussian() * (double)f + rand.nextGaussian() * 0.4D);
		    	return flame;
		    }

		    protected void playDispenseSound(IBlockSource source)
		    {
		        source.getWorld().playEvent(1002, source.getBlockPos(), 0);
		    }
		});
		

		// ============ everything else ============
		
		final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();

		// ============ octine ingot ============
		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.octineIngot)
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.OCTINE_INGOT, new BehaviorDispenseOptional()
		{
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				if(stack.getItem() instanceof ItemOctineIngot) { // because casting
					World world = source.getWorld();
					BlockPos tinderPos = source.getBlockPos().offset(source.getBlockState().getValue(BlockDispenser.FACING));
					IBlockState tinderBlock = world.getBlockState(tinderPos);
					ItemOctineIngot ingot = (ItemOctineIngot)stack.getItem();
					boolean hasTinder = false;
					boolean isBlockTinder = false;
					if(ingot.isTinder(stack, ItemStack.EMPTY, tinderBlock)) {
						hasTinder = true;
						isBlockTinder = true;
					} else { // no need to check items if the block is already confirmed tinder
						List<EntityItem> tinder = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(tinderPos), entity -> !entity.getItem().isEmpty() && ingot.isTinder(stack, entity.getItem(), null));
						if(!tinder.isEmpty()) {
							hasTinder = true;
						}
					}
					
					if(hasTinder && (isBlockTinder || tinderBlock.getMaterial().isReplaceable())) {
						world.setBlockState(tinderPos, Blocks.FIRE.getDefaultState());
						// only keeping SoundCategory PLAYERS so it's consistent with normal octine ingot lighting
						world.playSound(null, tinderPos.getX(), tinderPos.getY(), tinderPos.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
						this.successful = true;
					} else {
						this.successful = false;
					}
					return stack;
				}
				return behaviourDefaultDispenseItem.dispense(source, stack);
			}
		});
		

		// ============ buckets ============
		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.buckets) {
			IBehaviorDispenseItem bucketFallback;
			if(BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.containsKey(ItemRegistry.BL_BUCKET)) {
				bucketFallback = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(ItemRegistry.BL_BUCKET);
			} else {
				bucketFallback = net.minecraftforge.fluids.DispenseFluidContainer.getInstance();
			}
			
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.BL_BUCKET, new IBehaviorDispenseItem() {
				@Override
				public ItemStack dispense(IBlockSource source, ItemStack stack) {

					EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					World world = source.getWorld();
					BlockPos fillPos = source.getBlockPos().offset(enumfacing);
					IFluidHandler fluidHandler = FluidUtil.getFluidHandler(world, fillPos, enumfacing.getOpposite());

					if(fluidHandler != null && !(fluidHandler instanceof BlockLiquidWrapper)) {
						net.minecraftforge.fluids.FluidActionResult actionResult;
						if (FluidUtil.getFluidContained(stack) != null) {
							actionResult = FluidUtil.tryEmptyContainer(stack, fluidHandler, Fluid.BUCKET_VOLUME, null, true);
						} else {
							actionResult = FluidUtil.tryFillContainer(stack, fluidHandler, Fluid.BUCKET_VOLUME, null, true);
						}
						
						
						if(actionResult.isSuccess()) {
							ItemStack modifiedStack = actionResult.getResult();
							//so we don't play the sound multiple times if this fails
					        source.getWorld().playEvent(1000, source.getBlockPos(), 0); //playDispenseSound
					        EnumFacing facing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING); // getWorldEventDataFrom
					        source.getWorld().playEvent(2000, source.getBlockPos(), facing.getFrontOffsetX() + 1 + (facing.getFrontOffsetZ() + 1) * 3); // spawnDispenseParticles
							if(stack.getCount() == 1) {
								return modifiedStack;
							} 
							else if (!modifiedStack.isEmpty() && ((TileEntityDispenser)source.getBlockTileEntity()).addItemStack(modifiedStack) < 0)
				            {
								behaviourDefaultDispenseItem.dispense(source, modifiedStack);
				            }

				            ItemStack stackCopy = stack.copy();
				            stackCopy.shrink(1);
				            return stackCopy;
						}
			        }
			        
					return bucketFallback.dispense(source, stack);
				}
			});
		}

		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.compost)
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(EnumItemMisc.COMPOST.getItem(),  new BehaviorDefaultDispenseItem()
		{
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				if(stack.getItem() == EnumItemMisc.COMPOST.getItem() && stack.getItemDamage() == EnumItemMisc.COMPOST.getID()) {
					EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					World world = source.getWorld();
					BlockPos compostPos = source.getBlockPos().offset(enumfacing);
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
		

		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.pestle)
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.PESTLE, new BehaviorDefaultDispenseItem() {
			 public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			 {
				 if(stack.getItem() instanceof ItemPestle) {
					 EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					 World world = source.getWorld();
					 BlockPos mortarPos = source.getBlockPos().offset(enumfacing);
					 if(world.getTileEntity(mortarPos) instanceof TileEntityMortar) {
					 	TileEntityMortar mortar = (TileEntityMortar)world.getTileEntity(mortarPos);
					 	mortar.setInventorySlotContents(1, stack);
					 	return ItemStack.EMPTY;
					 }
				 }
				 return behaviourDefaultDispenseItem.dispense(source, stack);
			 }
		});

		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.vials)
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.DENTROTHYST_VIAL, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				if(stack.getItem() instanceof ItemDentrothystVial && (stack.getItemDamage() == 0 || stack.getItemDamage() == 2)) {
					EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					World world = source.getWorld();
				 	BlockPos alembicPos = source.getBlockPos().offset(enumfacing);
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

		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.infusionBuckets)
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.BL_BUCKET_INFUSION, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				if(stack.getItem() instanceof ItemBucketInfusion) {
					EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					World world = source.getWorld();
				 	BlockPos alembicPos = source.getBlockPos().offset(enumfacing);
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

		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.shears)
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.SYRMORITE_SHEARS, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				World world = source.getWorld();
				EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
				BlockPos harvestPos = source.getBlockPos().offset(enumfacing);
				IBlockState state = world.getBlockState(harvestPos);
				if(state.getBlock() instanceof BlockGenericCrop) {
					BlockGenericCrop crop = (BlockGenericCrop)world.getBlockState(harvestPos).getBlock();
					if(state.getValue(BlockGenericCrop.AGE) >= 15) {
						stack.damageItem(1, BetweenlandsRedstone.getFakePlayer((WorldServer) world));
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

		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.plantTonic)
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.BL_BUCKET_PLANT_TONIC,  new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
				World world = source.getWorld();
				BlockPos tonicPos = source.getBlockPos().offset(enumfacing);
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

		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.aspectrusSeeds)
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

		if(BLRedstoneConfig.DISPENSER_BEHAVIOURS.silkBundle)
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.SILK_BUNDLE, new BehaviorDispenseOptional() {
				public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
				{
					EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
					World world = source.getWorld();
				 	BlockPos steepingPotPos = source.getBlockPos().offset(enumfacing);
				 	IBlockState steepingPot = world.getBlockState(steepingPotPos);
				 	if(steepingPot.getBlock() instanceof BlockSteepingPot) {
				 		TileEntity te = world.getTileEntity(steepingPotPos);
				 		if(te != null && te instanceof TileEntitySteepingPot) {
				 			TileEntitySteepingPot tile = (TileEntitySteepingPot)te;
//				 			if(tile.getStackInSlot(0).isEmpty()) {
//				 				tile.setInventorySlotContents(0, stack.copy());
//				 				stack.shrink(1);
//		 						this.successful = true;
//				 				return stack;
//				 			} else {
//		 						this.successful = false;
//				 				return stack;
//				 			}
				 			
				 			if(tile.getStackInSlot(0).isEmpty()) {
				 				tile.setInventorySlotContents(0, stack.copy());
				 				stack.shrink(1);
				 				this.successful = true;
				 				return stack;
				 			} else {
				 				this.successful = false;
				 				return stack;
				 			}
				 		}
				 	}

					String name = stack.hasDisplayName() ? stack.getDisplayName() : "container.bl.silk_bundle";
				 	InventorySilkBundle bundleInventory = new InventorySilkBundle(stack, 4, name);
				 	
				 	full: {
				 		for(int i = 0; i < bundleInventory.getSizeInventory(); ++i) {
				 			ItemStack bundleStack = bundleInventory.getStackInSlot(i);
				 			if(bundleStack.getCount() < bundleInventory.getInventoryStackLimit()) {
				 				break full;
				 			}
				 		}
				 		//bag is full, return
				 		this.successful = false;
				 		return stack;
				 	}
				 	
				 	List<EntityItem> bagItems = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(steepingPotPos), entity -> {
				 		ItemStack entityStack = entity.getItem();
				 		if(entityStack.isEmpty() || thebetweenlands.util.InventoryUtils.isDisallowedInInventories(entityStack))
				 			return false;
				 		
				 		for(ItemStack template : ContainerSilkBundle.acceptedItems)
							if (template.getItem() == entityStack.getItem() && (template.getItemDamage() == entityStack.getItemDamage() || template.getItemDamage() == OreDictionary.WILDCARD_VALUE))
								return true;
				 		return false;
				 	});
				 	
				 	if(bagItems.isEmpty()) {
		 				this.successful = false;
				 		return stack;
				 	}
				 	
				 	IItemHandler handle = new InvWrapper(bundleInventory);

	 				this.successful = false;
	 				
				 	for(EntityItem entityItem : bagItems) {
				 		ItemStack bagItem = entityItem.getItem();
				 		int initialCount = bagItem.getCount();
				 		ItemStack resultStack = ItemHandlerHelper.insertItemStacked(handle, bagItem, false);
				 		if(resultStack.getCount() < initialCount) {
			 				this.successful = true;
				 		}
				 		entityItem.setItem(resultStack);
				 	}
				 	
				 	return stack;
//					return behaviourDefaultDispenseItem.dispense(source, stack);
				}
			});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemRegistry.BL_NAME_TAG, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
				World world = source.getWorld();
			 	BlockPos itemPos = source.getBlockPos().offset(enumfacing);
			 	List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(itemPos), entity -> !entity.getItem().isEmpty());
			 	
			 	for(EntityItem item : items) {
			 		item.getItem().setStackDisplayName(stack.getDisplayName());
			 	}
			 	
			 	return stack;
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
            
//
//    		if(Loader.isModLoaded("gamestages")) {
//    			GamestagesCompatibility.INSTANCE.configChanged();
//    		}
        }
    }
}
