package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class DWMHPacketHandler {
    private static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(DWMH.MODID);

    public static void sendToAll(IMessage message) {
        instance.sendToAll(message);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        instance.sendTo(message, player);
    }

    public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        instance.sendToAllAround(message, point);
    }

    public static void sendToAllTracking(IMessage message, NetworkRegistry.TargetPoint point) {
        instance.sendToAllTracking(message, point);
    }

    public static void sendToAllTracking(IMessage message, Entity entity) {
        instance.sendToAllTracking(message, entity);
    }

    public static void sendToDimension(IMessage message, int dimensionId) {
        instance.sendToDimension(message, dimensionId);
    }

    public static void sendToServer(IMessage message) {
        instance.sendToServer(message);
    }
}
