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

  public static void storeEntity(Entity entity, DimBlockPos location) {
    EntityData data = getData();
    data.lastKnownLocation.put(entity.getUniqueID(), location);
    save(data);
  }

  public static void setOwnerForEntity(PlayerEntity player, Entity entity) {
    EntityData data = getData();
    UUID playerId = player.getUniqueID();
    UUID entityId = entity.getUniqueID();
    data.entityToOwner.put(entityId, playerId);
    data.trackedEntities.add(entityId);
    save(data);
  }

  public static void unsetOwnerForEntity(Entity entity) {
    unsetOwnerForEntity(entity.getUniqueID());
  }

  public static void unsetOwnerForEntity (UUID entityId) {
    EntityData data = getData();
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

  @Nullable
  public static Entity fetchEntity(UUID uuid) {
    EntityData data = getData();
    Entity entity = findEntity(uuid);
    if (entity != null)  {
      return entity;
    }

    DimBlockPos pos = data.lastKnownLocation.get(uuid);
    if (pos != null) {
      return loadEntity(uuid, pos);
    }

    return null;
  }

  @Nullable
  public static Entity loadEntity(UUID uuid, DimBlockPos dimpos) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    ServerWorld dim = DimensionManager.getWorld(server, dimpos.getDim(), false, true);
    if (dim == null) {
      return null;
    }
    ChunkPos pos = new ChunkPos(dimpos.getPos());
    dim.forceChunk(pos.x, pos.z, true);
    clearMap.put(uuid, () -> dim.forceChunk(pos.x, pos.z, false));
    return dim.getEntityByUuid(uuid);
  }

  @Nullable
  public static Entity findEntity(UUID uuid) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    for (ServerWorld world : server.getWorlds()) {
      Entity potential = world.getEntityByUuid(uuid);
      if (potential != null) {
        return potential;
      }
    }

    return null;
  }

  public static void clearEntity(UUID entity) {
    Runnable runner = clearMap.remove(entity);
    if (runner != null) {
      runner.run();
    }
  }
}
