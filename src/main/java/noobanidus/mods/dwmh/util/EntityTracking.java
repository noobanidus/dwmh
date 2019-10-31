package noobanidus.mods.dwmh.util;

import noobanidus.mods.dwmh.world.DataHelper;
import noobanidus.mods.dwmh.world.EntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityTracking {
  private static ServerWorld getWorld(DimensionType dim) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    return server.getWorld(dim);
  }

  private static ServerWorld getWorld() {
    return getWorld(DimensionType.OVERWORLD);
  }

  private static EntityData getData() {
    ServerWorld world = getWorld();
    return DataHelper.getTrackingData(world);
  }

  public static int getEntityId(UUID uuid) {
    EntityData data = getData();
    return data.entityToId.getOrDefault(uuid, -1);
  }

  public static void setOwnerForEntity(PlayerEntity player, Entity entity) {
    EntityData data = getData();
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
  }
}
