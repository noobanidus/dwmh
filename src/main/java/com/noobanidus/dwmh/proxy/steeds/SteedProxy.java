package com.noobanidus.dwmh.proxy.steeds;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.EntityMountEvent;

public class SteedProxy implements ISteedProxy {
    public boolean hasCustomName(Entity entity) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.hasCustomName(entity);
            }
        }

        return false;
    }

    public String getCustomNameTag(Entity entity) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.getCustomNameTag(entity);
            }
        }

        return "";
    }

    public void setCustomNameTag(Entity entity, String name) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                proxy.setCustomNameTag(entity, name);
                break;
            }
        }
    }

    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isTeleportable(entity, player)) {
                return true;
            }
        }

        return false;
    }

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
    public boolean isTameable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isTameable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    public void tame(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                proxy.tame(entity, player);
                return;
            }
        }
    }

    public boolean isAgeable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isAgeable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    public void age(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                proxy.age(entity, player);
                return;
            }
        }
    }

    public boolean isHealable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isHealable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    public void heal(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                proxy.heal(entity, player);
                return;
            }
        }
    }

    // Not currently implemented
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isBreedable(entity, player)) {
                return true;
            }
        }

        return false;
    }

    public void breed(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                proxy.breed(entity, player);
                return;
            }
        }
    }

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
    public boolean blacklisted(Entity entity) {
        return DWMH.entityBlacklist.contains(entity.getClass());
    }

    public boolean isMyMod(Entity entity) {
        if (blacklisted(entity)) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return true;
            }
        }

        return false;
    }

    public boolean onDismount(EntityMountEvent event) {
        if (blacklisted(event.getEntityBeingMounted())) return false;

        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.onDismount(event)) {
                return true;
            }
        }

        return false;
    }

    public ITextComponent getEntityTypeName(Entity entity, EntityPlayer player) {
        for (ISteedProxy proxy : DWMH.proxyList) {
            if (proxy.isMyMod(entity)) {
                return proxy.getEntityTypeName(entity, player);
            }
        }

        return null;
    }


    public String proxyName() {
        return "main";
    }
}
