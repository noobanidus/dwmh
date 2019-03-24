package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.config.Registrar;
import com.noobanidus.dwmh.items.ItemOcarina;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOcarina {
    public static class Mode implements IMessage {
        private ItemOcarina.Mode main;
        private ItemOcarina.Mode sneak;

        public Mode() {
        }

        public Mode (ItemOcarina.PlayerMode mode) {
            this(mode.getMain(), mode.getSneak());
        }

        public Mode(ItemOcarina.Mode main, ItemOcarina.Mode sneak) {
            this.main = main;
            this.sneak = sneak;
        }

        public ItemOcarina.Mode getMain() {
            return main;
        }

        public ItemOcarina.Mode getSneak() {
            return sneak;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.main = ItemOcarina.Mode.fromOrdinal(buf.readInt());
            this.sneak = ItemOcarina.Mode.fromOrdinal(buf.readInt());
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(main.ordinal());
            buf.writeInt(sneak.ordinal());
        }

        public static class Handler extends PacketHandler.ServerHandler<Mode> {
            @Override
            void processMessage(Mode message, MessageContext ctx) {
                EntityPlayerMP player = ctx.getServerHandler().player;

                Registrar.ocarina.setMode(player, message.getMain(), message.getSneak());
            }
        }
    }

    public static class Trigger implements IMessage {
        private boolean isSneaking;
        private EnumHand hand;

        public Trigger () {
        }

        public Trigger (EntityPlayer player, EnumHand hand) {
            this.isSneaking = player.isSneaking();
            this.hand = hand;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.isSneaking = buf.readBoolean();
            this.hand = EnumHand.valueOf(ByteBufUtils.readUTF8String(buf));
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeBoolean(this.isSneaking);
            ByteBufUtils.writeUTF8String(buf, this.hand.toString());
        }

        public static class Handler extends PacketHandler.ServerHandler<Trigger> {

            @Override
            void processMessage(Trigger message, MessageContext ctx) {
                EntityPlayerMP player = ctx.getServerHandler().player;

                Registrar.ocarina.trigger(player, message.hand, message.isSneaking);
            }
        }
    }
}
