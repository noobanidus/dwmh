package com.noobanidus.dwmh.proxy.steeds.wrappers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public interface IWrapper {
    boolean isHorseSaddled();

    boolean isTame();

    boolean isChild();

    UUID getOwnerUniqueId();

    int getGrowingAge();

    void setGrowingAge(int age);

    Entity getEntity();

    boolean isInLove();

    void setInLove(EntityPlayer player);

    boolean hasHome();

    BlockPos getHomePosition();

    boolean getLeashed();

    boolean isBeingRidden();

    boolean isRidingSameEntity(Entity entity);

    void setTamedBy(EntityPlayer player);
}
