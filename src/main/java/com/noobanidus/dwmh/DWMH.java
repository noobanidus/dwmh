package com.noobanidus.dwmh;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.noobanidus.dwmh.config.Registrar;
import com.noobanidus.dwmh.proxy.DummySteedProxy;
import com.noobanidus.dwmh.proxy.ISteedProxy;
import com.noobanidus.dwmh.proxy.SteedProxy;
import com.noobanidus.dwmh.proxy.VanillaProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

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

    private Map<String, Boolean> proxyMap;
    private List<String> supportedMods = Arrays.asList("animania", "mocreatures", "zawa");

    public static List<Class<?>> zawaClasses = new ArrayList<>();
    public static List<Class<?>> animaniaClasses = new ArrayList<>();
    public static Set<Class<? extends AbstractHorse>> ignoreList = Sets.newHashSet();

    @Mod.Instance(DWMH.MODID)
    public static DWMH instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CONFIG = new Configuration(event.getSuggestedConfigurationFile(), true);
        TAB = new CreativeTabDWMH(CreativeTabs.getNextID(), MODID);
        Registrar.preInit();
        proxy = new SteedProxy();

        // set up proxy config
        proxyMap = new HashMap<>();
        for (String mod : supportedMods) {
            proxyMap.put(mod, CONFIG.get("Proxy", mod, true, String.format("Set to false to permanently disable compatibility with %s.", mod)).getBoolean(true));
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
    }

    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        if (proxyMap.get("animania")) {
            animaniaProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("animania", "com.noobanidus.dwmh.proxy.AnimaniaProxy")).orElse(new DummySteedProxy());
        }
        if (proxyMap.get("mocreatures")) {
            mocProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("mocreatures", "com.noobanidus.dwmh.proxy.MOCProxy")).orElse(new DummySteedProxy());
        }
        if (proxyMap.get("zawa")) {
            zawaProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("zawa", "com.noobanidus.dwmh.proxy.ZawaProxy")).orElse(new DummySteedProxy());
        }

        proxyList = Iterables.filter(Arrays.asList(animaniaProxy, mocProxy, zawaProxy, vanillaProxy), ISteedProxy::isLoaded);

        String[] animaniaConfigClasses = CONFIG.get("Animania", "HorsesClasses", new String[]{"com.animania.common.entities.horses.EntityMareBase", "com.animania.common.entities.horses.EntityStallionBase"}, "Specify list of Animania classes that are considered horses.").getStringList();

        if (Loader.isModLoaded("animania")) {
            DWMH.resolveClasses(DWMH.animaniaClasses, animaniaConfigClasses);
        }

        String[] zawaConfigClasses = CONFIG.get("ZAWA", "HorsesClasses", new String[]{"org.zawamod.entity.land.EntityAsianElephant", "org.zawamod.entity.land.EntityGaur", "org.zawamod.entity.land.EntityGrevysZebra", "org.zawamod.entity.land.EntityOkapi", "org.zawamod.entity.land.EntityReticulatedGiraffe"}, "Specify list of ZAWA classes that are considered rideable or horses.").getStringList();

        if (Loader.isModLoaded("zawa")) {
            DWMH.resolveClasses(DWMH.zawaClasses, zawaConfigClasses);
        }
    }

    private static void resolveClasses(List<Class<?>> list, String[] classes) {
        for (String c: classes) {
            try {
                Class clz = Class.forName(c);
                list.add(clz);
            } catch (ClassNotFoundException e) {
                DWMH.LOG.error(String.format("Could not find entity |%s|. The mod isn't loaded.", c));
            }
        }
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
