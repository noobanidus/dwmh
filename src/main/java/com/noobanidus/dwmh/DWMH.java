package com.noobanidus.dwmh;

import com.noobanidus.dwmh.init.ItemRegistry;
import com.noobanidus.dwmh.network.Networking;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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

  public static ChunkLoader LOADER = new ChunkLoader();

  public final static CreativeTabs TAB = new CreativeTabs("dwmh") {
    @Override
    public ItemStack createIcon() {
      return new ItemStack(ItemRegistry.OCARINA);
    }
  };

  @SuppressWarnings("unused")
  @Mod.Instance(DWMH.MODID)
  public static DWMH instance;

  public DWMH() {
    ForgeChunkManager.setForcedChunkLoadingCallback(this, LOADER);
  }

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    LOG = event.getModLog();
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    Networking.registerMessages();
  }
}
