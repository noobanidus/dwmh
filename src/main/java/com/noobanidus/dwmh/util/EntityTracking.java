package com.noobanidus.dwmh.util;

import com.noobanidus.dwmh.ConfigHandler;
import com.noobanidus.dwmh.events.EventHandler;
import com.noobanidus.dwmh.world.DataHelper;
import com.noobanidus.dwmh.world.EntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EntityTracking {
  public static class WrongSideException extends RuntimeException {
    public WrongSideException(String message) {
      super(message);
    }
  }

  public static WorldServer getWorld() {
    if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
      throw new WrongSideException("Attempted to access server data on the client side!");
    }
    return getWorld(0);
  }

  public static WorldServer getWorld(int id) {
    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    return server.getWorld(id);
  }

  public static EntityData getData() {
    World world = getWorld();
    return DataHelper.getTrackingData(world);
  }

  public static boolean setOwnerForEntity(EntityPlayer player, Entity entity) {
    EntityData data = getData();
    UUID playerId = player.getUniqueID();
    UUID entityId = entity.getUniqueID();

    List<UUID> ownedEntities = data.ownerToEntities.computeIfAbsent(playerId, o -> new ArrayList<>());
    if (ownedEntities.size() < ConfigHandler.entityMaximum) {
      data.entityToOwner.put(entityId, playerId);
      data.trackedEntities.add(entityId);
      data.entityToResourceLocation.put(entityId, EntityList.getKey(entity));
      data.entityToName.put(entityId, translationKey(entity));
      ownedEntities.add(entityId);
      save();
      return true;
    } else {
      return false;
    }
  }

  public static int entityCount(EntityPlayer player) {
    EntityData data = getData();
    List<UUID> ownedEntities = data.ownerToEntities.computeIfAbsent(player.getUniqueID(), o -> new ArrayList<>());
    return ownedEntities.size();
  }

  @Nullable
  public static Entity fetchEntity(UUID entityId, WorldServer world, EntityPlayer player) {
    for (int dim : DimensionManager.getIDs()) {
      WorldServer w = getWorld(dim);
      Entity entity = w.getEntityFromUuid(entityId);
      if (entity != null) {
        return entity;
      }
    }

    EntityData data = getData();
    NBTTagCompound entityTag = data.savedEntities.get(entityId);
    if (entityTag == null) {
      return null;
    }

    ResourceLocation resource = data.entityToResourceLocation.get(entityId);
    if (resource == null) {
      return null;
    }

    Entity result = EntityList.createEntityByIDFromName(resource, world);
    if (result == null) {
      return null;
    }

    result.readFromNBT(entityTag);
    result.isDead = false;
    result.extinguish();
    result.dimension = world.provider.getDimension();
    result.hurtResistantTime = 0;

    if (result instanceof EntityLivingBase) {
      ((EntityLivingBase) result).setHealth(((EntityLivingBase) result).getMaxHealth());
    }

    result.setPosition(player.posX, player.posY, player.posZ);
    if (result instanceof EntityLiving) {
      EntityLiving el = (EntityLiving) result;
      el.getNavigator().clearPath();
    }

    EventHandler.isSpawning = true;
    player.world.spawnEntity(result);
    EventHandler.isSpawning = false;

    return result;
  }

  @Nullable
  public static UUID nextEntity(EntityPlayer player, @Nullable UUID currentlyTracked) {
    EntityData data = getData();
    List<UUID> ownedEntities = data.ownerToEntities.computeIfAbsent(player.getUniqueID(), o -> new ArrayList<>());
    if (ownedEntities.isEmpty()) {
      return null;
    }
    if (currentlyTracked == null) {
      return ownedEntities.get(0);
    }
    int index = ownedEntities.indexOf(currentlyTracked);
    if (index == -1) {
      return null;
    }
    if (index < (ownedEntities.size() - 1)) {
      return ownedEntities.get(index + 1);
    } else {
      return ownedEntities.get(0);
    }
  }

  public static boolean unsetOwnerForEntity(EntityPlayer player, Entity entity) {
    EntityData data = getData();
    UUID playerId = player.getUniqueID();
    UUID entityId = entity.getUniqueID();
    data.entityToOwner.remove(entityId);
    data.trackedEntities.remove(entityId);
    List<UUID> owned = data.ownerToEntities.get(playerId);
    if (owned != null) {
      owned.remove(entityId);
    }
    data.savedEntities.remove(entityId);
    data.storedEntities.remove(entityId);
    data.restoredEntities.remove(entityId);
    return true;
  }

  public static void save() {
    EntityData data = getData();
    data.markDirty();
    World world = getWorld();
    Objects.requireNonNull(world.getMapStorage()).saveAllData();
  }

  @Nullable
  public static UUID getOwnerForEntity(Entity entity) {
    EntityData data = getData();
    return data.entityToOwner.getOrDefault(entity.getUniqueID(), null);
  }

  public static String getName(UUID entityId) {
    EntityData data = getData();
    return data.entityToName.get(entityId);
  }

  public static NBTTagList getTrackedDataNBT(EntityPlayer player) {
    NBTTagList tag = new NBTTagList();
    EntityData data = getData();
    List<UUID> ownedEntities = data.ownerToEntities.get(player.getUniqueID());
    if (ownedEntities == null || ownedEntities.isEmpty()) {
      return tag;
    }

    for (UUID uuid : ownedEntities) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("entity", uuid);
      String name = data.entityToName.get(uuid);
      if (name == null) {
        thisEntry.setString("name", "");
      } else {
        thisEntry.setString("name", name);
      }
      tag.appendTag(thisEntry);
    }

    return tag;
  }

  private static String translationKey(Entity entity) {
    if (entity.hasCustomName()) {
      return entity.getCustomNameTag();
    } else {
      String s = EntityList.getEntityString(entity);

      if (s == null) {
        s = "generic";
      }

      return "entity." + s + ".name";
    }
  }

}
