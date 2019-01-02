package com.noobanidus.dwmh;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.noobanidus.dwmh.commands.ClientEntityCommand;
import com.noobanidus.dwmh.config.CreativeTabDWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.config.Registrar;
import com.noobanidus.dwmh.proxy.ISidedProxy;
import com.noobanidus.dwmh.proxy.steeds.DummySteedProxy;
import com.noobanidus.dwmh.proxy.steeds.ISteedProxy;
import com.noobanidus.dwmh.proxy.steeds.SteedProxy;
import com.noobanidus.dwmh.proxy.steeds.VanillaProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
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

    public static List<Class<?>> zawaClasses = new ArrayList<>();
    public static List<Class<?>> animaniaClasses = new ArrayList<>();
    public static Set<Class<? extends AbstractHorse>> ignoreList = Sets.newHashSet();
    public static Set<Class<?>> entityBlacklist = Sets.newHashSet();

    @SidedProxy (clientSide="com.noobanidus.dwmh.proxy.ClientProxy", serverSide="com.noobanidus.dwmh.proxy.CommonProxy")
    public static ISidedProxy proxy;

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
        if (Loader.isModLoaded("animania")) {
            DWMH.resolveClasses(DWMH.animaniaClasses, DWMHConfig.proxies.Animania.classes);
        }

        if (Loader.isModLoaded("zawa")) {
            DWMH.resolveClasses(DWMH.zawaClasses, DWMHConfig.proxies.ZAWA.classes);
        }

        DWMH.resolveClasses(DWMH.entityBlacklist, DWMHConfig.blacklist);
    }

    private static void resolveClasses(Collection<Class<?>> list, String[] classes) {
        list.clear();

        for (String c: classes) {
            try {
                Class clz = Class.forName(c);
                list.add(clz);
            } catch (ClassNotFoundException e) {
                DWMH.LOG.error(String.format("Could not find entity |%s|. The mod isn't loaded.", c));
            }
        }
    }
}
