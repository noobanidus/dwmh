package com.noobanidus.dwmh.proxy.steeds;

import com.builtbroken.merpig.entity.EntityMerpig;
import com.noobanidus.dwmh.capability.CapabilityOcarina;
import com.noobanidus.dwmh.capability.CapabilityOwner;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class MerpigProxy implements ISteedProxy {
    @Override
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        EntityMerpig animal = (EntityMerpig) entity;

        if (animal.getLeashed() || (animal.isBeingRidden() && animal.isRidingSameEntity(player))) {
            return false;
        }

        // And prevent you from summoning animals being ridden by other players
        if (animal.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            return false;
        }

        return ownedBy(entity, player);
    }

    @Override
    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        return hasOwner(entity) && ownedBy(entity, player);
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
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        return false;
    }

    @Override
    public int age(Entity entity, EntityPlayer player) {
        return 0;
    }

    @Override
    public int breed(Entity entity, EntityPlayer player) {
        return 0;
    }

    @Override
    public boolean isMyMod(Entity entity) {
        return entity instanceof EntityMerpig;
    }

    @Override
    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        EntityMerpig pig = (EntityMerpig) entity;

        CapabilityOwner cap = capability(entity);

        if (cap != null && cap.hasOwner() && !cap.getOwner().equals(player.getUniqueID())) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.notyours").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (!pig.isSaddled()) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (pig.getLeashed()) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.leashed").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (pig.isBeingRidden() && pig.isRidingOrBeingRiddenBy(player)) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.ridden").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (pig.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (pig.isBeingRidden() && DWMHConfig.Ocarina.otherRiders) {
            return new TextComponentTranslation("dwmh.strings.summonable.ridden_other").setStyle(new Style().setColor(TextFormatting.DARK_AQUA));
        } else if (ownedBy(entity, player)) {
            return new TextComponentTranslation("dwmh.strings.summonable").setStyle(new Style().setColor(TextFormatting.AQUA));
        }

        return null;
    }

    @Override
    public String proxyName() {
        return "Merpig";
    }
}
