package noobanidus.mods.dwmh.init;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import noobanidus.mods.dwmh.DWMH;
import noobanidus.mods.dwmh.items.OcarinaItem;

public class ItemRegistry {
  public static OcarinaItem OCARINA = (OcarinaItem) new OcarinaItem().setRegistryName(new ResourceLocation(DWMH.MODID, "ocarina"));

  public static void onItemRegister(RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();
    registry.register(OCARINA);
  }
}
