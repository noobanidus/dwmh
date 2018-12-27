package com.noobanidus.dwmh.proxy;

import com.noobanidus.dwmh.items.ItemWhistle;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.passive.MoCEntityElephant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

// Instantiated by buildSoftDependProxy if Mo' Creatures is installed
@SuppressWarnings("unused")
public class MOCProxy implements ISteedProxy {
    public boolean hasCustomName (Entity entity) {
        if (!isMyMod(entity)) return false;

        MoCEntityTameableAnimal animal = (MoCEntityTameableAnimal) entity;

        return !animal.getPetName().equals("");
    }

    public String getCustomNameTag (Entity entity) {
        if (!isMyMod(entity)) return entity.getCustomNameTag();

        return ((MoCEntityTameableAnimal) entity).getPetName();
    }

    public void setCustomNameTag (Entity entity, String name) {
        if (!isMyMod(entity)) {
            entity.setCustomNameTag(name);
        } else {
            ((MoCEntityTameableAnimal) entity).setPetName(name);
        }
    }

    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        MoCEntityTameableAnimal animal = (MoCEntityTameableAnimal) entity;

        // I guess elephants DO have a special "saddle".
        if (animal instanceof MoCEntityElephant) {
            return animal.getArmorType() >= 1 && globalTeleportCheck(entity, player);
        } else {
            return animal.getIsRideable() && globalTeleportCheck(entity, player);
        }
    }

    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) {
            return false;
        }

        MoCEntityTameableAnimal animal = (MoCEntityTameableAnimal) entity;

        return animal.getOwnerId() != null && animal.getOwnerId().equals(player.getUniqueID());
    }

    // Carrot
    public boolean isTameable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) {
            return false;
        }

        MoCEntityTameableAnimal animal = (MoCEntityTameableAnimal) entity;
        return !animal.getIsTamed();
    }

    public void tame(Entity entity, EntityPlayer player) {
        MoCEntityTameableAnimal animal = (MoCEntityTameableAnimal) entity;
        animal.setTamed(true);
        MoCTools.tameWithName(player, animal);
    }

    public boolean isAgeable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        return !((MoCEntityTameableAnimal) entity).getIsAdult();
    }

    public void age(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return;

        MoCEntityTameableAnimal animal = (MoCEntityTameableAnimal) entity;

        if (animal.getIsAdult()) return;

        animal.setAdult(true);
        animal.setEdad(animal.getMaxEdad());
        animal.setType(0);
        animal.selectType();
    }

    // Not currently implemented
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        return false;
    }

    public void breed(Entity entity, EntityPlayer player) {
    }

    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        MoCEntityTameableAnimal animal = (MoCEntityTameableAnimal) entity;

        ITextComponent temp;

        if (!animal.getIsAdult()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.child");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.hasHome() && animal.world.getTileEntity(animal.getHomePosition()) != null) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.working");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.getLeashed()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.leashed");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (!(animal instanceof MoCEntityElephant) && !animal.getIsRideable() || (animal instanceof MoCEntityElephant && animal.getArmorType() == 0)) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.isBeingRidden() && animal.isRidingSameEntity(player)) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.isBeingRidden() && !ItemWhistle.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.isBeingRidden() && ItemWhistle.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.summonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_AQUA);
        } else {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        }

        return temp;
    }

    public boolean isMyMod (Entity entity) {
        if (entity instanceof MoCEntityTameableAnimal) {
            MoCEntityTameableAnimal animal = (MoCEntityTameableAnimal) entity;
            return animal.rideableEntity();
        }

        return false;
    }

    public String proxyName () {
        return "MOC";
    }
}


