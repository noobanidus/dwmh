package com.noobanidus.dwmh;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

import java.util.HashSet;
import java.util.Set;

@Config(modid = DWMH.MODID)
@SuppressWarnings("unused")
public class ConfigHandler {
  @Config.Comment("Set to false to disable the Saddle recipe")
  @Config.Name("Enable Saddle recipe")
  public static boolean enableSaddle = true;

  @Config.Comment("The total number of claimed entities a single player can have")
  @Config.Name("Claimed Entity Maximum")
  public static int entityMaximum = 10;

  @Config.Comment("A list of entities that should be considerable summonable regardless of their state (in the format of minecraft:horse, 1 per line)")
  @Config.Name("Forced Allowed Entities")
  public static String[] forcedEntities = new String[]{"primitivemobs:baby_spider", "minecraft:enderman"};

  @Config.Ignore
  private static Set<ResourceLocation> forcedEntityResources = null;

  public static Set<ResourceLocation> getForcedEntities() {
    if (forcedEntityResources == null) {
      forcedEntityResources = new HashSet<>();
      for (String rl : forcedEntities) {
        forcedEntityResources.add(new ResourceLocation(rl));
      }
    }
    return forcedEntityResources;
  }
}
