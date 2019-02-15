package com.noobanidus.dwmh.util;

import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.config.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public enum SoundType {
    NORMAL(0),
    MINOR(1),
    SPECIAL(2),
    BROKEN(3),
    NONE(-1);

    public int type;

    SoundType(int type) {
        this.type = type;
    }

    public SoundEvent getSoundEvent() {
        switch (type) {
            case 0:
                return SoundHandler.getRandomWhistle();
            case 1:
                return SoundHandler.getRandomMinorWhistle();
            case 2:
                return SoundHandler.WHISTLE_SPECIAL;
            case 3:
                return SoundHandler.WHISTLE_BROKEN;
            case -1:
            default:
                return null;
        }
    }

    public void playSound(EntityPlayer player, ItemStack stack) {
        // This is predicated on a !player.world.isRemote check

        // TODO
        if (!DWMHConfig.client.clientOcarina.sounds) return;

        SoundEvent e = this.getSoundEvent();
        if (e == null) return;

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

        player.world.playSound(null, player.getPosition(), e, SoundCategory.PLAYERS, 1.5f, 1);
    }
}
