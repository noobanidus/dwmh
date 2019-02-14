package com.noobanidus.dwmh.proxy;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.capability.CapabilityOwnHandler;
import com.noobanidus.dwmh.capability.CapabilityOwner;
import com.noobanidus.dwmh.config.CreativeTabDWMH;
import com.noobanidus.dwmh.config.Registrar;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy implements ISidedProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        DWMH.TAB = new CreativeTabDWMH(CreativeTabs.getNextID(), DWMH.MODID);
        Registrar.preInit();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        CapabilityManager.INSTANCE.register(CapabilityOwner.class, new CapabilityOwnHandler.CapabilityNameStorage(), CapabilityOwner::new);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        Registrar.ocarina.checkRepairItem();
        Registrar.ocarina.checkCostItem();
        Registrar.carrot.checkRepairItem();
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
    }
}