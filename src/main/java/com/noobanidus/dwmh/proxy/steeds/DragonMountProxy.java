package com.noobanidus.dwmh.proxy.steeds;

import com.TheRPGAdventurer.ROTD.DragonMountsConfig;
import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonLifeStageHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.EnumDragonLifeStage;
import com.TheRPGAdventurer.ROTD.util.DMUtils;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.util.StopItDragons;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class DragonMountProxy implements ISteedProxy {
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        EntityTameableDragon dragon = (EntityTameableDragon) entity;

        return dragon.isSaddled() && !dragon.isSitting() && globalTeleportCheck(entity, player);
    }

    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        EntityTameableDragon dragon = (EntityTameableDragon) entity;

        if (!dragon.isTamed() || dragon.dimension != player.dimension) return false;

        return dragon.getOwnerId() != null && dragon.getOwnerId().equals(player.getUniqueID());
    }

    public boolean isTameable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;
        EntityTameableDragon dragon = (EntityTameableDragon) entity;

        return !dragon.isTamed() && !dragon.isEgg();
    }

    public void tame(Entity entity, EntityPlayer player) {
        EntityTameableDragon dragon = (EntityTameableDragon) entity;
        dragon.tamedFor(player, true);

        if (DWMHConfig.EnchantedCarrot.messages.taming) {
            doGenericMessage(entity, player, Generic.TAMING);
        }
    }

    public boolean isAgeable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        // TODO: Config option

        DragonLifeStageHelper helper = ((EntityTameableDragon) entity).getLifeStageHelper();

        return !helper.isAdult();
    }

    public void age(Entity entity, EntityPlayer player) {
        EntityTameableDragon dragon = (EntityTameableDragon) entity;

        DragonLifeStageHelper helper = dragon.getLifeStageHelper();

        EnumDragonLifeStage current = helper.getLifeStage();
        EnumDragonLifeStage next = EnumDragonLifeStage.values()[current.ordinal()+1];

        helper.setLifeStage(next);

        if (DWMHConfig.EnchantedCarrot.messages.aging) {
            doGenericMessage(entity, player, "dwmh.strings.dragonmount.age");
        }
    }

    public boolean isBreedable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        EntityTameableDragon dragon = (EntityTameableDragon) entity;

        if (!dragon.isAdult() || dragon.isInLove()) return false;

        return true;
    }

    public void breed(Entity entity, EntityPlayer player) {
        EntityTameableDragon dragon = (EntityTameableDragon) entity;

        dragon.setInLove(player);
        dragon.world.setEntityState(entity, (byte) 7);

        if (DWMHConfig.EnchantedCarrot.messages.breeding) {
            doGenericMessage(entity, player, Generic.BREEDING);
        }
    }

    public boolean isMyMod(Entity entity) {
        return entity instanceof EntityTameableDragon;
    }

    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        ITextComponent temp;

        EntityTameableDragon dragon = (EntityTameableDragon) entity;

        if (dragon.isChild()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.child");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (dragon.hasHome() && dragon.world.getTileEntity(dragon.getHomePosition()) != null) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.working");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (dragon.getLeashed()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.leashed");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (dragon.isSitting()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.sitting").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (!dragon.isSaddled()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (dragon.isBeingRidden() && dragon.isRidingSameEntity(player)) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (dragon.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (dragon.isBeingRidden() && DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.summonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_AQUA);
        } else {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        }

        return temp;
    }

    @Override
    public void stopIt() {
        ReflectionHelper.setPrivateValue(DMUtils.class, null, StopItDragons.stopIt(DMUtils.getLogger()), "logger");
    }
}
