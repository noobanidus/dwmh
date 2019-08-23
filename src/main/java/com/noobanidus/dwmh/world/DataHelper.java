package com.noobanidus.dwmh.world;

import net.minecraft.world.World;

import java.util.Objects;

public class DataHelper {
  public static EntityData getTrackingData(World world) {
    EntityData saveData = (EntityData) Objects.requireNonNull(world.getMapStorage()).getOrLoadData(EntityData.class, EntityData.id);

    if (saveData == null) {
      saveData = new EntityData();
      world.getMapStorage().setData(EntityData.id, saveData);
    }

    return saveData;
  }
}
