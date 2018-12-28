package com.noobanidus.dwmh.proxy;

import com.hackshop.ultimate_unicorn.mobs.EntityKnightVagabond;
import com.hackshop.ultimate_unicorn.mobs.EntityMagicalHorse;
import com.hackshop.ultimate_unicorn.mobs.unique.EntityAleaBringerOfDawn;
import com.hackshop.ultimate_unicorn.mobs.unique.EntityAsmidiske;
import com.hackshop.ultimate_unicorn.mobs.unique.EntityTyphonTheDestroyer;
import com.hackshop.ultimate_unicorn.mobs.unique.EntityVelvetMysticalHealer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@SuppressWarnings("unused")
public class UnicornProxy extends VanillaProxy {
    @Override
    public boolean isMyMod (Entity entity) {
        return entity instanceof EntityMagicalHorse;
    }

    public boolean hasCustomName (Entity entity) {
        if (!entity.hasCustomName()) return false;

        if (entity instanceof EntityAleaBringerOfDawn && entity.getCustomNameTag().equals("Alea")) return false;
        if (entity instanceof EntityTyphonTheDestroyer && entity.getCustomNameTag().equals("Typhon")) return false;
        if (entity instanceof EntityVelvetMysticalHealer && entity.getCustomNameTag().equals("Velvet")) return false;
        if (entity instanceof EntityAsmidiske && entity.getCustomNameTag().equals("Asmidiske")) return false;

        return true;
    }

    @Override
    public boolean isTeleportable (Entity entity, EntityPlayer player) {
        boolean res = super.isListable(entity, player);

        if (!isMyMod(entity)) return res;

        EntityMagicalHorse horse = (EntityMagicalHorse) entity;

        if (horse.isTame() && horse.getOwnerUniqueId() == null) return false;

        return horse.isHorseSaddled() && globalTeleportCheck(entity, player);
    }

    @Override
    public boolean isListable (Entity entity, EntityPlayer player) {
        boolean res = super.isListable(entity, player);

        if (res) return true;

        if (!isMyMod(entity)) return false;

        EntityMagicalHorse horse = (EntityMagicalHorse) entity;

        return horse.isTame() && horse.getOwnerUniqueId() == null;
    }

    @Override
    public ITextComponent getResponseKey (Entity entity, EntityPlayer player) {
        ITextComponent temp = super.getResponseKey(entity, player);

        if (!isMyMod(entity)) return temp;

        EntityMagicalHorse horse = (EntityMagicalHorse) entity;

        if (horse.isTame() && horse.getOwnerUniqueId() == null) {
            List<Entity> riders = horse.getPassengers();
            if (riders.size() >= 1) {
                if (riders.get(0) instanceof EntityKnightVagabond) {
                    temp = new TextComponentTranslation("dwmh.strings.unsummonable.vagabond");
                    temp.getStyle().setColor(TextFormatting.RED);
                    return temp;
                }
            }

            temp = new TextComponentTranslation("dwmh.strings.unsummonable.no_owner");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        }

        return temp;
    }

    @SubscribeEvent
    public static void onVagabondDeath (LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityKnightVagabond && event.getSource().getTrueSource() instanceof EntityPlayer) {
            AbstractHorse horse = (AbstractHorse) event.getEntityLiving().getRidingEntity();
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            if (player != null && horse != null) {
                horse.setTamedBy(player);
                horse.world.setEntityState(horse, (byte) 7);
            }
        }
    }
}
