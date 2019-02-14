package com.noobanidus.dwmh.events;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.capability.CapabilityOwnHandler;
import com.noobanidus.dwmh.proxy.steeds.SteedProxy;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CapabilityHandler {
    @SubscribeEvent
    public static void onEntityCapabilitiesAttach (AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();

        if (SteedProxy.LOWEST_DENOMINATOR.isAssignableFrom(entity.getClass()) && DWMH.steedProxy.pseudoTaming(entity)) {
            event.addCapability(CapabilityOwnHandler.IDENTIFIER, new CapabilityOwnHandler());
        }
    }
}
