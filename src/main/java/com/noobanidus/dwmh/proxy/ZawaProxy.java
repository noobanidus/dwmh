package com.noobanidus.dwmh.proxy;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.items.ItemEnchantedCarrot;
import com.noobanidus.dwmh.items.ItemWhistle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.zawamod.entity.base.ZAWABaseLand;
import org.zawamod.entity.core.AnimalData;
import org.zawamod.init.advancement.Triggers;

public class ZawaProxy implements ISteedProxy {
    ZAWABaseLand.AIFight aifight = null;
    EntityAINearestAttackableTarget ainearatt = null;

    public boolean isTeleportable (Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        return isSaddled((ZAWABaseLand) entity) && globalTeleportCheck(entity, player);
    }

    public boolean isListable (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) {
            return false;
        }

        ZAWABaseLand zawa = (ZAWABaseLand) entity;
        if (zawa.getOwnerId() == null || !zawa.getOwnerId().equals(player.getUniqueID())) {
            return false;
        }

        return true;
    }

    private boolean isSaddled (ZAWABaseLand entity) {
        NBTTagCompound nbt = new NBTTagCompound();
        entity.writeEntityToNBT(nbt);

        if (nbt.hasKey("Saddle")) return nbt.getBoolean("Saddle");

        return false;
    }

    //  These do nothing
    public boolean isTameable (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        ZAWABaseLand animal = (ZAWABaseLand) entity;
        if (animal.isTamed()) return false;

        return true;
    }

    public void tame (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return;

        ZAWABaseLand animal = (ZAWABaseLand) entity;

        animal.setTamedBy(player);
        animal.setOwnerId(player.getUniqueID());
        if (!player.capabilities.isCreativeMode) {
            ItemStack item = player.inventory.getCurrentItem();
            ItemEnchantedCarrot.damageItem(item, player, ItemEnchantedCarrot.unbreakable);
        }

        if (player instanceof EntityPlayerMP) {
            Triggers.TAME_ANIMAL_ZAWA.trigger((EntityPlayerMP)player);
        }

        if (aifight == null || ainearatt == null) {
            aifight = ReflectionHelper.getPrivateValue(ZAWABaseLand.class, animal, "AIFight");
            ainearatt = ReflectionHelper.getPrivateValue(ZAWABaseLand.class, animal, "AINearAtt");
        }

        if (aifight != null && ainearatt != null) {
            animal.tasks.removeTask(aifight);
            animal.targetTasks.removeTask(ainearatt);
        } else {
            DWMH.LOG.error("Unable to remove AI tasks for recently tamed entity.");
        }

        if (animal.setNature() == AnimalData.EnumNature.AGGRESSIVE && player instanceof EntityPlayerMP) {
            Triggers.RISK_TAME.trigger((EntityPlayerMP)player);
        }

        animal.setHunger(animal.getMaxFood());
        animal.setEnrichment(animal.getMaxEnrichment());
        animal.world.setEntityState(animal, (byte)7);

        /*ITextComponent temp = new TextComponentTranslation("dwmh.strings.zawa.tamed");
        temp.appendText(" ");
        if (animal.hasCustomName()) {
            temp.appendText(" " + animal.getCustomNameTag());
        } else {
            temp.appendSibling(new TextComponentTranslation(String.format("entity.%s.name", EntityList.getEntityString(animal))));
        }

        temp.appendText("!");
        temp.getStyle().setColor(TextFormatting.GOLD);

        player.sendMessage(temp);*/
    }

    public boolean isAgeable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void age (Entity entity, EntityPlayer player) { }

    // The healing is by default in the interface

    public boolean isBreedable (Entity entity, EntityPlayer player) {
        return false;
    }

    public void breed (Entity entity, EntityPlayer player) {
    }

    public ITextComponent getResponseKey (Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        ITextComponent temp;

        ZAWABaseLand animal = (ZAWABaseLand) entity;

        if (animal.hasHome() && animal.world.getTileEntity(animal.getHomePosition()) != null) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.working");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (animal.getLeashed()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.leashed");
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
        } else if (isSaddled(animal)) {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        } else {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled");
            temp.getStyle().setColor(TextFormatting.RED);
        }

        return temp;
    }

    public boolean isMyMod (Entity entity) {
        for (Class<?> clz : DWMH.zawaClasses) {
            if (clz.isAssignableFrom(entity.getClass())) {
                return true;
            }
        }

        return false;
    }

    public String proxyName () {
        return "zawa";
    }
}


