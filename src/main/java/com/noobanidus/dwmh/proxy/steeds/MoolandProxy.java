package com.noobanidus.dwmh.proxy.steeds;

import com.legacy.moolands.entities.cow.EntityAwfulCow;
import com.noobanidus.dwmh.capability.CapabilityOcarina;
import com.noobanidus.dwmh.capability.CapabilityOwner;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

// Instantiated by buildSoftDependProxy
@SuppressWarnings("unused")
public class MoolandProxy implements ISteedProxy {
    @Override
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        EntityAwfulCow horse = (EntityAwfulCow) entity;

        if (!globalTeleportCheck(entity, player)) return false;

        CapabilityOwner cap = capability(horse);

        if (cap == null) return false;

        return cap.hasOwner() && cap.getOwner() != null && cap.getOwner().equals(player.getUniqueID());
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
        return entity instanceof EntityAwfulCow;
    }

    @Override
    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        ITextComponent temp = null;

        EntityAwfulCow animal = (EntityAwfulCow) entity;

        CapabilityOwner cap = capability(animal);

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

    @Override
    public String proxyName() {
        return "mooland";
    }
}

