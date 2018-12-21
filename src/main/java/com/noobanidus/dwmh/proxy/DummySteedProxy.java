package com.noobanidus.dwmh.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

public class DummySteedProxy implements ISteedProxy {
    public boolean isTeleportable (Entity entity, EntityPlayer player) {
        return false;
    }

    public boolean isListable (Entity entity, EntityPlayer player) {
        return false;
    }

    // Carrot
    public boolean isTameable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void tame (Entity entity, EntityPlayer player) {
    }

    public boolean isAgeable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void age (Entity entity, EntityPlayer player) {
    }

    public boolean isHealable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void heal (Entity entity, EntityPlayer player) {
    }

    // Not currently implemented
    public boolean isBreedable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void breed (Entity entity, EntityPlayer player) {
    }

    public boolean isLoaded () {
        return false;
    }

    public boolean isMyMod (Entity entity) {
        return false;
    }

    public ITextComponent getResponseKey (Entity entity, EntityPlayer player) {
         return null;
    }

    public String proxyName () {
        return "dummy";
    }
}
