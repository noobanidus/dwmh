package com.noobanidus.dwmh.util;

import com.noobanidus.dwmh.ConfigHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class Eligibility {
  public static boolean eligibleToBeTagged(EntityPlayer player, Entity entity) {
    if (entity instanceof EntityPlayer) return false;

    UUID playerId = player.getUniqueID();
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

    if (entity.isCreatureType(EnumCreatureType.MONSTER, false) || entity instanceof IMob) {
      ResourceLocation rl = EntityList.getKey(entity);
      return ConfigHandler.getForcedEntities().contains(rl);
    }

    return true;
  }
}
