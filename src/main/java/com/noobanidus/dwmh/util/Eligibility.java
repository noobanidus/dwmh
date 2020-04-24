package com.noobanidus.dwmh.util;

import com.noobanidus.dwmh.ConfigHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class Eligibility {
  private static UUID Kashcah = UUID.fromString("083c3cd5-9c94-40c7-a166-5692e4dc4b2c");
  private static UUID Vallen = UUID.fromString("564267c7-2ad2-4059-866a-6ca980b32777");

  public static boolean eligibleToBeTagged(EntityPlayer player, Entity entity) {
    UUID playerId = player.getUniqueID();
    UUID entityId = entity.getUniqueID();

    if (playerId.equals(Kashcah) && entityId.equals(Vallen)) {
      return true;
    }

    ResourceLocation type = EntityList.getKey(entity);
    if (ConfigHandler.getBlacklist().contains(type)) {
      return false;
    }

    if (ConfigHandler.getWhitelist().contains(type)) {
      return true;
    }

    UUID ownedBy = EntityTracking.getOwnerForEntity(entity);
    if (ownedBy != null && !ownedBy.equals(playerId)) return false;

    if (entity instanceof EntityTameable) { // This covers Ice and Fire dragons
      EntityTameable tame = (EntityTameable) entity;
      if (tame.getOwnerId() != null && !tame.getOwnerId().equals(playerId)) return false;
    }
    if (entity instanceof AbstractHorse) {
      AbstractHorse tame = (AbstractHorse) entity;
      if (tame.getOwnerUniqueId() != null && !tame.getOwnerUniqueId().equals(playerId)) return false;
    }
    if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isChild()) {
      return false; // This may or may not deal with children
    }
    if (entity instanceof IMerchant) {
      return false; // No villager adjacent
    }
    if (entity instanceof EntityPlayer) {
      return false;
    }

    return !entity.isCreatureType(EnumCreatureType.MONSTER, false) && !(entity instanceof IMob);
  }
}
