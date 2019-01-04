package com.noobanidus.dwmh.proxy.steeds;

import com.animania.common.entities.horses.EntityAnimaniaHorse;
import com.noobanidus.dwmh.DWMH;
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
    public boolean isTeleportable (Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        EntityAnimaniaHorse horse = (EntityAnimaniaHorse) entity;

        if (!globalTeleportCheck(entity, player)) return false;

        if (horse.isTame() && horse.getOwnerUniqueId() != null && horse.getOwnerUniqueId().equals(player.getUniqueID())) return true;

        if (!entity.hasCustomName() || horse.isTame()) {
            return false;
        }

        return entity.getCustomNameTag().equals(generateName(player));
    }

    private String generateName (EntityPlayer player) {
        return String.format("%s's Steed", player.getName());
    }

    // This can result in way too many mods being listed.
    public boolean isListable (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        EntityAnimaniaHorse horse = (EntityAnimaniaHorse) entity;

        if (horse.isTame()) return true;

        return entity.hasCustomName();
    }

    // Can't tame Animania animals -- OR CAN YOU?
    public boolean isTameable (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        EntityAnimaniaHorse horse = (EntityAnimaniaHorse) entity;

        return !horse.isTame();
    }

    public void tame (Entity entity, EntityPlayer player) {
        AbstractHorse horse = (AbstractHorse) entity;

        String name = generateName(player);

        if (horse.hasCustomName() && horse.getCustomNameTag().equals(name)) {
            horse.setCustomNameTag("");
        }

        horse.setTamedBy(player);

        if (DWMHConfig.EnchantedCarrot.messages.taming) {
            doGenericMessage(entity, player, "dwmh.strings.animania_taming");
        }
    }

    // Foal interactions -> uncertain
    public boolean isAgeable (Entity entity, EntityPlayer player) {
        return false;
    }
    public void age (Entity entity, EntityPlayer player) { }

    // Not currently implemented
    public boolean isBreedable (Entity entity, EntityPlayer player) {
        return false;
    }
    public void breed (Entity entity, EntityPlayer player) { }

    public boolean isMyMod (Entity entity) {
        if (!(entity instanceof EntityAnimaniaHorse)) return false;

        String clazz = entity.getClass().getName();

        if (DWMH.animaniaClasses.contains(clazz)) return true;

        DWMH.ignoreList.add(clazz);
        return false;
    }

    public ITextComponent getResponseKey (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        ITextComponent temp = null;

        String name = generateName(player);

        EntityAnimaniaHorse animal = (EntityAnimaniaHorse) entity;

        if (animal.isTame() && (animal.getOwnerUniqueId() == null || !animal.getOwnerUniqueId().equals(player.getUniqueID()))) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.notyours");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.hasHome() && animal.world.getTileEntity(animal.getHomePosition()) != null) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.working");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.getLeashed()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.leashed");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (!animal.hasCustomName() && !animal.isTame()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.unnamed");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.hasCustomName() && !animal.getCustomNameTag().equals(name) && !animal.isTame()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.notyours");
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
        } else if (animal.hasCustomName() && animal.getCustomNameTag().equals(name)) {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        } else if (animal.isTame() && animal.getOwnerUniqueId() != null && animal.getOwnerUniqueId().equals(player.getUniqueID())) {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        }

        return temp;
    }

    public String proxyName () {
        return "animania";
    }
}

