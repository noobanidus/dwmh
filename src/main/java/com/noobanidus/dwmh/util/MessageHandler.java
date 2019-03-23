package com.noobanidus.dwmh.util;

import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.network.PacketHandler;
import com.noobanidus.dwmh.network.PacketMessages;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class MessageHandler {
    public static void handleGenericMessage (EntityPlayer player, PacketMessages.GenericMessage message) {
        Generic generic = Generic.fromOrdinal(message.getGeneric());

        switch (generic) {
            case AGING:
                if (!DWMHConfig.client.clientCarrot.aging) return;
                break;
            case TAMING:
                if (!DWMHConfig.client.clientCarrot.taming) return;
                break;
            case HEALING:
                if (!DWMHConfig.client.clientCarrot.healing) return;
                break;
            case BREEDING:
                if (!DWMHConfig.client.clientCarrot.breeding) return;
                break;
        }

        Entity entity = message.getEntity(player.world);

        TextComponentTranslation key = new TextComponentTranslation(message.getLangKey(), entity.getDisplayName());
        String format = message.getTextFormat();
        if (!format.isEmpty()) {
            TextFormatting tf = TextFormatting.getValueByName(format);
            if (tf != null) {
                key.setStyle(new Style().setColor(tf));
            }
        }

        player.sendMessage(key);
    }

    public static void sendGenericMessage (EntityPlayer player, Entity entity, @Nullable Generic generic, @Nullable String keyOverride, @Nullable TextFormatting format) {
        PacketMessages.GenericMessage packet = new PacketMessages.GenericMessage(entity, generic, keyOverride, format);
        PacketHandler.sendTo(packet, (EntityPlayerMP) player);
    }

    public enum Generic {
        TAMING("dwmh.strings.generic.tamed"),
        HEALING("dwmh.strings.generic.healed"),
        AGING("dwmh.strings.generic.aged"),
        BREEDING("dwhm.strings.generic.breed"),
        EMPTY("");

        String languageKey;

        Generic(String langKey) {
            this.languageKey = langKey;
        }

        public String getLanguageKey() {
            return this.languageKey;
        }

        public boolean isEmpty() {
            return this.equals(Generic.EMPTY);
        }

        public static Generic fromOrdinal (int ordinal) {
            int i = 0;
            for (Generic generic : Generic.values()) {
                if (ordinal == i++) return generic;
            }

            return EMPTY;
        }
    }
}
