package com.noobanidus.dwmh.wrappers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public interface IWrapper {
    boolean isHorseSaddled ();
    boolean isTame ();
    boolean isChild ();
    UUID getOwnerUniqueId();
    void setGrowingAge (int age);
    int getGrowingAge ();
    Entity getEntity ();
    boolean isInLove ();
    boolean hasHome ();
    BlockPos getHomePosition ();
    boolean getLeashed ();
    boolean isBeingRidden ();
    boolean isRidingSameEntity (Entity entity);
    void setTamedBy (EntityPlayer player);
    void setInLove (EntityPlayer player);
}
