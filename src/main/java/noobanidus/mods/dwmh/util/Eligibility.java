package noobanidus.mods.dwmh.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import noobanidus.mods.dwmh.config.ConfigManager;

import java.util.UUID;

public class Eligibility {
  private static UUID Kashcah = UUID.fromString("083c3cd5-9c94-40c7-a166-5692e4dc4b2c");
  private static UUID Vallen = UUID.fromString("564267c7-2ad2-4059-866a-6ca980b32777");

  public static boolean eligibleToBeTagged (PlayerEntity player, Entity entity) {
    UUID playerId = player.getUniqueID();
    UUID entityId = entity.getUniqueID();

    ResourceLocation type = entity.getType().getRegistryName();
    if (ConfigManager.blacklist.getEntityList().contains(type)) {
      return false;
    }

    if (ConfigManager.whitelist.getEntityList().contains(type)) {
      return true;
    }

    if (playerId.equals(Kashcah) && entityId.equals(Vallen)) {
      return true;
    }

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

    if (entity instanceof PlayerEntity) return false; // Not players... not yet.

    return true;
  }
}
