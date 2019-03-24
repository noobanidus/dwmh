package com.noobanidus.dwmh.proxy.steeds;

import com.animania.common.entities.horses.EntityAnimaniaHorse;
import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.capability.CapabilityOcarina;
import com.noobanidus.dwmh.capability.CapabilityOwner;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

// Instantiated by buildSoftDependProxy
@SuppressWarnings("unused")
public class AnimaniaProxy implements ISteedProxy {
    @Override
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        EntityAnimaniaHorse horse = (EntityAnimaniaHorse) entity;

        if (!globalTeleportCheck(entity, player)) return false;

        return ownedBy(entity, player);
    }

    @Override
    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        return hasOwner(entity);
    }

    @Override
    public boolean pseudoTaming() {
        return true;
    }

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
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        return false;
    }

    @Override
    public int breed(Entity entity, EntityPlayer player) {
        return 0;
    }

    @Override
    public boolean isMyMod(Entity entity) {
        if (!(entity instanceof EntityAnimaniaHorse)) return false;

        String clazz = entity.getClass().getName();

        if (DWMH.sets("animania").contains(clazz)) return true;

        DWMH.sets("ignore").add(clazz);
        return false;
    }

    @Override
    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        EntityAnimaniaHorse animal = (EntityAnimaniaHorse) entity;

        CapabilityOwner cap = capability(animal);

        if (cap != null && cap.hasOwner() && !cap.getOwner().equals(player.getUniqueID())) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.notyours").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (animal.hasHome() && animal.world.getTileEntity(animal.getHomePosition()) != null) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.working").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (animal.getLeashed()) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.leashed").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (animal.isBeingRidden() && animal.isRidingOrBeingRiddenBy(player)) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.ridden").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (animal.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (animal.isBeingRidden() && DWMHConfig.Ocarina.otherRiders) {
            return new TextComponentTranslation("dwmh.strings.summonable.ridden_other").setStyle(new Style().setColor(TextFormatting.DARK_AQUA));
        } else if (ownedBy(entity, player)) {
            return new TextComponentTranslation("dwmh.strings.summonable").setStyle(new Style().setColor(TextFormatting.AQUA));
        }

        return null;
    }

    @Override
    public String proxyName() {
        return "animania";
    }
}

