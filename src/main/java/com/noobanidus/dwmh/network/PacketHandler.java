package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public class PacketHandler {
    private static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(DWMH.MODID);

    private static int id = 0;

    public static void initPackets () {
        registerMessage(PacketConfig.UpdateFromServer.Handler.class, PacketConfig.UpdateFromServer.class, Side.CLIENT);
    }

    public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends Handler<REQ>> messageHandler, Class<REQ> requestMessageType, Side side) {
        instance.registerMessage(messageHandler, requestMessageType, id++, side);
    }

    public static void register () {
        registerMessage(PacketConfig.UpdateFromServer.Handler.class, PacketConfig.UpdateFromServer.class, Side.CLIENT);
    }

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

    public abstract static class Handler<T extends IMessage> implements IMessageHandler<T, IMessage> {

        public IMessage onMessage(T message, MessageContext ctx) {
            DWMH.schedule(ctx, () -> processMessage(message, ctx));

            return null;
        }

        abstract void processMessage(T message, MessageContext ctx);
    }
}
