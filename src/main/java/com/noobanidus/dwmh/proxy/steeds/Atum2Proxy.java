package com.noobanidus.dwmh.proxy.steeds;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.teammetallurgy.atum.entity.animal.EntityCamel;
import com.teammetallurgy.atum.entity.animal.EntityDesertWolf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class Atum2Proxy implements ISteedProxy {
    public boolean isTeleportable(Entity entity, EntityPlayer player) { // TODO
        if (!isListable(entity, player)) {
            return false;
        }

        return Atum2Wrapper.build(entity).isHorseSaddled() && globalTeleportCheck(entity, player);
    }

    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        Atum2Wrapper wrapper = Atum2Wrapper.build(entity);

        if (wrapper.isChild() || !wrapper.isTame() || wrapper.dimension != player.dimension) {
            return false;
        }

        return wrapper.getOwnerUniqueId() != null && wrapper.getOwnerUniqueId().equals(player.getUniqueID());
    }

    // Carrot
    public boolean isTameable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        Atum2Wrapper wrapper = Atum2Wrapper.build(entity);

        if (wrapper.isChild()) return false;

        if (!wrapper.isAlpha()) return false;

        if (wrapper.isTame() && wrapper.getOwnerUniqueId() == null) return true; // TODO can this even happen?

        return !wrapper.isTame();
    }

    public void tame(Entity entity, EntityPlayer player) { // TODO
        if (entity instanceof AbstractHorse) {
            ((AbstractHorse) entity).setTamedBy(player);
        } else {
            EntityDesertWolf wolf = (EntityDesertWolf) entity;

            wolf.setTamedBy(player);
            wolf.getNavigator().clearPath();
            wolf.setAttackTarget(null);
            if (!wolf.isAlpha()) {
                wolf.aiSit.setSitting(true);
            }

            wolf.setHealth(40.0F);
            wolf.playTameEffect(true);
            wolf.world.setEntityState(wolf, (byte) 7);
        }

        if (DWMHConfig.EnchantedCarrot.messages.taming) {
            doGenericMessage(entity, player, Generic.TAMING);
        }
    }

    public boolean isAgeable(Entity entity, EntityPlayer player) { // TODO
        if (!isMyMod(entity)) return false;

        return Atum2Wrapper.build(entity).isChild();
    }

    public void age(Entity entity, EntityPlayer player) { // TODO
        Atum2Wrapper wrapper = Atum2Wrapper.build(entity);

        wrapper.setGrowingAge(0);
        wrapper.world.setEntityState(entity, (byte) 7);

        if (DWMHConfig.EnchantedCarrot.messages.aging) {
            doGenericMessage(entity, player, Generic.AGING);
        }
    }

    // Not currently implemented
    public boolean isBreedable(Entity entity, EntityPlayer player) { // TODO
        if (!isMyMod(entity)) return false;

        Atum2Wrapper wrapper = Atum2Wrapper.build(entity);

        return !wrapper.isChild() && wrapper.getGrowingAge() == 0 && !wrapper.isInLove();
    }

    public void breed(Entity entity, EntityPlayer player) { // TODO
        if (!isMyMod(entity)) return;

        EntityAnimal animal = (EntityAnimal) entity;

        animal.setInLove(player);

        if (DWMHConfig.EnchantedCarrot.messages.breeding) {
            doGenericMessage(entity, player, Generic.BREEDING);
        }
    }

    public boolean isMyMod(Entity entity) {
        if (!(entity instanceof EntityCamel) && !(entity instanceof EntityDesertWolf)) return false;

        String clazz = entity.getClass().getName();

        if (DWMH.atum2Classes.contains(clazz)) return true;

        DWMH.ignoreList.add(clazz);
        return false;
    }

    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        Atum2Wrapper wrapper = Atum2Wrapper.build(entity);

        ITextComponent temp = null;

        if (wrapper.hasHome() && wrapper.world.getTileEntity(wrapper.getHomePosition()) != null) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.working");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (wrapper.getLeashed()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.leashed");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (!wrapper.isHorseSaddled()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (wrapper.isBeingRidden() && wrapper.isRidingSameEntity(player)) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (wrapper.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (wrapper.isBeingRidden() && DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.summonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_AQUA);
        } else {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        }

        return temp;
    }

}
