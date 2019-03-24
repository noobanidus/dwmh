package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.util.MessageHandler;
import com.noobanidus.dwmh.util.OcarinaSound;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketSounds {
    public static class OcarinaTune implements IMessage {
        public OcarinaSound type;
        public UUID source;

        public OcarinaTune() {
        }

        public OcarinaTune(EntityPlayer source, OcarinaSound type) {
            this.type = type;
            this.source = source.getUniqueID();
        }

        public OcarinaSound getType() {
            return type;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.type = OcarinaSound.fromOrdinal(buf.readByte());
            this.source = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeByte(this.type.ordinal());
            ByteBufUtils.writeUTF8String(buf, this.source.toString());
        }

        public static class Handler extends PacketHandler.ClientHandler<OcarinaTune> {

            @Override
            public void processMessage(OcarinaTune message, MessageContext ctx) {
                Minecraft mc = Minecraft.getMinecraft();

                EntityPlayer source = mc.world.getPlayerEntityByUUID(message.source);

                MessageHandler.handleOcarinaTune(source, message);
            }
        }
    }
}
