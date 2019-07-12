package com.noobanidus.dwmh.init;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.items.ItemEnchantedCarrot;
import com.noobanidus.dwmh.items.ItemOcarina;
import com.noobanidus.dwmh.items.ItemPipes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = DWMH.MODID)
public class ItemRegistry {
  public static ItemOcarina OCARINA = (ItemOcarina) new ItemOcarina().setTranslationKey("dwmh.ocarina").setRegistryName(new ResourceLocation(DWMH.MODID, "ocarina")).setCreativeTab(DWMH.TAB);
  public static ItemEnchantedCarrot CARROT = (ItemEnchantedCarrot) new ItemEnchantedCarrot().setTranslationKey("dwmh.carrot").setRegistryName(new ResourceLocation(DWMH.MODID, "carrot")).setCreativeTab(DWMH.TAB);
  public static ItemPipes PIPES = (ItemPipes) new ItemPipes().setTranslationKey("dwmh.pipes").setRegistryName(new ResourceLocation(DWMH.MODID, "pipes")).setCreativeTab(DWMH.TAB);
  public static Item REED = new Item().setTranslationKey("dwmh.reed").setRegistryName(new ResourceLocation(DWMH.MODID, "reed")).setCreativeTab(DWMH.TAB);

  @SubscribeEvent
  public static void onItemRegister(RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();
    registry.registerAll(OCARINA, CARROT, PIPES, REED);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onModelRegister(ModelRegistryEvent event) {
    ModelLoader.setCustomModelResourceLocation(OCARINA, 0, new ModelResourceLocation(Objects.requireNonNull(OCARINA.getRegistryName()), "inventory"));
    ModelLoader.setCustomModelResourceLocation(CARROT, 0, new ModelResourceLocation(Objects.requireNonNull(CARROT.getRegistryName()), "inventory"));
    ModelLoader.setCustomModelResourceLocation(PIPES, 0, new ModelResourceLocation(Objects.requireNonNull(PIPES.getRegistryName()), "inventory"));
    ModelLoader.setCustomModelResourceLocation(REED, 0, new ModelResourceLocation(Objects.requireNonNull(REED.getRegistryName()), "inventory"));
  }
}
