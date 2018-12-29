package com.noobanidus.dwmh;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.config.Registrar;
import com.noobanidus.dwmh.proxy.DummySteedProxy;
import com.noobanidus.dwmh.proxy.ISteedProxy;
import com.noobanidus.dwmh.proxy.SteedProxy;
import com.noobanidus.dwmh.proxy.VanillaProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
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

    public final static Logger LOG = LogManager.getLogger(MODID);

    public static CreativeTabDWMH TAB;

    public static ISteedProxy vanillaProxy = new VanillaProxy();
    public static ISteedProxy animaniaProxy = new DummySteedProxy();
    public static ISteedProxy mocProxy = new DummySteedProxy();
    public static ISteedProxy zawaProxy = new DummySteedProxy();
    public static ISteedProxy unicornProxy = new DummySteedProxy();

    // This is more of an overall helper class that checks everything
    public static ISteedProxy proxy;

    public static List<ISteedProxy> proxyList;

    @SuppressWarnings("unused")
    private List<String> supportedMods = Arrays.asList("animania", "mocreatures", "zawa", "ultimate_unicorn_mod");

    public static List<Class<?>> zawaClasses = new ArrayList<>();
    public static List<Class<?>> animaniaClasses = new ArrayList<>();
    public static Set<Class<? extends AbstractHorse>> ignoreList = Sets.newHashSet();
    public static Set<Class<?>> entityBlacklist = Sets.newHashSet();

    @Mod.Instance(DWMH.MODID)
    public static DWMH instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
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
        if (DWMHConfig.proxies.enable.animania) {
            animaniaProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("animania", "com.noobanidus.dwmh.proxy.AnimaniaProxy")).orElse(new DummySteedProxy());
        }
        if (DWMHConfig.proxies.enable.mocreatures) {
            mocProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("mocreatures", "com.noobanidus.dwmh.proxy.MOCProxy")).orElse(new DummySteedProxy());
            if (Loader.isModLoaded("mocreatures")) {
                MinecraftForge.EVENT_BUS.register(mocProxy.getClass());
            }
        }
        if (DWMHConfig.proxies.enable.zawa) {
            zawaProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("zawa", "com.noobanidus.dwmh.proxy.ZawaProxy")).orElse(new DummySteedProxy());
            if (Loader.isModLoaded("zawa")) {
                ModContainer zawa = Loader.instance().getIndexedModList().get("zawa");
                if (!zawa.getVersion().equals("1.12.2-1.4.0")) {
                    zawaProxy = new DummySteedProxy();
                    DWMH.LOG.error("ZAWA is only supported for version 1.4.0. ZAWA compatibility has been disabled");
                }
            }
        }
        if (DWMHConfig.proxies.enable.ultimate_unicorn_mod) {
            unicornProxy = ((Optional<ISteedProxy>) e.buildSoftDependProxy("ultimate_unicorn_mod", "com.noobanidus.dwmh.proxy.UnicornProxy")).orElse(new DummySteedProxy());
            if (Loader.isModLoaded("ultimate_unicorn_mod")) {
                MinecraftForge.EVENT_BUS.register(unicornProxy.getClass());
            }
        }

        proxyList = Lists.newArrayList(animaniaProxy, mocProxy, zawaProxy, unicornProxy, vanillaProxy);
        proxyList.removeIf(i -> !i.isLoaded());

        if (Loader.isModLoaded("animania")) {
            DWMH.resolveClasses(DWMH.animaniaClasses, DWMHConfig.proxies.Animania.classes);
        }

        if (Loader.isModLoaded("zawa")) {
            DWMH.resolveClasses(DWMH.zawaClasses, DWMHConfig.proxies.ZAWA.classes);
        }

        DWMH.resolveClasses(DWMH.entityBlacklist, DWMHConfig.proxies.blacklist);
    }

    private static void resolveClasses(Collection<Class<?>> list, String[] classes) {
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
    }

    private final class CreativeTabDWMH extends CreativeTabs {
        public CreativeTabDWMH(int id, String id2) {
            super(id, id2);
        }

        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(Registrar.ocarina);
        }
    }
}
