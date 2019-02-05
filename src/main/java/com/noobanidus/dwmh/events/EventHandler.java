package com.noobanidus.dwmh.events;

import com.animania.common.capabilities.ICapabilityPlayer;
import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.capability.CapabilityNameHandler;
import com.noobanidus.dwmh.items.ItemEnchantedCarrot;
import com.noobanidus.dwmh.items.ItemOcarina;
import com.noobanidus.dwmh.proxy.steeds.SteedProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void onDismount(EntityMountEvent event) {
        DWMH.steedProxy.onDismount(event);
    }

    @SubscribeEvent
    public static void onInteractCarrot(PlayerInteractEvent.EntityInteract event) {
        ItemEnchantedCarrot.onInteractCarrot(event);
    }

    @SubscribeEvent
    public static void onInteractOcarina(PlayerInteractEvent.EntityInteract event) {
        ItemOcarina.onInteractOcarina(event);
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack is = player.getHeldItemMainhand();
        if (is.isEmpty()) return;

        Item item = is.getItem();

        if (item instanceof ItemOcarina || item instanceof ItemEnchantedCarrot) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityCapabilitiesAttach (AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();

        if (SteedProxy.LOWEST_DENOMINATOR.isAssignableFrom(entity.getClass()) && DWMH.steedProxy.pseudoTaming(entity)) {
            event.addCapability(CapabilityNameHandler.IDENTIFIER, new CapabilityNameHandler());
        }
    }
}
