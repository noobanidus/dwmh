package com.noobanidus.dwmh.proxy.steeds;

import com.animania.common.entities.horses.EntityAnimaniaHorse;
import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.capability.CapabilityName;
import com.noobanidus.dwmh.capability.CapabilityNameHandler;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

// Instantiated by buildSoftDependProxy
@SuppressWarnings("unused")
public class AnimaniaProxy implements ISteedProxy {
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        EntityAnimaniaHorse horse = (EntityAnimaniaHorse) entity;

        if (!globalTeleportCheck(entity, player)) return false;

        if (!horse.hasCapability(CapabilityNameHandler.INSTANCE, null)) return false;

        CapabilityName cap = horse.getCapability(CapabilityNameHandler.INSTANCE, null);

        if (cap == null) return false;

        return cap.hasOwner() && cap.getOwner() != null && cap.getOwner().equals(player.getUniqueID());

    }

    private boolean hasOwner (Entity horse) {
        CapabilityName cap = horse.getCapability(CapabilityNameHandler.INSTANCE, null);

        if (cap == null) return false;

        return cap.hasOwner();
    }

    // This can result in way too many mods being listed.
    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        return hasOwner(entity);
    }

    public boolean pseudoTaming () {
        return true;
    }

    // Can't tame Animania animals -- OR CAN YOU?
    public boolean isTameable(Entity entity, EntityPlayer player) {
        return false;
    }

    public int tame(Entity entity, EntityPlayer player) {
        return 0;
        /*
        AbstractHorse horse = (AbstractHorse) entity;

        String name = generateName(player);

        if (horse.hasCustomName() && horse.getCustomNameTag().equals(name)) {
            horse.setCustomNameTag("");
        }

        horse.setTamedBy(player);

        if (DWMHConfig.EnchantedCarrot.messages.taming) {
            doGenericMessage(entity, player, "dwmh.strings.animania_taming");
        }

        return 1;*/
    }

    // Foal interactions -> uncertain
    public boolean isAgeable(Entity entity, EntityPlayer player) {
        return false;
    }

    public int age(Entity entity, EntityPlayer player) {
        return 0;
    }

    // Not currently implemented
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        return false;
    }

    public int breed(Entity entity, EntityPlayer player) {
        return 0;
    }

    public boolean isMyMod(Entity entity) {
        if (!(entity instanceof EntityAnimaniaHorse)) return false;

        String clazz = entity.getClass().getName();

        if (DWMH.animaniaClasses.contains(clazz)) return true;

        DWMH.ignoreList.add(clazz);
        return false;
    }

    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        ITextComponent temp = null;

        EntityAnimaniaHorse animal = (EntityAnimaniaHorse) entity;

        CapabilityName cap = animal.getCapability(CapabilityNameHandler.INSTANCE, null);

        if (cap != null && cap.hasOwner() && !cap.getOwner().equals(player.getUniqueID())) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.notyours");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.hasHome() && animal.world.getTileEntity(animal.getHomePosition()) != null) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.working");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.getLeashed()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.leashed");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.isBeingRidden() && animal.isRidingOrBeingRiddenBy(player)) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.isBeingRidden() && DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.summonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_AQUA);
        } else if (cap != null && cap.getOwner().equals(player.getUniqueID())) {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        }

        return temp;
    }

    public String proxyName() {
        return "animania";
    }
}

