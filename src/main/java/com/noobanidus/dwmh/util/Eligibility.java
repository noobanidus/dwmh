package com.noobanidus.dwmh.util;

import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Objects;
import java.util.UUID;

public class Eligibility {
  public static boolean eligibleToBeTagged (EntityPlayer player, Entity entity) {
    UUID playerId = player.getUniqueID();
    UUID ownedBy = EntityTracking.getOwnerForEntity(entity);
    if (ownedBy != null && !ownedBy.equals(playerId)) return false;

    if (entity instanceof EntityTameable) {
      EntityTameable tame = (EntityTameable) entity;
      if (tame.getOwnerId() != null && !tame.getOwnerId().equals(playerId)) return false;
    }
    if (entity instanceof AbstractHorse) {
      AbstractHorse tame = (AbstractHorse) entity;
      if (tame.getOwnerUniqueId() != null && !tame.getOwnerUniqueId().equals(playerId)) return false;
    }
    if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isChild()) return false; // This may or may not deal with children
    if (entity.isCreatureType(EnumCreatureType.MONSTER, false)) return false; // This may or may not be enough to deal with hostiles, but what about hostile mobs that become friendly?
    if (entity instanceof IMerchant) return false; // No villager adjacent
    if (entity instanceof IMob) return false; // No deliberate mobs? Might not work for Ice & Fire

    return true;
  }
}
