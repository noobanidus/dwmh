package com.noobanidus.dwmh.proxy.vanilla;

import com.noobanidus.dwmh.items.ItemWhistle;
import com.noobanidus.dwmh.proxy.ISteedProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;

public class VanillaProxy implements ISteedProxy {
     public boolean isTeleportable (Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        if (!horse.isHorseSaddled() || horse.getLeashed() || (horse.isBeingRidden() && horse.isRidingSameEntity(player))) {
            return false;
        }

        // And prevent you from summoning horses being ridden by other players
        if (horse.isBeingRidden() && !ItemWhistle.otherRiders) {
            return false;
        }

        // Compatibility for Horse Power device-attached horses
        if (horse.hasHome() && horse.world.getTileEntity(horse.getHomePosition()) != null) {
            return false;
        }

        return true;
    }

    public boolean isListable (Entity entity, EntityPlayer player) {
        if (entity == null || entity.isDead || !(entity instanceof AbstractHorse)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        if (horse.isChild() || !horse.isTame() || horse.dimension != player.dimension || (horse.getOwnerUniqueId() != null && !horse.getOwnerUniqueId().equals(player.getUniqueID()))) {
            return false;
        }

        return true;
    }

    // Carrot
    public boolean isTameable (Entity entity, EntityPlayer player) {
         if (!(entity instanceof AbstractHorse)) {
             return false;
         }

         AbstractHorse horse = (AbstractHorse) entity;

         if (horse.isChild() || horse.isTame()) {
             return false;
         }

        return true;
    }

    public void tame (Entity entity, EntityPlayer player) {
        ((AbstractHorse) entity).setTamedBy(player);
    }

    public boolean isAgeable (Entity entity, EntityPlayer player) {
         if (!(entity instanceof AbstractHorse)) {
             return false;
         }

         AbstractHorse horse = (AbstractHorse) entity;

         if (!horse.isChild()) {
             return false;
         }

         if (horse.getEntityData().getBoolean("quark:poison_potato_applied")) {
             return false;
         }

         return true;
    }

    public void age (Entity entity, EntityPlayer player) {
        AbstractHorse horse = (AbstractHorse) entity;

        horse.setGrowingAge(0);
        horse.world.setEntityState(horse, (byte)7);
    }

    public boolean isHealable (Entity entity, EntityPlayer player) {
         if (!(entity instanceof AbstractHorse)) {
             return false;
         }

         AbstractHorse horse = (AbstractHorse) entity;

         return horse.getHealth() < horse.getMaxHealth();
    }

    public void heal (Entity entity, EntityPlayer player) {
         AbstractHorse horse = (AbstractHorse) entity;

         horse.heal(horse.getMaxHealth() - horse.getHealth());
         horse.world.setEntityState(horse, (byte)7);
    }

    // Not currently implemented
    public boolean isBreedable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void breed (Entity entity, EntityPlayer player) {
    }
}
