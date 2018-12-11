package com.noobanidus.dwmh;

import com.noobanidus.dwmh.config.Registrar;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = DWMH.MODID, name = DWMH.MODNAME, version = DWMH.VERSION)
@SuppressWarnings("WeakerAccess")
public class DWMH {
    public static final String MODID = "dwmh";
    public static final String MODNAME = "Dude! Where's my Horse?";
    public static final String VERSION = "GRADLE:VERSION";

    public final static Logger LOG = LogManager.getLogger(MODID);

    public static Configuration CONFIG;
    public static CreativeTabDWMH TAB;

    @Mod.Instance(DWMH.MODID)
    public static DWMH instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CONFIG = new Configuration(event.getSuggestedConfigurationFile(), true);
        TAB = new CreativeTabDWMH(CreativeTabs.getNextID(), MODID);
        Registrar.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        LOG.info("DWMH: Load Complete.");
        CONFIG.save();
    }

    public final class CreativeTabDWMH extends CreativeTabs {
        public CreativeTabDWMH(int id, String id2) {
            super(id, id2);
        }

        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(Registrar.whistle);
        }
    }
}
