package com.noobanidus.dwmh;

import com.google.common.collect.Sets;
import com.noobanidus.dwmh.config.CreativeTabDWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.proxy.ISidedProxy;
import com.noobanidus.dwmh.proxy.ProxyList;
import com.noobanidus.dwmh.proxy.steeds.DummySteedProxy;
import com.noobanidus.dwmh.proxy.steeds.ISteedProxy;
import com.noobanidus.dwmh.proxy.steeds.SteedProxy;
import com.noobanidus.dwmh.proxy.steeds.VanillaProxy;
import it.unimi.dsi.fastutil.Hash;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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

    public final static Logger LOG = LogManager.getLogger(MODID);

    public static CreativeTabDWMH TAB;

    public static Map<String, ISteedProxy> proxyMap = new HashMap<>();

    public static ISteedProxy vanillaProxy = new VanillaProxy();
    public static ISteedProxy animaniaProxy = new DummySteedProxy();
    public static ISteedProxy mocProxy = new DummySteedProxy();
    public static ISteedProxy zawaProxy = new DummySteedProxy();
    public static ISteedProxy unicornProxy = new DummySteedProxy();
    public static ISteedProxy atum2Proxy = new DummySteedProxy();
    public static ISteedProxy iceandfireProxy = new DummySteedProxy();
    public static ISteedProxy dragonProxy = new DummySteedProxy();
    public static ISteedProxy varoddProxy = new DummySteedProxy();
    public static ISteedProxy moolandProxy = new DummySteedProxy();

    // This is more of an overall helper class that checks everything
    public static ISteedProxy steedProxy = new SteedProxy();

    public static List<ISteedProxy> proxyList = new ArrayList<>();
    public static Set<String> zawaClasses;
    public static Set<String> animaniaClasses;
    public static Set<String> atum2Classes;
    public static Set<String> iceandfireClasses;
    public static Set<String> iceandFireExclusions;
    public static Set<String> ignoreList = Sets.newHashSet();
    public static Set<String> entityBlacklist;
    @SidedProxy(clientSide = "com.noobanidus.dwmh.proxy.ClientProxy", serverSide = "com.noobanidus.dwmh.proxy.CommonProxy")
    public static ISidedProxy proxy;
    @SuppressWarnings("unused")
    @Mod.Instance(DWMH.MODID)
    public static DWMH instance;
    @SuppressWarnings("unused")
    private List<String> supportedMods = Arrays.asList("animania", "mocreatures", "zawa", "ultimate_unicorn_mod", "atum", "dragonmounts", "varodd", "moolands");

    public static void resolveClasses() {
        // All of these are string-based now hooray
        DWMH.animaniaClasses = Sets.newHashSet(DWMHConfig.proxies.Animania.classes);
        DWMH.zawaClasses = Sets.newHashSet(DWMHConfig.proxies.ZAWA.classes);
        DWMH.entityBlacklist = Sets.newHashSet(DWMHConfig.blacklist);
        DWMH.atum2Classes = Sets.newHashSet(DWMHConfig.proxies.Atum2.classes);
        DWMH.iceandfireClasses = Sets.newHashSet(DWMHConfig.proxies.IceAndFire.classes);
        DWMH.iceandFireExclusions = Sets.newHashSet(DWMHConfig.proxies.IceAndFire.exclusions);
        DWMH.ignoreList.clear();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        for (ProxyList.Proxy entry : ProxyList.get()) {

        }
         if (DWMHConfig.proxies.enable.animania) {
            DWMH.animaniaProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("animania", "com.noobanidus.dwmh.proxy.steeds.AnimaniaProxy")).orElse(new DummySteedProxy());
        }
        if (DWMHConfig.proxies.enable.mocreatures) {
            DWMH.mocProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("mocreatures", "com.noobanidus.dwmh.proxy.steeds.MOCProxy")).orElse(new DummySteedProxy());
            if (Loader.isModLoaded("mocreatures")) {
                MinecraftForge.EVENT_BUS.register(DWMH.mocProxy.getClass());
            }
        }
        if (DWMHConfig.proxies.enable.zawa) {
            DWMH.zawaProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("zawa", "com.noobanidus.dwmh.proxy.steeds.ZawaProxy")).orElse(new DummySteedProxy());
        }
        if (DWMHConfig.proxies.enable.ultimate_unicorn_mod) {
            DWMH.unicornProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("ultimate_unicorn_mod", "com.noobanidus.dwmh.proxy.steeds.UnicornProxy")).orElse(new DummySteedProxy());
            if (Loader.isModLoaded("ultimate_unicorn_mod")) {
                MinecraftForge.EVENT_BUS.register(DWMH.unicornProxy.getClass());
            }
        }
        if (DWMHConfig.proxies.enable.atum2) {
            DWMH.atum2Proxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("atum", "com.noobanidus.dwmh.proxy.steeds.Atum2Proxy")).orElse(new DummySteedProxy());
        }
        if (DWMHConfig.proxies.enable.iceandfire) {
            DWMH.iceandfireProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("iceandfire", "com.noobanidus.dwmh.proxy.steeds.IceAndFireProxy")).orElse(new DummySteedProxy());
        }
        if (DWMHConfig.proxies.enable.dragon) {
            DWMH.dragonProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("dragonmounts", "com.noobanidus.dwmh.proxy.steeds.DragonMountProxy")).orElse(new DummySteedProxy());
            if (Loader.isModLoaded("dragonmounts")) {
                DWMH.dragonProxy.stopIt();
            }
        }
        if (DWMHConfig.proxies.enable.varodd) {
            DWMH.varoddProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("varodd", "com.noobanidus.dwmh.proxy.steeds.VaroddProxy")).orElse(new DummySteedProxy());
        }
        if (DWMHConfig.proxies.enable.moolands) {
            DWMH.moolandProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("moolands", "com.noobanidus.dwmh.proxy.steeds.MoolandProxy")).orElse(new DummySteedProxy());
        }

        DWMH.proxyList.addAll(Arrays.asList(DWMH.animaniaProxy, DWMH.mocProxy, DWMH.zawaProxy, DWMH.unicornProxy, DWMH.atum2Proxy, DWMH.iceandfireProxy, DWMH.dragonProxy, DWMH.varoddProxy, DWMH.moolandProxy, DWMH.vanillaProxy));
        DWMH.proxyList.removeIf(i -> !i.isLoaded());
        DWMH.resolveClasses();

        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }
}
