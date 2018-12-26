package com.noobanidus.dwmh.proxy;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.items.ItemWhistle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

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
        if (animal.isBeingRidden() && !ItemWhistle.otherRiders) {
            return false;
        }

        // Compatibility for Horse Power device-attached animals
        if (animal.hasHome() && animal.world.getTileEntity(animal.getHomePosition()) != null) {
            return false;
        }

        if (ItemWhistle.path && animal.getEntityData().hasKey(ItemWhistle.TAG_MOVING_TO_LOCATION) && animal.getEntityData().getBoolean(ItemWhistle.TAG_MOVING_TO_LOCATION)) {
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
            return DWMH.vanillaProxy.isHealable(entity, player);
        }

        return false;
    }

    default void heal(Entity entity, EntityPlayer player) {
        if (isMyMod(entity)) {
            DWMH.vanillaProxy.heal(entity, player);
        }
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
}
