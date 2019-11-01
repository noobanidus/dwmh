package noobanidus.mods.dwmh.setup;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.dwmh.network.Networking;

public class ModSetup {
	public void init(FMLCommonSetupEvent event) {
    Networking.registerMessages();
	}
}
