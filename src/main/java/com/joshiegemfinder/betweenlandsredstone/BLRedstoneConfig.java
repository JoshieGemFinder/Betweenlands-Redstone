package com.joshiegemfinder.betweenlandsredstone;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

@Config(modid = BetweenlandsRedstone.MODID, category = "")
public class BLRedstoneConfig {
	
	@Name("general")
	@LangKey("config.betweenlandsredstone.general")
	public static final General GENERAL = new General();

	public static class General {
		@RequiresWorldRestart
		@Comment({
			"Whether or not slime blocks are sticky and will attach to other blocks when being pushed by a mud brick piston.",
			"(If false they will be treated like normal blocks, if true they act normally)"
		})
		@LangKey("config.betweenlandsredstone.slimeBlocksOnPistons")
		public boolean slimeBlocksWorkOnScabystPistons = true;

		@RequiresMcRestart
		@Comment({
			"Disables the scabyst crushing recipe for modpack creators",
			"Requires a game restart to take effect"
		})
		@LangKey("config.betweenlandsredstone.disableMortarRecipe")
		public boolean disableMortarRecipe = false;
	}

	@RequiresMcRestart
	@Name("dispenser_behaviours")
	@LangKey("config.betweenlandsredstone.dispenser_behaviours")
	public static final DispenserBehaviours DISPENSER_BEHAVIOURS = new DispenserBehaviours();

	public static class DispenserBehaviours {
		@Name("octine_ingot")
		@LangKey("item.thebetweenlands.octine_ingot.name")
		public boolean octineIngot = true;
		
		@Name("compost")
		@LangKey("item.thebetweenlands.compost.name")
		public boolean compost = true;
		
		@Name("buckets")
		@LangKey("config.betweenlandsredstone.dispenser.buckets")
		public boolean buckets = true;
		
		@Name("pestle")
		@LangKey("item.thebetweenlands.pestle.name")
		public boolean pestle = true;
		
		@Name("dentrothyst_vial")
		@LangKey("config.betweenlandsredstone.dispenser.vials")
		public boolean vials = true;
		
		@Name("infusion_buckets")
		@LangKey("config.betweenlandsredstone.dispenser.infusion_buckets")
		public boolean infusionBuckets = true;
		
		@Name("shears")
		@LangKey("item.thebetweenlands.syrmorite_shears.name")
		public boolean shears = true;
		
		@Name("plant_tonic")
		@LangKey("item.thebetweenlands.bl_bucket_plant_tonic_syrmorite.name")
		public boolean plantTonic = true;
		
		@Name("aspectrus_seeds")
		@LangKey("item.thebetweenlands.aspectrus_seeds.name")
		public boolean aspectrusSeeds = true;
		
		@Name("silk_bundle")
		@LangKey("item.thebetweenlands.silk_bundle.name")
		public boolean silkBundle = true;
	}
	

	
	@Name("extras")
	@LangKey("config.betweenlandsredstone.extras")
	public static final ExtraFeatures EXTRA_FEATURES = new ExtraFeatures();

	public static class ExtraFeatures {
		@Name("disable_silently")
		@LangKey("config.betweenlandsredstone.extras.disable_silently")
		@Comment({
			"Whether or not to ignore the missing item/block notifications when a world is loaded with a disabled block"
		})
		public boolean disableSilently = false;
		
		@Name("dispenser_hiding")
		@LangKey("config.betweenlandsredstone.extras.dispenser_hiding")
		@Comment({
			"Whether or not the face of dispensers and droppers will vanish when surrounded by cut pitstone"
		})
		public boolean dispenserHiding = true;

		@RequiresMcRestart
		@Name("register_syrmorite_bars")
		@LangKey("config.betweenlandsredstone.extras.register_syrmorite_bars")
		@Comment({
			"Whether or not syrmorite bars are enabled",
			"Disabling this may cause warnings in game logs"
		})
		public boolean registerSyrmoriteBars = true;

		@Name("syrmorite_bars_allow_projectiles")
		@LangKey("config.betweenlandsredstone.extras.syrmorite_bars_allow_projectiles")
		@Comment({
			"Whether or not syrmorite bars will allow projectiles to pass through them"
		})
		public boolean syrmoriteBarsAllowProjectiles = true;
		

		@RequiresMcRestart
		@Name("register_white_pear_block")
		@LangKey("config.betweenlandsredstone.extras.register_white_pear_block")
		@Comment({
			"Whether or not the white pear block will be registered",
			"Disabling this may cause warnings in game logs"
		})
		public boolean registerWhitePearBlock = true;
	}
}
