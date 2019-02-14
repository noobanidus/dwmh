package com.noobanidus.dwmh.proxy.steeds;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

public class DummySteedProxy implements ISteedProxy {
    @Override
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isListable(Entity entity, EntityPlayer player) {
        return false;
    }

    // Carrot
    @Override
    public boolean isTameable(Entity entity, EntityPlayer player) {
        return false;
    }

    @Override
    public int tame(Entity entity, EntityPlayer player) {
        return 0;
    }

    @Override
    public boolean isAgeable(Entity entity, EntityPlayer player) {
        return false;
    }

    @Override
    public int age(Entity entity, EntityPlayer player) {
        return 0;
    }

    @Override
    public boolean isHealable(Entity entity, EntityPlayer player) {
        return false;
    }

    @Override
    public int heal(Entity entity, EntityPlayer player) {
        return 0;
    }

    // Not currently implemented
    @Override
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        return false;
    }

    @Override
    public int breed(Entity entity, EntityPlayer player) {
        return 0;
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public boolean isMyMod(Entity entity) {
        return false;
    }

    @Override
    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        return null;
    }

    @Override
    public String proxyName() {
        return "dummy";
    }
}
