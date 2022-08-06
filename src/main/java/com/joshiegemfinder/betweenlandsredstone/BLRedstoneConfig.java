package com.joshiegemfinder.betweenlandsredstone;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

@Config(modid = Main.MODID)
public class BLRedstoneConfig {

	@RequiresWorldRestart
	@Comment({
		"Whether or not scabyst and vanilla redstone components interact.",
		"(true = no vanilla interaction, false = vanilla interaction)",
		"(if on, this may cause some bugs when interacting with other mods)"
	})
	@LangKey("config.scabystExclusivity.name")
	public static boolean scabystExclusivity = false;
	
	//Reminder: make a way to put in a list of blocks that scabyst can interact with if exclusivity is on
}
