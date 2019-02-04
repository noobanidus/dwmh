package com.noobanidus.dwmh.wrappers;

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
    private EntityDragonBase dragon = null;
    private EntityHippocampus hippocampus = null;
    private EntityHippogryph hippogryph = null;
    private EntityTameable base = null;
    private boolean isDragon = false;

    public int dimension;
    public World world;

    public IceAndFireWrapper (Entity entity) {
        if (entity instanceof EntityDragonBase) {
            dragon = (EntityDragonBase) entity;
            isDragon = true;
        }
        else if (entity instanceof EntityHippocampus) hippocampus = (EntityHippocampus) entity;
        else if (entity instanceof EntityHippogryph) hippogryph = (EntityHippogryph) entity;
        base = (EntityTameable) entity;

        dimension = base.dimension;
        world = base.world;
    }

    public boolean isSitting () {
        return (isDragon) ? dragon.isSitting() : ((hippogryph != null) ? hippogryph.isSitting() : hippocampus.isSitting());
    }

    public boolean isHorseSaddled () {
        return (isDragon) || ((hippogryph != null) ? hippogryph.isSaddled() : hippocampus.isSaddled());
    }

    public boolean isDead () {
        if (!isDragon) return false;

        return dragon.isMobDead();
    }

    public boolean getIsDragon () {
        return isDragon;
    }

    public boolean isTame () {
        return base.isTamed();
    }
    public boolean isChild () {
        return base.isChild();
    }
    public UUID getOwnerUniqueId() {
        return base.getOwnerId();
    }
    public void setGrowingAge (int age) {
        if (!isDragon)
            base.setGrowingAge(age);
    }
    public int getGrowingAge () {
        return base.getGrowingAge();
    }

    public Entity getEntity () {
        return base;
    }

    public boolean isInLove () {
        return base.isInLove();
    }

    public boolean hasHome () {
        return base.hasHome();
    }

    public BlockPos getHomePosition () {
        return base.getHomePosition();
    }

    public boolean getLeashed () {
        return base.getLeashed();
    }

    public boolean isBeingRidden () {
        return base.isBeingRidden();
    }

    public boolean isRidingSameEntity (Entity entity) {
        return base.isRidingSameEntity(entity);
    }

    public int getDragonStage () {
        if (!isDragon) return (isChild() ? 0 : 2);

        return dragon.getDragonStage();
    }

    public void setTamedBy (EntityPlayer player) {
        ((EntityTameable) getEntity()).setTamedBy(player);
    }

    public void setInLove (EntityPlayer player) {
        ((EntityTameable) getEntity()).setInLove(player);
    }

    public boolean isListable () {
        if (isDragon) return true;

        return !isChild();
    }
}
