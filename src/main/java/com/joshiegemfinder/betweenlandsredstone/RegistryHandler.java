package com.joshiegemfinder.betweenlandsredstone;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.joshiegemfinder.betweenlandsredstone.blocks.BlockBLTarget;
import com.joshiegemfinder.betweenlandsredstone.util.RecipeHolder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.common.entity.EntityBLItemFrame;
import thebetweenlands.common.registries.ItemRegistry;

@EventBusSubscriber
@SuppressWarnings("deprecation")
public class RegistryHandler {

    public static final List<RecipeHolder> recipes = new ArrayList<RecipeHolder>();
    
    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
    	Logger logger = Main.logger;
    	logger.info("Registering Recipes!");
    	for(RecipeHolder holder : recipes) {
        	//logger.info("Registering recipe \"{}\", with result \"{}\"!", holder.getName(), holder.getResult());
    		event.getRegistry().register(holder.parse());
    	}
    }

    @SubscribeEvent
    public static void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
    	Main.logger.info("Registering Sounds!");
    	event.getRegistry().registerAll(ModSounds.SOUNDS.toArray(new SoundEvent[0]));
    }
    
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
    	Main.logger.info("Registering Items!");
    	event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
    }
    
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
    	Main.logger.info("Registering Blocks!");
    	event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityEntry> event) {
    	Main.logger.info("Registering Entities!");
    	event.getRegistry().registerAll(ModEntities.ENTITIES.toArray(new EntityEntry[0]));
    }
	
	@SubscribeEvent
	public static void onProjectileImpact(ProjectileImpactEvent event) {
		RayTraceResult result = event.getRayTraceResult();
		Entity entity = event.getEntity();
		World world = entity.world;
		if(result.entityHit == null) {
            BlockPos blockpos = result.getBlockPos();
            IBlockState iblockstate = world.getBlockState(blockpos);
            if(iblockstate.getBlock() instanceof BlockBLTarget) {
            	((BlockBLTarget)iblockstate.getBlock()).onHit(world, blockpos, iblockstate, entity, result);
            }
		}
	}
	
	@SubscribeEvent
	public static void onJump(LivingJumpEvent event) {
		if(event.getEntityLiving() != null && event.getEntityLiving().world != null && event.getEntityLiving().world.isRemote) {
			EntityLivingBase entity = event.getEntityLiving();
			World world = entity.world;
			BlockPos pos = new BlockPos(entity);
			BlockPos posDown = pos.down();
			if(
					(world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() == ModBlocks.SCABYST_SLIME_2)
					||
					(world.isBlockLoaded(posDown) && world.getBlockState(posDown).getBlock() == ModBlocks.SCABYST_SLIME_2)
			) {
				//we do this the most accurate way we can
				double f = 0.42F;
				try {
					Method getJumpHeight = ReflectionHelper.findMethod(EntityLivingBase.class, "getJumpUpwardsMotion", "func_175134_bD");
					float height = (float)getJumpHeight.invoke(entity);
					//do this to prevent errors in invoke possibly corrupting `f`
					f = height;
				} catch(Exception e) {
					e.printStackTrace();
					Main.logger.info("Error getting jump height for entity {}", EntityList.getEntityString(entity));
				}
				f = f * 0.5F;

		        if (entity.isPotionActive(MobEffects.JUMP_BOOST))
		        {
		            f += (double)((float)(entity.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
		        }
//				entity.motionY = Math.min(entity.motionY, f * 0.5f);
				entity.motionY = f;
			}
		}
	}

	/*
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
    	World world = event.getWorld();
    	GameRules rules = world.getGameRules();
    	
    	if(!rules.hasRule(Reference.exclusivityGameRule)) {
    		rules.addGameRule(Reference.exclusivityGameRule, Boolean.toString(BLRedstoneConfig.scabystExclusivity), ValueType.ANY_VALUE);
    	}
    }*/
}
