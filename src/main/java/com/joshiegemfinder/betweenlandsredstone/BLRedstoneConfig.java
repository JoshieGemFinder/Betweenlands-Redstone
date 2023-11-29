package com.joshiegemfinder.betweenlandsredstone;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

@Config(modid = Main.MODID)
public class BLRedstoneConfig {

	@RequiresWorldRestart
	@Comment({
		"Whether or not slime blocks are sticky and will attach to other blocks when being pushed by a mud brick piston.",
		"(If false they will be treated like normal blocks, if true they act normally)"
	})
	@LangKey("config.slimeBlocksOnPistons.name")
	public static boolean slimeBlocksWorkOnScabystPistons = true;
}
