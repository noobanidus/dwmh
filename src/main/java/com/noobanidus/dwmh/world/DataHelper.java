package com.noobanidus.dwmh.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Objects;

public class DataHelper {
  public static WorldServer getWorld () {
    return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
  }

  public static EntityData getTrackingData() {
    World world = getWorld();
    EntityData saveData = (EntityData) Objects.requireNonNull(world.getMapStorage()).getOrLoadData(EntityData.class, EntityData.id);

    if (saveData == null) {
      saveData = new EntityData();
      world.getMapStorage().setData(EntityData.id, saveData);
    }

    return saveData;
  }
}
