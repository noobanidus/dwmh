package com.noobanidus.dwmh.wrappers;

import com.teammetallurgy.atum.entity.animal.EntityCamel;
import com.teammetallurgy.atum.entity.animal.EntityDesertWolf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

public class Atum2Wrapper implements IWrapper {
    private EntityDesertWolf wolf = null;
    private EntityCamel camel = null;

    public int dimension;
    public World world;

    public Atum2Wrapper(Entity entity) {
        assert entity instanceof EntityCamel || entity instanceof EntityDesertWolf;
        if (entity instanceof EntityDesertWolf)  this.wolf = (EntityDesertWolf) entity;
        else this.camel = (EntityCamel) entity;
        this.dimension = entity.dimension;
        this.world = entity.world;
    }

    public boolean isHorseSaddled () {
        return (wolf != null) ? wolf.isSaddled() : camel.isHorseSaddled();
    }

    public boolean isTame () {
        return (wolf != null) ? wolf.isTamed() : camel.isTame();
    }

    public boolean isChild () {
        return (wolf != null) ? wolf.isChild() : camel.isChild();
    }

    public UUID getOwnerUniqueId () {
        return (wolf != null) ? wolf.getOwnerId() : camel.getOwnerUniqueId();
    }

    public void setGrowingAge (int age) {
        if (wolf != null) wolf.setGrowingAge(age);
        else camel.setGrowingAge(age);
    }

    public int getGrowingAge () {
        return (wolf != null) ? wolf.getGrowingAge() : camel.getGrowingAge();
    }

    public Entity getEntity () {
        return (wolf != null) ? wolf : camel;
    }

    public boolean isInLove () {
        return (wolf != null) ? wolf.isInLove() : camel.isInLove();
    }

    public boolean hasHome () {
        return (wolf != null) ? wolf.hasHome() : camel.hasHome();
    }

    public BlockPos getHomePosition () {
        return (wolf != null) ? wolf.getHomePosition() : camel.getHomePosition();
    }

    public boolean getLeashed () {
        return (wolf != null) ? wolf.getLeashed() : camel.getLeashed();
    }

    public boolean isBeingRidden () {
        return (wolf != null) ? wolf.isBeingRidden() : camel.isBeingRidden();
    }

    public boolean isRidingSameEntity (Entity entity) {
        return (wolf != null) ? wolf.isRidingSameEntity(entity) : camel.isRidingSameEntity(entity);
    }

    public boolean isAlpha () {
        return (wolf != null) && wolf.isAlpha();
    }

    public void setTamedBy (EntityPlayer player) {
        // Don't use this
    }

    public void setInLove (EntityPlayer player) {
        ((EntityAnimal) getEntity()).setInLove(player);
    }
}
