package com.noobanidus.dwmh.proxy.steeds;

import net.darkhax.resourcehogs.entity.EntityResourceHog;
import net.minecraft.entity.Entity;

public class HogProxy extends PigProxy {
    @Override
    public boolean isMyMod(Entity entity) {
        return entity instanceof EntityResourceHog;
    }

    @Override
    public String proxyName() {
        return "ResourceHogs";
    }
}
