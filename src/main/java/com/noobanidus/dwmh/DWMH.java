package com.noobanidus.dwmh;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.noobanidus.dwmh.config.Registrar;
import com.noobanidus.dwmh.proxy.DummySteedProxy;
import com.noobanidus.dwmh.proxy.ISteedProxy;
import com.noobanidus.dwmh.proxy.SteedProxy;
import com.noobanidus.dwmh.proxy.VanillaProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber
@Mod(modid = DWMH.MODID, name = DWMH.MODNAME, version = DWMH.VERSION)
@SuppressWarnings("WeakerAccess")
public class DWMH {
    public static final String MODID = "dwmh";
    public static final String MODNAME = "Dude! Where's my Horse?";
    public static final String VERSION = "GRADLE:VERSION";

    // This may eventually be used.
    @SuppressWarnings("unused")
    public final static Logger LOG = LogManager.getLogger(MODID);

    public static Configuration CONFIG;
    public static CreativeTabDWMH TAB;

    public static ISteedProxy vanillaProxy = new VanillaProxy();
    public static ISteedProxy animaniaProxy = new DummySteedProxy();
    public static ISteedProxy mocProxy = new DummySteedProxy();
    public static ISteedProxy zawaProxy = new DummySteedProxy();

    // This is more of an overall helper class that checks everything
    public static ISteedProxy proxy;

    public static Iterable<ISteedProxy> proxyList;

    @Mod.Instance(DWMH.MODID)
    public static DWMH instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CONFIG = new Configuration(event.getSuggestedConfigurationFile(), true);
        TAB = new CreativeTabDWMH(CreativeTabs.getNextID(), MODID);
        Registrar.preInit();
        proxy = new SteedProxy();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
    }

    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        animaniaProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("animania", "com.noobanidus.dwmh.proxy.AnimaniaProxy")).orElse(new DummySteedProxy());
        mocProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("mocreatures", "com.noobanidus.dwmh.proxy.MOCProxy")).orElse(new DummySteedProxy());
        zawaProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("zawa", "com.noobanidus.dwmh.proxy.ZawaProxy")).orElse(new DummySteedProxy());

        proxyList = Iterables.filter(Arrays.asList(animaniaProxy, mocProxy, zawaProxy, vanillaProxy), ISteedProxy::isLoaded);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        CONFIG.save();
    }

    private final class CreativeTabDWMH extends CreativeTabs {
        public CreativeTabDWMH(int id, String id2) {
            super(id, id2);
        }

        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(Registrar.whistle);
        }
    }
}
