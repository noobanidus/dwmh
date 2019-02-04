package com.noobanidus.dwmh.wrappers;

import com.lying.variousoddities.entity.mount.AbstractMount;
import com.lying.variousoddities.entity.mount.EntityGryphon;
import com.teammetallurgy.atum.entity.animal.EntityCamel;
import com.teammetallurgy.atum.entity.animal.EntityDesertWolf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class VaroddWrapper implements IWrapper {
    private EntityGryphon gryphon;
    private AbstractHorse pegasi;
    private EntityCreature base;

    public int dimension;
    public World world;

    public VaroddWrapper(Entity entity) {
        assert entity instanceof EntityCreature;

        base = (EntityCreature) entity;

        if (entity instanceof AbstractMount) {
            gryphon = (EntityGryphon) entity;
            pegasi = null;
        } else {
            gryphon = null;
            pegasi = (AbstractHorse) entity;
        }

        this.dimension = entity.dimension;
        this.world = entity.world;
    }

    public boolean isHorseSaddled () {
        return (pegasi == null) ? gryphon.isSaddled() : pegasi.isHorseSaddled();
    }

    public boolean isTame () {
        return (pegasi != null) ? pegasi.isTame() : gryphon.isTame();
    }

    public boolean isChild () {
        return (pegasi != null) && pegasi.isChild();
    }

    public UUID getOwnerUniqueId () {
        return (pegasi != null) ? pegasi.getOwnerUniqueId() : gryphon.getOwnerUniqueId();
    }

    public void setGrowingAge (int age) {
        if (pegasi != null) pegasi.setGrowingAge(age);
        return;
    }

    public int getGrowingAge () {
        return (pegasi != null) ? pegasi.getGrowingAge() : -1;
    }

    public EntityCreature getEntity () {
        return base;
    }

    public boolean isInLove () {
        return (pegasi != null) && pegasi.isInLove();
    }

    public boolean hasHome () {
        return (pegasi != null) ? pegasi.hasHome() : gryphon.hasHome();
    }

    public BlockPos getHomePosition () {
        return (pegasi != null) ? pegasi.getHomePosition() : gryphon.getHomePosition();
    }

    public boolean getLeashed () {
        return (pegasi != null) ? pegasi.getLeashed() : gryphon.getLeashed();
    }

    public boolean isBeingRidden () {
        return (pegasi != null) ? pegasi.isBeingRidden() : gryphon.isBeingRidden();
    }

    public boolean isRidingSameEntity (Entity entity) {
        return (pegasi != null) ? pegasi.isRidingSameEntity(entity) : gryphon.isRidingSameEntity(entity);
    }

    public void setTamedBy (EntityPlayer player) {
        if (pegasi == null) {
            gryphon.setTamedBy(player);
            gryphon.world.setEntityState(gryphon, (byte) 7);
        } else {
            pegasi.setTamedBy(player);
            pegasi.world.setEntityState(pegasi, (byte) 7);
        }
    }

    public void setInLove (EntityPlayer player) {
        if (pegasi != null) {
            pegasi.setInLove(player);
            pegasi.world.setEntityState(pegasi, (byte)7);
        }
    }

    public boolean ageable () {
        return pegasi != null && isChild();
    }

    public boolean isGryphon () {
        return gryphon != null;
    }
}
