package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.util.MessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class PacketMessages {
    public static class GenericMessage implements IMessage {
        private int entityId;
        private int generic;
        private String langKey;
        private String textFormat;

        public int getGeneric () {
            return generic;
        }

        public String getLangKey() {
            return langKey;
        }

        public String getTextFormat() {
            return textFormat;
        }

        public GenericMessage () {
        }

        public GenericMessage(Entity entity, MessageHandler.Generic generic, @Nullable String keyOverride, @Nullable TextFormatting format) {
            this.entityId = entity.getEntityId();
            this.generic = generic.ordinal();
            if (keyOverride != null) {
                this.langKey = keyOverride;
            } else {
                this.langKey = generic.getLanguageKey();
            }
            if (format != null) {
                this.textFormat = format.getFriendlyName();
            } else {
                this.textFormat = TextFormatting.YELLOW.getFriendlyName();
            }
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.entityId = buf.readInt();
            this.generic = buf.readInt();
            this.langKey = ByteBufUtils.readUTF8String(buf);
            this.textFormat = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.entityId);
            buf.writeInt(this.generic);
            ByteBufUtils.writeUTF8String(buf, this.langKey);
            ByteBufUtils.writeUTF8String(buf, this.textFormat);
        }

        @SideOnly(Side.CLIENT)
        public Entity getEntity (World world) {
            return world.getEntityByID(this.entityId);
        }

        public static class Handler extends PacketHandler.ClientHandler<GenericMessage> {
            @Override
            void processMessage(GenericMessage message, MessageContext ctx) {
                Minecraft mc = Minecraft.getMinecraft();

                MessageHandler.handleGenericMessage(mc.player, message);
            }
        }
    }
}
