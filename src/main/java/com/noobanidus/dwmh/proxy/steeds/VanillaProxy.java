package com.noobanidus.dwmh.proxy.steeds;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

// Always instantiated by default
public class VanillaProxy implements ISteedProxy {
    @Override
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        return horse.isHorseSaddled() && globalTeleportCheck(entity, player);
    }

    @Override
    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        AbstractHorse horse = (AbstractHorse) entity;

        if (DWMHConfig.client.clientOcarina.noLlamas && entity instanceof EntityLlama) return false;

        if (horse.isChild() || !horse.isTame() || horse.dimension != player.dimension) {
            return false;
        }

        return horse.getOwnerUniqueId() != null && horse.getOwnerUniqueId().equals(player.getUniqueID());
    }

    // Carrot
    @Override
    public boolean isTameable(Entity entity, EntityPlayer player) {
        if (!(entity instanceof AbstractHorse)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        if (horse.isChild()) return false;

        if (horse.isTame() && horse.getOwnerUniqueId() == null) return true;

        return !horse.isTame();
    }

    @Override
    public int tame(Entity entity, EntityPlayer player) {
        ((AbstractHorse) entity).setTamedBy(player);

        doGenericMessage(entity, player, Generic.TAMING);

        return 1;
    }

    @Override
    public boolean isAgeable(Entity entity, EntityPlayer player) {
        if (!(entity instanceof AbstractHorse)) {
            return false;
        }

        AbstractHorse horse = (AbstractHorse) entity;

        return horse.isChild();
    }

    @Override
    public int age(Entity entity, EntityPlayer player) {
        AbstractHorse horse = (AbstractHorse) entity;

        horse.setGrowingAge(0);
        horse.world.setEntityState(horse, (byte) 7);

        doGenericMessage(entity, player, Generic.AGING);

        return 1;
    }

    // Not currently implemented
    @Override
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        AbstractHorse horse = (AbstractHorse) entity;

        // Mules are sterile!
        if (horse instanceof EntityMule) return false;

        // As are undead horses.
        if (horse instanceof EntitySkeletonHorse || horse instanceof EntityZombieHorse) return false;

        if (horse.isChild() || horse.getGrowingAge() != 0 || horse.isInLove()) return false;

        return true;
    }

    @Override
    public int breed(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return 0;

        AbstractHorse horse = (AbstractHorse) entity;
        horse.setInLove(player);

        doGenericMessage(entity, player, Generic.BREEDING);

        return 1;
    }

    @Override
    public boolean isMyMod(Entity entity) {
        if (DWMH.sets("ignore").contains(entity.getClass().getName())) return false;

        String clazz = entity.getClass().getName();

        if (!DWMH.proxy("animania").isLoaded() && clazz.contains("animania")) {
            DWMH.sets("ignore").add(clazz);
            return false;
        }

        if (!DWMH.proxy("ultimate_unicorn_mod").isLoaded() && clazz.contains("ultimate_unicorn")) {
            DWMH.sets("ignore").add(clazz);
            return false;
        }

        return entity instanceof AbstractHorse;
    }

    @Override
    public String proxyName() {
        return "vanilla";
    }

    @Override
    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        AbstractHorse horse = (AbstractHorse) entity;

        if (horse.hasHome() && horse.world.getTileEntity(horse.getHomePosition()) != null) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.working").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (horse.getLeashed()) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.leashed").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (!horse.isHorseSaddled()) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (horse.isBeingRidden() && horse.isRidingSameEntity(player)) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.ridden").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (horse.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (horse.isBeingRidden() && DWMHConfig.Ocarina.otherRiders) {
            return new TextComponentTranslation("dwmh.strings.summonable.ridden_other").setStyle(new Style().setColor(TextFormatting.DARK_AQUA));
        } else {
            return new TextComponentTranslation("dwmh.strings.summonable").setStyle(new Style().setColor(TextFormatting.AQUA));
        }
    }
}
