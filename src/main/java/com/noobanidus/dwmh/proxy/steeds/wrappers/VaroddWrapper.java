package com.noobanidus.dwmh.proxy.steeds.wrappers;

import com.lying.variousoddities.entity.mount.AbstractMount;
import com.lying.variousoddities.entity.mount.EntityGryphon;
import com.noobanidus.dwmh.client.render.particle.ParticleSender;
import com.noobanidus.dwmh.util.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class VaroddWrapper implements IWrapper {
    public int dimension;
    public World world;
    private EntityGryphon gryphon;
    private AbstractHorse pegasi;
    private EntityCreature base;

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

    @Override
    public boolean isHorseSaddled() {
        return (pegasi == null) ? gryphon.isSaddled() : pegasi.isHorseSaddled();
    }

    @Override
    public boolean isTame() {
        return (pegasi != null) ? pegasi.isTame() : gryphon.isTame();
    }

    @Override
    public boolean isChild() {
        return (pegasi != null) && pegasi.isChild();
    }

    @Override
    public UUID getOwnerUniqueId() {
        return (pegasi != null) ? pegasi.getOwnerUniqueId() : gryphon.getOwnerUniqueId();
    }

    @Override
    public int getGrowingAge() {
        return (pegasi != null) ? pegasi.getGrowingAge() : -1;
    }

    @Override
    public void setGrowingAge(int age) {
        if (pegasi != null) pegasi.setGrowingAge(age);
        return;
    }

    @Override
    public EntityCreature getEntity() {
        return base;
    }

    @Override
    public boolean isInLove() {
        return (pegasi != null) && pegasi.isInLove();
    }

    @Override
    public void setInLove(EntityPlayer player) {
        if (pegasi != null) {
            pegasi.setInLove(player);
            pegasi.world.setEntityState(pegasi, (byte) 7);
        }
    }

    @Override
    public boolean hasHome() {
        return (pegasi != null) ? pegasi.hasHome() : gryphon.hasHome();
    }

    @Override
    public BlockPos getHomePosition() {
        return (pegasi != null) ? pegasi.getHomePosition() : gryphon.getHomePosition();
    }

    @Override
    public boolean getLeashed() {
        return (pegasi != null) ? pegasi.getLeashed() : gryphon.getLeashed();
    }

    @Override
    public boolean isBeingRidden() {
        return (pegasi != null) ? pegasi.isBeingRidden() : gryphon.isBeingRidden();
    }

    @Override
    public boolean isRidingSameEntity(Entity entity) {
        return (pegasi != null) ? pegasi.isRidingSameEntity(entity) : gryphon.isRidingSameEntity(entity);
    }

    @Override
    public void setTamedBy(EntityPlayer player) {
        if (pegasi == null) {
            gryphon.setTamedBy(player);
        } else {
            pegasi.setTamedBy(player);
        }
        Entity entity = getEntity();
        ParticleSender.generateParticles(entity, ParticleType.TAMING);
    }

    public boolean ageable() {
        return pegasi != null && isChild();
    }

    public boolean isGryphon() {
        return gryphon != null;
    }
}
