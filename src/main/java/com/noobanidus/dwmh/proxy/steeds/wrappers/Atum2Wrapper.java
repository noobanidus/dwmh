package com.noobanidus.dwmh.proxy.steeds.wrappers;

import com.teammetallurgy.atum.entity.animal.EntityCamel;
import com.teammetallurgy.atum.entity.animal.EntityDesertWolf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    @Override
    public boolean isHorseSaddled () {
        return (wolf != null) ? wolf.isSaddled() : camel.isHorseSaddled();
    }

    @Override
    public boolean isTame () {
        return (wolf != null) ? wolf.isTamed() : camel.isTame();
    }

    @Override
    public boolean isChild () {
        return (wolf != null) ? wolf.isChild() : camel.isChild();
    }

    @Override
    public UUID getOwnerUniqueId () {
        return (wolf != null) ? wolf.getOwnerId() : camel.getOwnerUniqueId();
    }

    @Override
    public void setGrowingAge (int age) {
        if (wolf != null) wolf.setGrowingAge(age);
        else camel.setGrowingAge(age);
    }

    @Override
    public int getGrowingAge () {
        return (wolf != null) ? wolf.getGrowingAge() : camel.getGrowingAge();
    }

    @Override
    public Entity getEntity () {
        return (wolf != null) ? wolf : camel;
    }

    @Override
    public boolean isInLove () {
        return (wolf != null) ? wolf.isInLove() : camel.isInLove();
    }

    @Override
    public boolean hasHome () {
        return (wolf != null) ? wolf.hasHome() : camel.hasHome();
    }

    @Override
    public BlockPos getHomePosition () {
        return (wolf != null) ? wolf.getHomePosition() : camel.getHomePosition();
    }

    @Override
    public boolean getLeashed () {
        return (wolf != null) ? wolf.getLeashed() : camel.getLeashed();
    }

    @Override
    public boolean isBeingRidden () {
        return (wolf != null) ? wolf.isBeingRidden() : camel.isBeingRidden();
    }

    @Override
    public boolean isRidingSameEntity (Entity entity) {
        return (wolf != null) ? wolf.isRidingSameEntity(entity) : camel.isRidingSameEntity(entity);
    }

    public boolean isAlpha () {
        return (wolf != null) && wolf.isAlpha();
    }

    @Override
    public void setTamedBy (EntityPlayer player) {
        // Don't use this
    }

    @Override
    public void setInLove (EntityPlayer player) {
        ((EntityAnimal) getEntity()).setInLove(player);
    }
}
