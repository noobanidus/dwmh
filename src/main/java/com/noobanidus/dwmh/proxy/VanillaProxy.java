package com.noobanidus.dwmh.proxy;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

// Always instantiated by default
public class VanillaProxy implements ISteedProxy {
    public boolean isTeleportable (Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        return horse.isHorseSaddled() && globalTeleportCheck(entity, player);
    }

    public boolean isListable (Entity entity, EntityPlayer player) {
        if (entity == null || entity.isDead || !(entity instanceof AbstractHorse)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        if (horse.isChild() || !horse.isTame() || horse.dimension != player.dimension) {
            return false;
        }

        return horse.getOwnerUniqueId() != null && horse.getOwnerUniqueId().equals(player.getUniqueID());
    }

    // Carrot
    public boolean isTameable (Entity entity, EntityPlayer player) {
        if (!(entity instanceof AbstractHorse)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        if (horse.isChild()) return false;

        if (horse.isTame() && horse.getOwnerUniqueId() == null) return true;

        return !horse.isTame();
    }

    public void tame (Entity entity, EntityPlayer player) {
        ((AbstractHorse) entity).setTamedBy(player);
    }

    public boolean isAgeable (Entity entity, EntityPlayer player) {
        if (!(entity instanceof AbstractHorse)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        return horse.isChild();
    }

    public void age (Entity entity, EntityPlayer player) {
        AbstractHorse horse = (AbstractHorse) entity;

        horse.setGrowingAge(0);
        horse.world.setEntityState(horse, (byte)7);
    }

    public boolean isHealable (Entity entity, EntityPlayer player) {
        if (!(entity instanceof AbstractHorse)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        return horse.getHealth() < horse.getMaxHealth();
    }

    public void heal (Entity entity, EntityPlayer player) {
        AbstractHorse horse = (AbstractHorse) entity;

        horse.heal(horse.getMaxHealth() - horse.getHealth());
        horse.world.setEntityState(horse, (byte)7);
    }

    // Not currently implemented
    public boolean isBreedable (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        AbstractHorse horse = (AbstractHorse) entity;

        // Yes, I know this can be simplified
        // but then it becomes unreadable
        if (horse.isChild() || horse.getGrowingAge() != 0 || horse.isInLove()) return false;

        return true;
    }

    public void breed (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return;

        AbstractHorse horse = (AbstractHorse) entity;
        horse.setInLove(player);
    }

    public boolean isMyMod (Entity entity) {
        for (Class<? extends AbstractHorse> clz : DWMH.ignoreList) {
            if (clz.isAssignableFrom(entity.getClass())) {
                return false;
            }
        }

        return entity instanceof AbstractHorse;
    }

    public ITextComponent getResponseKey (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        AbstractHorse horse = (AbstractHorse) entity;
        ITextComponent temp = null;

        if (horse.hasHome() && horse.world.getTileEntity(horse.getHomePosition()) != null) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.working");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (horse.getLeashed()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.leashed");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (!horse.isHorseSaddled()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (horse.isBeingRidden() && horse.isRidingSameEntity(player)) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (horse.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (horse.isBeingRidden() && DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.summonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_AQUA);
        } else {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        }

        return temp;
    }


}
