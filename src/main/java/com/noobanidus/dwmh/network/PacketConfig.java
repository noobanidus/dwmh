package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.config.ConfigHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@SuppressWarnings("unused")
public class PacketConfig {
    public static class UpdateFromServer implements IMessage {
        private NBTTagCompound compound;

        public UpdateFromServer() {
        }

        public UpdateFromServer(NBTTagCompound compound) {
            this.compound = compound;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.compound = ByteBufUtils.readTag(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            ByteBufUtils.writeTag(buf, this.compound);
        }

        public static class Handler extends PacketHandler.Handler<UpdateFromServer> {
            void processMessage(UpdateFromServer message, MessageContext ctx) {
                ConfigHandler.deserialize(message.compound);
            }
        }
    }

}
