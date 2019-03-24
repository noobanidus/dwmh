package com.noobanidus.dwmh.proxy.steeds;

import com.noobanidus.dwmh.capability.CapabilityOcarina;
import com.noobanidus.dwmh.capability.CapabilityOwner;
import com.noobanidus.dwmh.client.render.particle.ParticleSender;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.util.MessageHandler;
import com.noobanidus.dwmh.util.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

// Instantiated by buildSoftDependProxy
@SuppressWarnings("unused")
public class PigProxy extends VanillaProxy {
    @Override
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        if (!globalTeleportCheck(entity, player)) return false;

        EntityPig pig = (EntityPig) entity;

        return pig.getSaddled() && ownedBy(entity, player);
    }

    @Override
    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        return ownedBy(entity, player);
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
    public boolean isAgeable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        EntityPig pig = (EntityPig) entity;

        return pig.isChild();
    }

    @Override
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        EntityPig pig = (EntityPig) entity;

        return !pig.isChild() && pig.getGrowingAge() == 0 && !pig.isInLove();
    }

    @Override
    public int age(Entity entity, EntityPlayer player) {
        EntityPig pig = (EntityPig) entity;

        pig.setGrowingAge(0);
        ParticleSender.generateParticles(entity, ParticleType.AGING);

        doGenericMessage(entity, player, MessageHandler.Generic.AGING, null, null);

        return 1;
    }

    @Override
    public int breed(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return 0;

        ((EntityPig) entity).setInLove(player);

        ParticleSender.generateParticles(entity, ParticleType.BREEDING);

        doGenericMessage(entity, player, MessageHandler.Generic.BREEDING, null, null);

        return 1;
    }

    @Override
    public boolean isMyMod(Entity entity) {
        return entity instanceof EntityPig;
    }

    @Override
    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        ITextComponent temp = null;

        EntityPig pig = (EntityPig) entity;

        CapabilityOwner cap = capability(entity);

        if (cap != null && cap.hasOwner() && !cap.getOwner().equals(player.getUniqueID())) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.notyours").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (pig.hasHome() && pig.world.getTileEntity(pig.getHomePosition()) != null) {
            return new TextComponentTranslation("dwmh.strings.unsummonable.working").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (!pig.getSaddled()) {
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
        return "vanilla_pig";
    }
}

