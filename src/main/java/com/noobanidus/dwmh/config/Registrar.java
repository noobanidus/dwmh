package com.noobanidus.dwmh.config;

import com.noobanidus.dwmh.items.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber
public class Registrar {
    public static ItemWhistle whistle = null;

    @SuppressWarnings("ConstantConditions")
    public static void preInit() {
        if (!Loader.isModLoaded("Circadian") && ItemWhistle.enabled) {
            whistle = new ItemWhistle();
            whistle.init();
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        if (!Loader.isModLoaded("Circadian") && ItemWhistle.enabled) {
            event.getRegistry().register(whistle);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        if (!Loader.isModLoaded("Circadian") && ItemWhistle.enabled) {
            ModelLoader.setCustomModelResourceLocation(whistle, 0, new ModelResourceLocation("dwmh:whistle", "inventory"));
        }
    }
}
