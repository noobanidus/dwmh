package com.noobanidus.dwmh.init;

import com.noobanidus.dwmh.DWMH;
import net.minecraftforge.common.config.Config;

@Config(modid=DWMH.MODID)
@SuppressWarnings("unused")
public class ConfigHandler {
  @Config.Comment("Settings related to enabled recipes")
  @Config.Name("Recipe Settings")
  public static Recipes Recipes = new Recipes();

  public static class Recipes {
    @Config.Comment("Set to false to disable the Ocarina recipe")
    @Config.Name("Encable Ocarina recipe")
    public boolean enableOcarina = true;

    @Config.Comment("Set to false to disable the Saddle recipe")
    @Config.Name("Enable Saddle recipe")
    public boolean enableSaddle = true;
  }
}
