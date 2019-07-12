package com.noobanidus.dwmh.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
  @Override
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);
  }

  @Override
  public void init(FMLInitializationEvent e) {
    super.init(e);
  }

  @Override
  public void loadComplete(FMLLoadCompleteEvent event) {
    super.loadComplete(event);
  }
}