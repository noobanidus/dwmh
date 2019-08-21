package com.noobanidus.dwmh.util;

import com.noobanidus.dwmh.world.DataHelper;
import com.noobanidus.dwmh.world.EntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.*;

public class EntityTracking {
  @Nullable
  private static World getWorld() {
    Side side = FMLCommonHandler.instance().getEffectiveSide();
    if (side == Side.CLIENT) {
      return null;
    } else {
      return getServerWorld();
    }
  }

  private static World getServerWorld() {
    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    return server.getEntityWorld();
  }

  @Nullable
  public static EntityData getData() {
    World world = getWorld();
    if (world == null) return null;
    return DataHelper.getTrackingData(world);
  }

  public static int getEntityId(UUID uuid) {
    EntityData data = getData();
    if (data == null) return -1;

    return data.entityToId.getOrDefault(uuid, -1);
  }

  public static void setOwnerForEntity(EntityPlayer player, Entity entity) {
    EntityData data = getData();
    if (data == null) return;
    UUID playerId = player.getUniqueID();
    UUID entityId = entity.getUniqueID();
    int entityIntId = entity.getEntityId();
    data.entityToOwner.put(entityId, playerId);
    data.entityToId.put(entityId, entityIntId);
    data.trackedEntities.add(entityId);
    data.ownerToEntities.computeIfAbsent(playerId, o -> new HashSet<>()).add(entityId);
    save();
  }

  public static void unsetOwnerForEntity (EntityPlayer player, Entity entity) {
    EntityData data = getData();
    if (data == null) return;
    UUID playerId = player.getUniqueID();
    UUID entityId = entity.getUniqueID();
    data.entityToOwner.remove(entityId);
    data.trackedEntities.remove(entityId);
    Set<UUID> owned = data.ownerToEntities.get(playerId);
    if (owned != null) {
      owned.remove(entityId);
    }
    data.savedEntities.remove(entityId);
    data.storedEntities.remove(entityId);
    data.restoredEntities.remove(entityId);
  }

   public static void save() {
    EntityData data = getData();
    if (data == null) return;
    data.markDirty();
    World world = getWorld();
    if (world == null) return;
    Objects.requireNonNull(world.getMapStorage()).saveAllData();
  }

  @Nullable
  public static UUID getOwnerForEntity(Entity entity) {
    EntityData data = getData();
    if (data == null) return null;
    return data.entityToOwner.getOrDefault(entity.getUniqueID(), null);
  }

  public static boolean isTrackingEntity(UUID uuid) {
    EntityData data = getData();
    if (data == null) return false;
    return data.trackedEntities.contains(uuid);
  }

  public static void updateEntityId(Entity entity) {
    int id = entity.getEntityId();
    EntityData data = getData();
    if (data == null) return;

    data.entityToId.put(entity.getUniqueID(), id);
  }
}
