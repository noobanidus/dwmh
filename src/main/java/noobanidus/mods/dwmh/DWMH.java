package noobanidus.mods.dwmh;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import noobanidus.mods.dwmh.config.ConfigManager;
import noobanidus.mods.dwmh.events.ClientEventHandler;
import noobanidus.mods.dwmh.events.EventHandler;
import noobanidus.mods.dwmh.init.ItemRegistry;
import noobanidus.mods.dwmh.setup.ModSetup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("dwmh")
public class DWMH {
  public static final String MODID = "dwmh";

  public static Logger LOG = LogManager.getLogger();

  public final static ItemGroup ITEM_GROUP = new ItemGroup("dwmh") {
    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack createIcon() {
      return new ItemStack(ItemRegistry.OCARINA);
    }
  };

  public static ModSetup setup = new ModSetup();
  public static EventHandler handler = new EventHandler();

  public DWMH() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    bus.addListener(setup::init);

    DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
      MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::onClientTick);
    });

    MinecraftForge.EVENT_BUS.register(handler);


    ConfigManager.init();
    ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
  }
}
