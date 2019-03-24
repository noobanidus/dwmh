package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.util.MessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class PacketMessages {
    public static class GenericMessage implements IMessage {
        private int generic;
        private ITextComponent result;


        public int getGeneric () {
            return generic;
        }

        public ITextComponent getResult() {
            return result;
        }

        public GenericMessage () {
        }

        public GenericMessage(MessageHandler.Generic generic, ITextComponent result) {
            this.generic = generic.ordinal();
            this.result = result;

        }

        @Override
        public void fromBytes(ByteBuf buf) {
            PacketBuffer pbuf = new PacketBuffer(buf);
            this.generic = pbuf.readInt();
            try {
                this.result = pbuf.readTextComponent();
            } catch (IOException e) {
                this.result = new TextComponentString("Error");
                e.printStackTrace();
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            PacketBuffer pbuf = new PacketBuffer(buf);
            pbuf.writeInt(this.generic);
            pbuf.writeTextComponent(this.result);
        }

        public static class Handler extends PacketHandler.ClientHandler<GenericMessage> {
            @Override
            @SideOnly(Side.CLIENT)
            public void processMessage(GenericMessage message, MessageContext ctx) {
                Minecraft mc = Minecraft.getMinecraft();

                MessageHandler.handleGenericMessage(mc.player, message);
            }
        }
    }

    public static class ListingMessage implements IMessage {
        private ITextComponent result1;
        private String entityName;
        private long pos;
        private ITextComponent result2;
        private int dist;
        private ITextComponent direction;

        public ListingMessage() {
        }

        public ListingMessage(ITextComponent result1, ITextComponent result2) {
            this.result1 = result1;
            this.result2 = result2;
        }

        public ITextComponent getResult1() {
            return result1;
        }

        public ITextComponent getResult2() {
            return result2;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            PacketBuffer pbuf = new PacketBuffer(buf);
            try {
                this.result1 = pbuf.readTextComponent();
            } catch (IOException e) {
                this.result1 = new TextComponentString("Error");
                e.printStackTrace();
            }
            try {
                this.result2 = pbuf.readTextComponent();
            } catch (IOException e) {
                this.result2 = new TextComponentString("Error");
                e.printStackTrace();
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            PacketBuffer pbuf = new PacketBuffer(buf);
            pbuf.writeTextComponent(this.result1);
            pbuf.writeTextComponent(this.result2);
        }

        public static class Handler extends PacketHandler.ClientHandler<ListingMessage> {

            @Override
            @SideOnly(Side.CLIENT)
            public void processMessage(ListingMessage message, MessageContext ctx) {
                Minecraft mc = Minecraft.getMinecraft();

                MessageHandler.handleListingMessage(mc.player, message);
            }
        }
    }

    public static class SummonMessage implements IMessage {
        private ITextComponent result;

        public SummonMessage() {
        }

        public SummonMessage(ITextComponent result) {
            this.result = result;
        }

        public ITextComponent getResult() {
            return result;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            PacketBuffer pbuf = new PacketBuffer(buf);
            try {
                this.result = pbuf.readTextComponent();
            } catch (IOException e) {
                this.result = new TextComponentString("Error");
                e.printStackTrace();
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            PacketBuffer pbuf = new PacketBuffer(buf);
            pbuf.writeTextComponent(this.result);
        }

        public static class Handler extends PacketHandler.ClientHandler<SummonMessage> {
            @Override
            @SideOnly(Side.CLIENT)
            public void processMessage(SummonMessage message, MessageContext ctx) {
                Minecraft mc = Minecraft.getMinecraft();

                MessageHandler.handleSummonMessage(mc.player, message);
            }
        }
    }
}
