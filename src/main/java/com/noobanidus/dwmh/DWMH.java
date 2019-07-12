package com.noobanidus.dwmh;

import com.noobanidus.dwmh.init.ItemRegistry;
import com.noobanidus.dwmh.proxy.ISidedProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = DWMH.MODID, name = DWMH.MODNAME, version = DWMH.VERSION)
@SuppressWarnings("WeakerAccess")
public class DWMH {
  public static final String MODID = "dwmh";
  public static final String MODNAME = "Dude! Where's My Horse?";
  public static final String VERSION = "GRADLE:VERSION";

  public static Logger LOG;

  public final static CreativeTabs TAB = new CreativeTabs("dwmh") {
    @Override
    public ItemStack createIcon() {
      return new ItemStack(ItemRegistry.OCARINA);
    }
  };

  @SidedProxy(clientSide = "com.noobanidus.dwmh.proxy.ClientProxy", serverSide = "com.noobanidus.dwmh.proxy.CommonProxy")
  public static ISidedProxy proxy;

  @SuppressWarnings("unused")
  @Mod.Instance(DWMH.MODID)
  public static DWMH instance;

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    LOG = event.getModLog();
    proxy.preInit(event);
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    proxy.init(event);
  }

  @Mod.EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    proxy.postInit(event);
  }

  @Mod.EventHandler
  public void loadComplete(FMLLoadCompleteEvent event) {
    proxy.loadComplete(event);
  }
}
