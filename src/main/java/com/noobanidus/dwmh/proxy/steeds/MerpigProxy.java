package com.noobanidus.dwmh.proxy.steeds;

import com.builtbroken.merpig.entity.EntityMerpig;
import net.darkhax.resourcehogs.entity.EntityResourceHog;
import net.minecraft.entity.Entity;

public class MerpigProxy extends PigProxy {
    @Override
    public boolean isMyMod(Entity entity) {
        return entity instanceof EntityMerpig;
    }

    @Override
    public String proxyName() {
        return "Merpig";
    }
}
