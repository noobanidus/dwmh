package com.noobanidus.dwmh.util;

import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.config.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public enum OcarinaSound {
    NORMAL(0),
    MINOR(1),
    SPECIAL(2),
    BROKEN(3),
    NONE(-1);

    public int type;

    OcarinaSound(int type) {
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
        }

        return null;
    }

    public static OcarinaSound fromOrdinal(int ordinal) {
        int i = 0;
        for (OcarinaSound generic : OcarinaSound.values()) {
            if (ordinal == i++) return generic;
        }

        return NONE;
    }
}
