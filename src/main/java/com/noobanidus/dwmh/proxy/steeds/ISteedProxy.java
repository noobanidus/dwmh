package com.noobanidus.dwmh.proxy.steeds;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.capability.CapabilityOwnHandler;
import com.noobanidus.dwmh.capability.CapabilityOwner;
import com.noobanidus.dwmh.client.render.particle.ParticleSender;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.util.MessageHandler;
import com.noobanidus.dwmh.util.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityMountEvent;

import javax.annotation.Nullable;

public interface ISteedProxy {
    boolean isTeleportable(Entity entity, EntityPlayer player);

    default boolean hasCustomName(Entity entity) {
        return entity.hasCustomName();
    }

    default String getCustomNameTag(Entity entity) {
        return entity.getCustomNameTag();
    }

    default void setCustomNameTag(Entity entity, String name) {
        entity.setCustomNameTag(name);
    }

    default boolean globalTeleportCheck(Entity entity, EntityPlayer player) {
        EntityAnimal animal = (EntityAnimal) entity;

        if (animal.getLeashed() || (animal.isBeingRidden() && animal.isRidingSameEntity(player))) {
            return false;
        }

        // And prevent you from summoning animals being ridden by other players
        if (animal.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            return false;
        }

        // Compatibility for Horse Power device-attached animals
        return !animal.hasHome() || animal.world.getTileEntity(animal.getHomePosition()) == null;
    }

    boolean isListable(Entity entity, EntityPlayer player);

    // Carrot
    boolean isTameable(Entity entity, EntityPlayer player);

    int tame(Entity entity, EntityPlayer player);

    boolean isAgeable(Entity entity, EntityPlayer player);

    int age(Entity entity, EntityPlayer player);

    default boolean isHealable(Entity entity, EntityPlayer player) {
        if (isMyMod(entity)) {
            EntityLiving horse = (EntityLiving) entity;
            return horse.getHealth() < horse.getMaxHealth();

        }

        return false;
    }

    default int heal(Entity entity, EntityPlayer player) {
        EntityLiving horse = (EntityLiving) entity;
        horse.heal(horse.getMaxHealth() - horse.getHealth());
        ParticleSender.generateParticles(horse, ParticleType.HEALING);

        if (DWMHConfig.client.clientCarrot.healing) {
            doGenericMessage(entity, player, MessageHandler.Generic.HEALING, null, null);
        }

        return 1;
    }

    // Not currently implemented
    boolean isBreedable(Entity entity, EntityPlayer player);

    int breed(Entity entity, EntityPlayer player);

    ITextComponent getResponseKey(Entity entity, EntityPlayer player);

    default boolean isLoaded() {
        return true;
    }

    default boolean isMyMod(Entity entity) {
        return false;
    }

    String proxyName();

    default ITextComponent getEntityTypeName(Entity entity, EntityPlayer player) {
        String entityKey = resolveEntityKey(String.format("entity.%s.name", EntityList.getEntityString(entity)));
        return new TextComponentTranslation(entityKey);
    }

    default String resolveEntityKey(String key) {
        return key;
    }

    default boolean onDismount(EntityMountEvent event) {
        if (event.getEntityBeingMounted() instanceof EntityCreature && event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && isMyMod(event.getEntityBeingMounted()) && DWMHConfig.Ocarina.home && !DWMHConfig.Ocarina.skipDismount) {
            ((EntityCreature) event.getEntityBeingMounted()).detachHome();
            return true;
        }

        return false;
    }

    // This... probably shouldn't be here
    default void doGenericMessage(Entity entity, EntityPlayer player, MessageHandler.Generic generic, @Nullable String keyOverride, @Nullable TextFormatting format) {
        String langKey;

        if (generic != null && !generic.isEmpty()) {
            langKey = generic.getLanguageKey();
        } else if (keyOverride != null && !keyOverride.isEmpty()) {
            langKey = keyOverride;
        } else {
            DWMH.LOG.error("DWMH: No valid language key passed for entity %s.", entity.getDisplayName().toString());
            return;
        }

        MessageHandler.sendGenericMessage(player, entity, generic, keyOverride, format);
    }

    default CapabilityOwner capability(Entity entity) {
        if (!entity.hasCapability(CapabilityOwnHandler.INSTANCE, null)) return null;

        return entity.getCapability(CapabilityOwnHandler.INSTANCE, null);
    }

    default boolean ownedBy(Entity entity, EntityPlayer player) {
        CapabilityOwner cap = capability(entity);
        if (cap == null) return false;

        if (cap.getOwner() == null) return false;

        return cap.getOwner().equals(player.getUniqueID());
    }

    default boolean hasOwner(Entity pig) {
        CapabilityOwner cap = capability(pig);

        if (cap == null) return false;

        return cap.hasOwner();
    }

    default boolean pseudoTaming(Entity entity) {
        return false;
    }

    default boolean pseudoTaming() {
        return false;
    }

    default boolean isPackAnimal (Entity entity, EntityPlayer player) {
        return false;
    }

    default void stopIt() {
    }

}
