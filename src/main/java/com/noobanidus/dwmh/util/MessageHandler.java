package com.noobanidus.dwmh.util;

import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.network.PacketHandler;
import com.noobanidus.dwmh.network.PacketMessages;
import com.noobanidus.dwmh.network.PacketSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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
            case SUMMONED:
                if (!DWMHConfig.client.clientOcarina.simple) return;
                break;
            case SUMMONED_PACK:
                if (!DWMHConfig.client.clientOcarina.simple) return;
                break;
        }

        player.sendMessage(message.getResult());
    }

    public static void sendGenericMessage (EntityPlayer player, @Nullable Entity entity, Generic generic, @Nullable String keyOverride, @Nullable TextFormatting format) {
        String langKey;
        if (keyOverride != null) {
                langKey = keyOverride;
            } else {
                langKey = generic.getLanguageKey();
            }
        if (format == null) {
            format = TextFormatting.YELLOW;
        }

        TextComponentTranslation key;

        if (entity != null) {
            key = new TextComponentTranslation(langKey, entity.getDisplayName());
        } else {
            key = new TextComponentTranslation(langKey);
        }

        key.setStyle(new Style().setColor(format));

        PacketMessages.GenericMessage packet = new PacketMessages.GenericMessage(generic, key);
        PacketHandler.sendTo(packet, (EntityPlayerMP) player);
    }

    public static void handleListingMessage (EntityPlayer player, PacketMessages.ListingMessage message) {
        ITextComponent result = message.getResult1();

        if (DWMHConfig.client.clientOcarina.distance) {
            result.appendSibling(message.getResult2());
        }

        player.sendMessage(result);
    }

    public static void sendListingMessage (EntityPlayer player, ITextComponent result1, ITextComponent result2) {
        PacketMessages.ListingMessage packet = new PacketMessages.ListingMessage(result1, result2);
        PacketHandler.sendTo(packet, (EntityPlayerMP) player);
    }

    public static void handleSummonMessage (EntityPlayer player, PacketMessages.SummonMessage message) {
        if (!DWMHConfig.client.clientOcarina.quiet && !DWMHConfig.client.clientOcarina.simple) {
            player.sendMessage(message.getResult());
        }
    }

    public static void sendSummonMessage (EntityPlayer player, ITextComponent result) {
        PacketMessages.SummonMessage packet = new PacketMessages.SummonMessage(result);
        PacketHandler.sendTo(packet, (EntityPlayerMP) player);
    }

    public static void handleOcarinaTune (EntityPlayer player, PacketSounds.OcarinaTune message) {
        if (DWMHConfig.client.clientOcarina.sounds) {
            SoundEvent sound = message.getType().getSoundEvent();
            if (sound != null) {
                player.playSound(sound, 1.5f, 1);
            }
        }
    }

    public static void sendOcarinaTune (ItemStack stack, EntityPlayer source, OcarinaSound sound) {
        long cur = MinecraftServer.getCurrentTimeMillis();

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        if (tag.hasKey("dwmh:last_played")) {
            long lastPlayed = tag.getLong("dwmh:last_played");
            if (cur - lastPlayed < DWMHConfig.Ocarina.soundDelay * 1000) {
                return;
            }
        }

        tag.setLong("dwmh:last_played", cur);

        PacketSounds.OcarinaTune packet = new PacketSounds.OcarinaTune(source, sound);
        PacketHandler.sendToAllAround(packet, new NetworkRegistry.TargetPoint(source.dimension, source.posX, source.posY, source.posZ, 32));
    }

    public enum Generic {
        TAMING("dwmh.strings.generic.tamed"),
        HEALING("dwmh.strings.generic.healed"),
        AGING("dwmh.strings.generic.aged"),
        BREEDING("dwmh.strings.generic.breed"),
        SUMMONED("dwmh.strings.generic.summoned"),
        SUMMONED_PACK("dwmh.strings.generic.summoned.pack"),
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
