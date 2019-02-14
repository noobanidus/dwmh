package com.noobanidus.dwmh;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.noobanidus.dwmh.config.CreativeTabDWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.proxy.DataStore;
import com.noobanidus.dwmh.proxy.ISidedProxy;
import com.noobanidus.dwmh.proxy.steeds.*;
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

    private static ISteedProxy dummy = new DummySteedProxy();

    public static ISteedProxy proxy (String proxyName) {
        if (proxyMap.containsKey(proxyName)) return proxyMap.get(proxyName);

        return DWMH.dummy;
    }

    public static Set<String> sets (String refId) {
        return DataStore.set(refId);
    }

    public static ISteedProxy steedProxy = new SteedProxy();

    public static List<ISteedProxy> proxyList;

    @SidedProxy(clientSide = "com.noobanidus.dwmh.proxy.ClientProxy", serverSide = "com.noobanidus.dwmh.proxy.CommonProxy")
    public static ISidedProxy proxy;
    @SuppressWarnings("unused")
    @Mod.Instance(DWMH.MODID)
    public static DWMH instance;

    public static void resolveClasses() {
        // All of these are string-based now hooray
        sets("animania").addAll(Arrays.asList(DWMHConfig.proxies.Animania.animaniaClasses));
        sets("zawa").addAll(Arrays.asList(DWMHConfig.proxies.ZAWA.zawaClasses));
        sets("blacklist").addAll(Arrays.asList(DWMHConfig.blacklist));
        sets("atum").addAll(Arrays.asList(DWMHConfig.proxies.Atum2.atum2Classes));
        sets("iceandfire").addAll(Arrays.asList(DWMHConfig.proxies.IceAndFire.iceandfireClasses));
        sets("iceandfire_exclusions").addAll(Arrays.asList(DWMHConfig.proxies.IceAndFire.iceandfireExclusions));
        sets("ignore").clear();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        DWMHConfig.instance.serialize();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unchecked")
    public void postInit(FMLPostInitializationEvent event) {
        for (DataStore.Proxy proxy : DataStore.get()) {
            if (!proxy.isEnabled()) continue;

            ISteedProxy instance = ((Optional<ISteedProxy>) event.buildSoftDependProxy(proxy.getModId(), proxy.getClassPath())).orElse(new DummySteedProxy());
            proxyMap.put(proxy.getModId(), instance);
        }

        proxyMap.put("vanilla", new VanillaProxy());
        proxyMap.put("vanilla_pig", new PigProxy());

        proxyList = Lists.newArrayList(proxyMap.values());

        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }
}
