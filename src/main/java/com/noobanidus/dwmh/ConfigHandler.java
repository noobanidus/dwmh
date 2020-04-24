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

  @Config.Comment("A whitelist of entities that should be considerable summonable regardless of their state (in the format of minecraft:horse, 1 per line)")
  @Config.Name("Whitelisted Entities")
  public static String[] whitelistRaw = new String[]{"primitivemobs:baby_spider"};

  @Config.Ignore
  private static Set<ResourceLocation> whitelist = null;

  public static Set<ResourceLocation> getWhitelist() {
    if (whitelist == null) {
      whitelist = new HashSet<>();
      for (String rl : whitelistRaw) {
        whitelist.add(new ResourceLocation(rl));
      }
    }
    return whitelist;
  }

  @Config.Comment("A blacklist of entities that should NOT be considerable summonable regardless of their state (in the format of minecraft:horse, 1 per line)")
  @Config.Name("Whitelisted Entities")
  public static String[] blacklistRaw = new String[]{""};

  @Config.Ignore
  private static Set<ResourceLocation> blacklist = null;

  public static Set<ResourceLocation> getBlacklist() {
    if (blacklist == null) {
      blacklist = new HashSet<>();
      for (String rl : blacklistRaw) {
        blacklist.add(new ResourceLocation(rl));
      }
    }
    return blacklist;
  }
}
