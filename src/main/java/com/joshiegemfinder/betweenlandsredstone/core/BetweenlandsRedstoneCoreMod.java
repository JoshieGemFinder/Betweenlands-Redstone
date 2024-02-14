package com.joshiegemfinder.betweenlandsredstone.core;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("Betweenlands Redstone Transformer")
public class BetweenlandsRedstoneCoreMod implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
//		return null; //coremod is currently disabled, as I found a non-coremod solution
//		return new String[]{"com.joshiegemfinder.betweenlandsredstone.core.transformer.TransformerBlockRedstoneWire"};
		return new String[]{"com.joshiegemfinder.betweenlandsredstone.core.transformer.TransformerTileEntityHopper"};
	}

	@Override
	public String getModContainerClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetupClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
