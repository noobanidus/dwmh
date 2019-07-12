package com.noobanidus.dwmh.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface ISidedProxy {
  void preInit(FMLPreInitializationEvent event);

  void init(FMLInitializationEvent e);

  void postInit(FMLPostInitializationEvent e);

  void loadComplete(FMLLoadCompleteEvent event);
}
