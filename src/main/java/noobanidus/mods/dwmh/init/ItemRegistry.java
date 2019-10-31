package noobanidus.mods.dwmh.init;

import noobanidus.mods.dwmh.DWMH;
import noobanidus.mods.dwmh.items.OcarinaItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = DWMH.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegistry {
  public static OcarinaItem OCARINA = (OcarinaItem) new OcarinaItem().setRegistryName(new ResourceLocation(DWMH.MODID, "ocarina"));

  @SubscribeEvent
  public static void onItemRegister(RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();
    registry.register(OCARINA);
  }

  /*@SubscribeEvent
  @OnlyIn(Dist.CLIENT)
  public static void onModelRegister(ModelRegistryEvent event) {
    ModelLoader.setCustomModelResourceLocation(OCARINA, 0, new ModelResourceLocation(Objects.requireNonNull(OCARINA.getRegistryName()), "inventory"));
  }*/
}
