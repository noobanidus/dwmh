package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public class PacketHandler {
    private static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(DWMH.MODID);

    public static void initPackets() {
        instance.registerMessage(PacketConfig.UpdateFromServer.Handler.class, PacketConfig.UpdateFromServer.class, 0, Side.CLIENT);
        instance.registerMessage(PacketParticles.GenerateParticles.Handler.class, PacketParticles.GenerateParticles.class, 1, Side.CLIENT);
        instance.registerMessage(PacketMessages.GenericMessage.Handler.class, PacketMessages.GenericMessage.class, 2, Side.CLIENT);
        instance.registerMessage(PacketMessages.ListingMessage.Handler.class, PacketMessages.ListingMessage.class, 3, Side.CLIENT);
        instance.registerMessage(PacketMessages.SummonMessage.Handler.class, PacketMessages.SummonMessage.class, 4, Side.CLIENT);
        instance.registerMessage(PacketSounds.OcarinaTune.Handler.class, PacketSounds.OcarinaTune.class, 5, Side.CLIENT);
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
        abstract void processMessage(T message, MessageContext ctx);
    }

    public abstract static class ServerHandler<T extends IMessage> extends Handler<T> {

        @Override
        public IMessage onMessage(T message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> processMessage(message, ctx));
            return null;
        }
    }

    public abstract static class ClientHandler<T extends IMessage> extends Handler<T> {

        @Override
        public IMessage onMessage(T message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> processMessage(message, ctx));

            return null;
        }
    }
}
