package com.noobanidus.dwmh;

import com.noobanidus.dwmh.init.ItemRegistry;
import com.noobanidus.dwmh.setup.ModSetup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("dwmh")
public class DWMH {
  public static final String MODID = "dwmh";
  public static final String MODNAME = "Dude! Where's My Horse?";
  public static final String VERSION = "GRADLE:VERSION";

  public static Logger LOG = LogManager.getLogger();

  public final static ItemGroup ITEM_GROUP = new ItemGroup("dwmh") {
    @Override
    public ItemStack createIcon() {
      return new ItemStack(ItemRegistry.OCARINA);
    }
  };

  public static ModSetup setup = new ModSetup();

  public DWMH() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
  }
}
