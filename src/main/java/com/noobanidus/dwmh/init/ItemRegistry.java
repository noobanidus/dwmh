package com.noobanidus.dwmh.init;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.items.ItemEnchantedCarrot;
import com.noobanidus.dwmh.items.ItemOcarina;
import com.noobanidus.dwmh.items.ItemWhistle;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = DWMH.MODID)
public class ItemRegistry {
  public static ItemOcarina OCARINA = new ItemOcarina();
  public static ItemEnchantedCarrot CARROT = new ItemEnchantedCarrot();
  public static ItemWhistle WHISTLE = new ItemWhistle();

  @SubscribeEvent
  public static void onItemRegister(RegistryEvent.Register<Item> event) {
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onModeleRegister(ModelRegistryEvent event) {

  }
}
