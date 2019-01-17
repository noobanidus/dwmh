package com.noobanidus.dwmh.proxy;

import com.google.common.collect.Lists;
import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.CreativeTabDWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.config.Registrar;
import com.noobanidus.dwmh.proxy.steeds.DummySteedProxy;
import com.noobanidus.dwmh.proxy.steeds.ISteedProxy;
import com.noobanidus.dwmh.proxy.steeds.SteedProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Optional;

public class CommonProxy implements ISidedProxy {
    public void preInit(FMLPreInitializationEvent event) {
        DWMH.TAB = new CreativeTabDWMH(CreativeTabs.getNextID(), DWMH.MODID);
        Registrar.preInit();
        DWMH.steedProxy = new SteedProxy();
    }

    public void init(FMLInitializationEvent e) {
    }

    @SuppressWarnings("unchecked")
    public void postInit(FMLPostInitializationEvent e) {
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

        DWMH.proxyList = Lists.newArrayList(DWMH.animaniaProxy, DWMH.mocProxy, DWMH.zawaProxy, DWMH.unicornProxy, DWMH.atum2Proxy, DWMH.iceandfireProxy, DWMH.vanillaProxy);
        DWMH.proxyList.removeIf(i -> !i.isLoaded());
        DWMH.resolveClasses();

        // Call these here to generate log error messages if necessary.
        Registrar.ocarina.checkRepairItem();
        Registrar.ocarina.checkCostItem();
        Registrar.carrot.checkRepairItem();
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
    }
}