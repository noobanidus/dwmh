package com.noobanidus.dwmh.config;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.items.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class Registrar {
    public static ItemWhistle whistle = null;
    public static ItemEnchantedCarrot carrot = null;

    public static void preInit() {
        whistle = new ItemWhistle();
        whistle.init();

        carrot = new ItemEnchantedCarrot();
        carrot.init();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(whistle);
        if (ItemEnchantedCarrot.enabled) {
            event.getRegistry().register(carrot);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(whistle, 0, new ModelResourceLocation("dwmh:whistle", "inventory"));
        if (ItemEnchantedCarrot.enabled) {
            ModelLoader.setCustomModelResourceLocation(carrot, 0, new ModelResourceLocation("dwmh:carrot", "inventory"));
        }
    }

    @SubscribeEvent
    public static void onDismount (EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && event.getEntityBeingMounted() instanceof AbstractHorse && ItemWhistle.home && !ItemWhistle.skipDismount) {
            AbstractHorse entity = (AbstractHorse) event.getEntityBeingMounted();
            entity.detachHome();
        }
    }

    @SubscribeEvent()
    public static void onInteractCarrot (PlayerInteractEvent.EntityInteract event) {
        ItemEnchantedCarrot.onInteractCarrot(event);
    }

    @SubscribeEvent
    public static void onInteractOcarina (PlayerInteractEvent.EntityInteract event) {
        ItemWhistle.onInteractOcarina(event);
    }
}
