package com.noobanidus.dwmh.proxy.steeds.wrappers;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class IceAndFireWrapper implements IWrapper {
    public int dimension;
    public World world;
    private EntityDragonBase dragon = null;
    private EntityHippocampus hippocampus = null;
    private EntityHippogryph hippogryph = null;
    private EntityTameable base = null;
    private boolean isDragon = false;

    public IceAndFireWrapper(Entity entity) {
        if (entity instanceof EntityDragonBase) {
            dragon = (EntityDragonBase) entity;
            isDragon = true;
        } else if (entity instanceof EntityHippocampus) hippocampus = (EntityHippocampus) entity;
        else if (entity instanceof EntityHippogryph) hippogryph = (EntityHippogryph) entity;
        base = (EntityTameable) entity;

        dimension = base.dimension;
        world = base.world;
    }

    public boolean isSitting() {
        return (isDragon) ? dragon.isSitting() : ((hippogryph != null) ? hippogryph.isSitting() : hippocampus.isSitting());
    }

    @Override
    public boolean isHorseSaddled() {
        return (isDragon) || ((hippogryph != null) ? hippogryph.isSaddled() : hippocampus.isSaddled());
    }

    public boolean isDead() {
        if (!isDragon) return false;

        return dragon.isMobDead();
    }

    public boolean getIsDragon() {
        return isDragon;
    }

    @Override
    public boolean isTame() {
        return base.isTamed();
    }

    @Override
    public boolean isChild() {
        return base.isChild();
    }

    @Override
    public UUID getOwnerUniqueId() {
        return base.getOwnerId();
    }

    @Override
    public int getGrowingAge() {
        return base.getGrowingAge();
    }

    @Override
    public void setGrowingAge(int age) {
        if (!isDragon)
            base.setGrowingAge(age);
    }

    @Override
    public Entity getEntity() {
        return base;
    }

    @Override
    public boolean isInLove() {
        return base.isInLove();
    }

    @Override
    public void setInLove(EntityPlayer player) {
        ((EntityTameable) getEntity()).setInLove(player);
    }

    @Override
    public boolean hasHome() {
        return base.hasHome();
    }

    @Override
    public BlockPos getHomePosition() {
        return base.getHomePosition();
    }

    @Override
    public boolean getLeashed() {
        return base.getLeashed();
    }

    @Override
    public boolean isBeingRidden() {
        return base.isBeingRidden();
    }

    @Override
    public boolean isRidingSameEntity(Entity entity) {
        return base.isRidingSameEntity(entity);
    }

    public int getDragonStage() {
        if (!isDragon) return (isChild() ? 0 : 2);

        return dragon.getDragonStage();
    }

    @Override
    public void setTamedBy(EntityPlayer player) {
        ((EntityTameable) getEntity()).setTamedBy(player);
    }

    public boolean isListable() {
        if (isDragon) return true;

        return !isChild();
    }
}
