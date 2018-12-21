package com.noobanidus.dwmh.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.zawamod.entity.base.ZAWABaseLand;
import org.zawamod.entity.land.*;

public class ZawaProxy implements ISteedProxy {
    public boolean isTeleportable (Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        return isSaddled((ZAWABaseLand) entity);
    }

    public boolean isListable (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) {
            return false;
        }

        ZAWABaseLand zawa = (ZAWABaseLand) entity;
        if (zawa.getOwnerId() == null || !zawa.getOwnerId().equals(player.getUniqueID())) {
            return false;
        }

        return rideableEntity(entity);
    }

    private boolean isSaddled (ZAWABaseLand entity) {
        NBTTagCompound nbt = new NBTTagCompound();
        entity.writeEntityToNBT(nbt);

        if (nbt.hasKey("Saddle")) return nbt.getBoolean("Saddle");

        return false;
    }

    private boolean rideableEntity (Entity entity) {
        if (entity instanceof EntityAsianElephant || entity instanceof EntityGaur || entity instanceof EntityGrevysZebra || entity instanceof EntityOkapi || entity instanceof EntityReticulatedGiraffe) {
            return true;
        }

        return false;
    }

    //  These do nothing
    public boolean isTameable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void tame (Entity entity, EntityPlayer player) { }

    public boolean isAgeable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void age (Entity entity, EntityPlayer player) { }

    // The healing is by default in the interface

    public boolean isBreedable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void breed (Entity entity, EntityPlayer player) {
    }

    public ITextComponent getResponseKey (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        ITextComponent temp;

        if (isSaddled((ZAWABaseLand) entity)) {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        } else {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled");
            temp.getStyle().setColor(TextFormatting.RED);
        }

        return temp;
    }

    public boolean isMyMod (Entity entity) {
        return rideableEntity(entity);
    }

    public String proxyName () {
        return "zawa";
    }
}


