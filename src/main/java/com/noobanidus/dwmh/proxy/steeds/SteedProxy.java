package com.noobanidus.dwmh.proxy.steeds;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.EntityMountEvent;

public class SteedProxy implements ISteedProxy {
    // The current lowest common denominator class for supported mocClasses
    public static Class<?> LOWEST_DENOMINATOR = EntityCreature.class;

    @Override
    public boolean hasCustomName(Entity entity) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.hasCustomName(entity);
            }
        }

        return false;
    }

    @Override
    public String getCustomNameTag(Entity entity) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.getCustomNameTag(entity);
            }
        }

        return "";
    }

    @Override
    public void setCustomNameTag(Entity entity, String name) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                proxy.setCustomNameTag(entity, name);
                break;
            }
        }
    }

    @Override
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isTeleportable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isListable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isListable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    // Carrot
    @Override
    public boolean isTameable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isTameable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int tame(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.tame(entity, player);
            }
        }

        return 0;
    }

    @Override
    public boolean isAgeable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isAgeable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int age(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.age(entity, player);
            }
        }

        return 0;
    }

    @Override
    public boolean isHealable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isHealable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int heal(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity) && !proxy.isTameable(entity, player)) {
                return proxy.heal(entity, player);
            }
        }

        return 0;
    }

    // Not currently implemented
    @Override
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isBreedable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int breed(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.breed(entity, player);
            }
        }

        return 0;
    }

    @Override
    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return null;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.getResponseKey(entity, player);
            }
        }

        return null;
    }

    // This may be sufficient to implement #5
    private boolean blacklisted(Entity entity) {
        return DWMH.entityBlacklist.contains(entity.getClass().getName());
    }

    @Override
    public boolean isMyMod(Entity entity) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onDismount(EntityMountEvent event) {
        if (blacklisted(event.getEntityBeingMounted())) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.onDismount(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ITextComponent getEntityTypeName(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.getEntityTypeName(entity, player);
            }
        }

        return null;
    }

    @Override
    public boolean pseudoTaming (Entity entity) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.pseudoTaming();
            }
        }

        return false;
    }

    @Override
    public boolean pseudoTaming () {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.pseudoTaming()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String proxyName() {
        return "main";
    }
}
