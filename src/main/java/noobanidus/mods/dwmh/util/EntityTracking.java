package noobanidus.mods.dwmh.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.dwmh.types.DimBlockPos;
import noobanidus.mods.dwmh.world.DataHelper;
import noobanidus.mods.dwmh.world.EntityData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityTracking {
  public static Map<UUID, Runnable> clearMap = new HashMap<>();

  private static ServerWorld getWorld(DimensionType dim, boolean load) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    return DimensionManager.getWorld(server, dim, false, load);
  }

  private static ServerWorld getWorld() {
    return getWorld(DimensionType.OVERWORLD, true);
  }

  public static EntityData getData() {
    ServerWorld world = getWorld();
    return DataHelper.getTrackingData(world);
  }

  public static int getEntityId(UUID uuid) {
    EntityData data = getData();
    return data.entityToId.getOrDefault(uuid, -1);
  }

  public static void storeEntity(Entity entity, DimBlockPos location) {
    EntityData data = getData();
    data.lastKnownLocation.put(entity.getUniqueID(), location);
    save(data);
  }

  public static void setOwnerForEntity(PlayerEntity player, Entity entity) {
    EntityData data = getData();
    UUID playerId = player.getUniqueID();
    UUID entityId = entity.getUniqueID();
    int entityIntId = entity.getEntityId();
    data.entityToOwner.put(entityId, playerId);
    data.entityToId.put(entityId, entityIntId);
    data.trackedEntities.add(entityId);
    save(data);
  }

  public static void unsetOwnerForEntity(Entity entity) {
    EntityData data = getData();
    UUID entityId = entity.getUniqueID();
    data.entityToId.removeInt(entityId);
    data.entityToOwner.remove(entityId);
    data.trackedEntities.remove(entityId);
    save(data);
  }

  public static void save(EntityData data) {
    data.markDirty();
    ServerWorld world = getWorld();
    world.getSavedData().save();
  }

  @Nullable
  public static UUID getOwnerForEntity(Entity entity) {
    EntityData data = getData();
    return data.entityToOwner.getOrDefault(entity.getUniqueID(), null);
  }

  public static boolean isTrackingEntity(UUID uuid) {
    EntityData data = getData();
    return data.trackedEntities.contains(uuid);
  }

  public static void updateEntityId(Entity entity) {
    int id = entity.getEntityId();
    EntityData data = getData();
    data.entityToId.put(entity.getUniqueID(), id);
    save(data);
  }

  @Nullable
  public static Entity fetchEntity(UUID uuid) {
    EntityData data = getData();
    ServerWorld overworld = getWorld();
    int intId = data.entityToId.getInt(uuid);
    Entity entity = overworld.getEntityByID(intId);
    if (entity != null && entity.getUniqueID().equals(uuid)) {
      return entity;
    }

    DimBlockPos pos = data.lastKnownLocation.get(uuid);
    if (pos != null) {
      return loadEntity(uuid, pos);
    }

    return findEntity(uuid);
  }

  @Nullable
  public static Entity loadEntity(UUID uuid, DimBlockPos dimpos) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    ServerWorld dim = DimensionManager.getWorld(server, dimpos.getDim(), false, true);
    if (dim == null) {
      return null;
    }
    ChunkPos pos = new ChunkPos(dimpos.getPos());
    dim.getChunk(pos.x, pos.z);
    dim.forceChunk(pos.x, pos.z, true);
    clearMap.put(uuid, () -> dim.forceChunk(pos.x, pos.z, false));
    return findEntity(dim, uuid);
  }

  @Nullable
  public static Entity findEntity(UUID uuid) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    for (ServerWorld world : server.getWorlds()) {
      Entity potential = findEntity(world, uuid);
      if (potential != null) {
        return potential;
      }
    }

    return null;
  }

  @Nullable
  public static Entity findEntity(ServerWorld world, UUID uuid) {
    return world.getEntities().filter(o -> o.getUniqueID().equals(uuid)).findFirst().orElse(null);
  }

  public static void clearEntity(UUID entity) {
    Runnable runner = clearMap.remove(entity);
    if (runner != null) {
      runner.run();
    }
  }
}
