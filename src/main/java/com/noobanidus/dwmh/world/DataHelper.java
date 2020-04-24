package com.noobanidus.dwmh.world;

import net.minecraft.world.WorldServer;

public class DataHelper {
  public static EntityData getTrackingData(WorldServer world) {
    return (EntityData) world.getMapStorage().getOrLoadData(EntityData.class, EntityData.id);
  }
}
