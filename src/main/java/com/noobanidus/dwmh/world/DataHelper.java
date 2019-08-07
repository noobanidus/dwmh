package com.noobanidus.dwmh.world;

import net.minecraft.world.World;

public class DataHelper {
  public static EntityData getTrackingData(World world) {
    EntityData saveData = (EntityData) world.getMapStorage().getOrLoadData(EntityData.class, EntityData.id);

    if (saveData == null) {
      saveData = new EntityData();
      world.getMapStorage().setData(EntityData.id, saveData);
    }

    return saveData;
  }
}
