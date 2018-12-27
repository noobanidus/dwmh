package com.noobanidus.dwmh.events;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.items.ItemEnchantedCarrot;
import com.noobanidus.dwmh.items.ItemWhistle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void onDismount (EntityMountEvent event) {
        DWMH.proxy.onDismount(event);
    }

    @SubscribeEvent
    public static void onInteractCarrot (PlayerInteractEvent.EntityInteract event) {
        ItemEnchantedCarrot.onInteractCarrot(event);
    }

    @SubscribeEvent
    public static void onInteractOcarina (PlayerInteractEvent.EntityInteract event) {
        ItemWhistle.onInteractOcarina(event);
    }

    @SubscribeEvent
    public static void onAttack (AttackEntityEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack is = player.getHeldItemMainhand();
        if (is.isEmpty()) return;

        Item item = is.getItem();

        if (item instanceof ItemWhistle || item instanceof ItemEnchantedCarrot) {
            event.setCanceled(true);
        }
    }
}
