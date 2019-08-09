package com.noobanidus.dwmh.util;

import com.noobanidus.dwmh.world.DataHelper;
import com.noobanidus.dwmh.world.EntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityTracking {
  @Nullable
  private static ServerWorld getWorld() {
    return getServerWorld();
  }

  private static ServerWorld getServerWorld() {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    return server.getWorld(DimensionType.OVERWORLD);
  }

  @Nullable
  private static EntityData getData() {
    ServerWorld world = getWorld();
    if (world == null) return null;
    return DataHelper.getTrackingData(world);
  }

  public static int getEntityId(UUID uuid) {
    EntityData data = getData();
    if (data == null) return -1;

    return data.entityToId.getOrDefault(uuid, -1);
  }

  public static void setOwnerForEntity(PlayerEntity player, Entity entity) {
    EntityData data = getData();
    if (data == null) return;
    UUID playerId = player.getUniqueID();
    UUID entityId = entity.getUniqueID();
    int entityIntId = entity.getEntityId();
    data.entityToOwner.put(entityId, playerId);
    data.entityToId.put(entityId, entityIntId);
    data.trackedEntities.add(entityId);
    save();
  }

  public static void save() {
    EntityData data = getData();
    if (data == null) return;
    data.markDirty();
    ServerWorld world = getWorld();
    if (world == null) return;
    world.getSavedData().save();
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
