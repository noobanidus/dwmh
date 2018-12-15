package com.noobanidus.dwmh.util;

import net.minecraft.entity.passive.AbstractHorse;

import java.util.Arrays;
import java.util.List;

public class Util {
    public static List<String> animaniaHorses = Arrays.asList("com.animania.common.entities.horses.EntityFoalDraftHorse", "com.animania.common.entities.horses.EntityMareDraftHorse",
"com.animania.common.entities.horses.EntityStallionDraftHorse");

    public static boolean isAnimania (AbstractHorse horse) {
        return animaniaHorses.contains(horse.getClass().getName());
    }
}
