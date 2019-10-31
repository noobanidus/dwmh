package noobanidus.mods.dwmh.world;

import net.minecraft.world.server.ServerWorld;

public class DataHelper {
  public static EntityData getTrackingData(ServerWorld world) {
    return world.getSavedData().getOrCreate(EntityData::new, EntityData.id);
  }
}
