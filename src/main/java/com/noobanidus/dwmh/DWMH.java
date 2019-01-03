package com.noobanidus.dwmh;

import com.google.common.collect.Sets;
import com.noobanidus.dwmh.config.CreativeTabDWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.proxy.ISidedProxy;
import com.noobanidus.dwmh.proxy.steeds.DummySteedProxy;
import com.noobanidus.dwmh.proxy.steeds.ISteedProxy;
import com.noobanidus.dwmh.proxy.steeds.VanillaProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
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

    public static ISteedProxy vanillaProxy = new VanillaProxy();
    public static ISteedProxy animaniaProxy = new DummySteedProxy();
    public static ISteedProxy mocProxy = new DummySteedProxy();
    public static ISteedProxy zawaProxy = new DummySteedProxy();
    public static ISteedProxy unicornProxy = new DummySteedProxy();

    // This is more of an overall helper class that checks everything
    public static ISteedProxy steedProxy;

    public static List<ISteedProxy> proxyList;

    @SuppressWarnings("unused")
    private List<String> supportedMods = Arrays.asList("animania", "mocreatures", "zawa", "ultimate_unicorn_mod");

    public static Set<String> zawaClasses;
    public static Set<String> animaniaClasses;
    public static Set<String> ignoreList = Sets.newHashSet();
    public static Set<String> entityBlacklist;

    @SidedProxy (clientSide="com.noobanidus.dwmh.proxy.ClientProxy", serverSide="com.noobanidus.dwmh.proxy.CommonProxy")
    public static ISidedProxy proxy;

    @SuppressWarnings("unused")
    @Mod.Instance(DWMH.MODID)
    public static DWMH instance;

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
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    public static void resolveClasses () {
        // All of these are string-based now hooray
        DWMH.animaniaClasses = Sets.newHashSet(DWMHConfig.proxies.Animania.classes);
        DWMH.zawaClasses = Sets.newHashSet(DWMHConfig.proxies.ZAWA.classes);
        DWMH.entityBlacklist = Sets.newHashSet(DWMHConfig.blacklist);
        DWMH.ignoreList.clear();
    }
}
