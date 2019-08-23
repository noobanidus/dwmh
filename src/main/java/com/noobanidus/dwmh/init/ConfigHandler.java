package com.noobanidus.dwmh.init;

import com.noobanidus.dwmh.DWMH;
import net.minecraftforge.common.config.Config;

@Config(modid = DWMH.MODID)
@SuppressWarnings("unused")
public class ConfigHandler {
  @Config.Comment("Set to false to disable the Saddle recipe")
  @Config.Name("Enable Saddle recipe")
  public static boolean enableSaddle = true;

  @Config.Comment("The total number of claimed entities a single player can have")
  @Config.Name("Claimed Entity Maximum")
  public static int entityMaximum = 10;
}
