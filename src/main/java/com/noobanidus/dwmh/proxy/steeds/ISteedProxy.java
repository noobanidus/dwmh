package com.noobanidus.dwmh.proxy.steeds;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.EntityMountEvent;

public interface ISteedProxy {
    boolean isTeleportable (Entity entity, EntityPlayer player);

    default boolean hasCustomName (Entity entity) {
        return entity.hasCustomName();
    }

    default String getCustomNameTag (Entity entity) {
        return entity.getCustomNameTag();
    }

    default void setCustomNameTag (Entity entity, String name) {
        entity.setCustomNameTag(name);
    }

    default boolean globalTeleportCheck (Entity entity, EntityPlayer player) {
        EntityAnimal animal = (EntityAnimal) entity;

        if (animal.getLeashed() || (animal.isBeingRidden() && animal.isRidingSameEntity(player))) {
            return false;
        }

        // And prevent you from summoning animals being ridden by other players
        if (animal.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            return false;
        }

        // Compatibility for Horse Power device-attached animals
        if (animal.hasHome() && animal.world.getTileEntity(animal.getHomePosition()) != null) {
            return false;
        }

        return true;
    }

    boolean isListable (Entity entity, EntityPlayer player);

    // Carrot
    boolean isTameable (Entity entity, EntityPlayer player);

    void tame (Entity entity, EntityPlayer player);

    boolean isAgeable (Entity entity, EntityPlayer player);

    void age (Entity entity, EntityPlayer player);

    default boolean isHealable (Entity entity, EntityPlayer player) {
        if (isMyMod(entity)) {
            EntityLiving horse = (EntityLiving) entity;
            if (horse.getHealth() < horse.getMaxHealth()) return true;

            return false;
        }

        return false;
    }

    default void heal (Entity entity, EntityPlayer player) {
        EntityLiving horse = (EntityLiving) entity;
        horse.heal(horse.getMaxHealth() - horse.getHealth());
        horse.world.setEntityState(horse, (byte)7);
    }

    // Not currently implemented
    boolean isBreedable (Entity entity, EntityPlayer player);

    void breed (Entity entity, EntityPlayer player);

    ITextComponent getResponseKey (Entity entity, EntityPlayer player);

    default boolean isLoaded () {
        return true;
    }

    default boolean isMyMod (Entity entity) {
        return false;
    }

    default String proxyName () {
        return "default";
    }

    default boolean onDismount (EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && isMyMod(event.getEntityBeingMounted()) && DWMHConfig.Ocarina.home && !DWMHConfig.Ocarina.skipDismount) {
            EntityCreature entity = (EntityCreature) event.getEntityBeingMounted();
            entity.detachHome();
            DWMH.LOG.info("Removed home for " + entity.getDisplayName());
            return true;
        }

        return false;
    }
}
