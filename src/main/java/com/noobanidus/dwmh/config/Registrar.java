package com.noobanidus.dwmh.config;

import com.noobanidus.dwmh.items.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class Registrar {
    public static ItemOcarina ocarina = null;
    public static ItemEnchantedCarrot carrot = null;

    public static void preInit() {
        ocarina = new ItemOcarina();
        ocarina.init();

        carrot = new ItemEnchantedCarrot();
        carrot.init();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ocarina);
        if (DWMHConfig.EnchantedCarrot.enabled) {
            event.getRegistry().register(carrot);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(ocarina, 0, new ModelResourceLocation("dwmh:Ocarina", "inventory"));
        if (DWMHConfig.EnchantedCarrot.enabled) {
            ModelLoader.setCustomModelResourceLocation(carrot, 0, new ModelResourceLocation("dwmh:EnchantedCarrot", "inventory"));
        }
    }
}
