package com.noobanidus.dwmh.util;

import net.minecraft.entity.*;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class Eligibility {
  public static boolean eligibleToBeTagged (PlayerEntity player, Entity entity) {
    UUID playerId = player.getUniqueID();
    UUID ownedBy = EntityTracking.getOwnerForEntity(entity);
    if (ownedBy != null && !ownedBy.equals(playerId)) return false;

    if (entity instanceof TameableEntity) {
      TameableEntity tame = (TameableEntity) entity;
      if (tame.getOwnerId() != null && !tame.getOwnerId().equals(playerId)) return false;
    }
    if (entity instanceof AbstractHorseEntity) {
      AbstractHorseEntity tame = (AbstractHorseEntity) entity;
      if (tame.getOwnerUniqueId() != null && !tame.getOwnerUniqueId().equals(playerId)) return false;
    }
    if (entity instanceof LivingEntity && ((LivingEntity) entity).isChild()) return false; // This may or may not deal with children
    if (entity.getClassification(false) == EntityClassification.MONSTER) return false; // This may or may not be enough to deal with hostiles, but what about hostile mobs that become friendly?
    if (entity instanceof IMerchant) return false; // No villager adjacent
    if (entity instanceof IMob) return false; // No deliberate mobs? Might not work for Ice & Fire

    return true;
  }
}
