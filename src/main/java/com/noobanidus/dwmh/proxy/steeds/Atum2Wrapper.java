package com.noobanidus.dwmh.proxy.steeds;

import com.teammetallurgy.atum.entity.animal.EntityCamel;
import com.teammetallurgy.atum.entity.animal.EntityDesertWolf;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Atum2Wrapper {
    private EntityDesertWolf wolf;
    private EntityCamel camel;

    public int dimension;
    public World world;

    private Atum2Wrapper(EntityCamel camel) {
        this.wolf = null;
        this.camel = camel;
        this.dimension = camel.dimension;
        this.world = camel.world;
    }

    private Atum2Wrapper(EntityDesertWolf wolf) {
        this.wolf = wolf;
        this.camel = null;
        this.dimension = wolf.dimension;
        this.world = wolf.world;
    }

    @Nonnull
    public static Atum2Wrapper build (Entity entity) {
        assert entity instanceof EntityCamel || entity instanceof EntityDesertWolf;

        return (entity instanceof EntityCamel) ? new Atum2Wrapper((EntityCamel) entity) : new Atum2Wrapper((EntityDesertWolf) entity);
    }

    boolean isHorseSaddled () {
        return (wolf != null) ? wolf.isSaddled() : camel.isHorseSaddled();
    }

    boolean isTame () {
        return (wolf != null) ? wolf.isTamed() : camel.isTame();
    }

    boolean isChild () {
        return (wolf != null) ? wolf.isChild() : camel.isChild();
    }

    UUID getOwnerUniqueId () {
        return (wolf != null) ? wolf.getOwnerId() : camel.getOwnerUniqueId();
    }

    void setGrowingAge (int age) {
        if (wolf != null) wolf.setGrowingAge(age);
        else camel.setGrowingAge(age);
    }

    int getGrowingAge () {
        return (wolf != null) ? wolf.getGrowingAge() : camel.getGrowingAge();
    }

    Entity getEntity () {
        return (wolf != null) ? wolf : camel;
    }

    boolean isInLove () {
        return (wolf != null) ? wolf.isInLove() : camel.isInLove();
    }

    boolean hasHome () {
        return (wolf != null) ? wolf.hasHome() : camel.hasHome();
    }

    BlockPos getHomePosition () {
        return (wolf != null) ? wolf.getHomePosition() : camel.getHomePosition();
    }

    boolean getLeashed () {
        return (wolf != null) ? wolf.getLeashed() : camel.getLeashed();
    }

    boolean isBeingRidden () {
        return (wolf != null) ? wolf.isBeingRidden() : camel.isBeingRidden();
    }

    boolean isRidingSameEntity (Entity entity) {
        return (wolf != null) ? wolf.isRidingSameEntity(entity) : camel.isRidingSameEntity(entity);
    }

    boolean isAlpha () {
        return (wolf != null) && wolf.isAlpha();
    }
}
