package com.noobanidus.dwmh.proxy.steeds;

import com.hackshop.ultimate_unicorn.gui.ViewableBreedNames;
import com.hackshop.ultimate_unicorn.mobs.EntityKnightVagabond;
import com.hackshop.ultimate_unicorn.mobs.EntityMagicalHorse;
import com.hackshop.ultimate_unicorn.mobs.unique.EntityAleaBringerOfDawn;
import com.hackshop.ultimate_unicorn.mobs.unique.EntityAsmidiske;
import com.hackshop.ultimate_unicorn.mobs.unique.EntityTyphonTheDestroyer;
import com.hackshop.ultimate_unicorn.mobs.unique.EntityVelvetMysticalHealer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@SuppressWarnings("unused")
public class UnicornProxy extends VanillaProxy {
    @SubscribeEvent
    public static void onVagabondDeath(LivingDeathEvent event) {
        if (!event.getEntityLiving().world.isRemote) {
            if (event.getEntityLiving() instanceof EntityKnightVagabond) {
                AbstractHorse horse = (AbstractHorse) event.getEntityLiving().getRidingEntity();
                Entity killer = event.getSource().getTrueSource();
                if (killer instanceof EntityPlayer && horse != null) {
                    horse.setTamedBy((EntityPlayer) killer);
                    horse.world.setEntityState(horse, (byte) 7);
                } else if (horse != null) {
                    // We don't care about the killer any more as it's not a player
                    horse.setHorseTamed(false);
                    horse.setOwnerUniqueId(null);
                    horse.replaceItemInInventory(400, ItemStack.EMPTY);
                    horse.setHorseSaddled(false);
                    BlockPos pos = horse.getPosition();
                    EntityItem drop = new EntityItem(horse.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.SADDLE));
                    horse.world.spawnEntity(drop);
                }
            }
        }
    }

    @Override
    public boolean isMyMod(Entity entity) {
        return entity instanceof EntityMagicalHorse;
    }

    @Override
    public boolean hasCustomName(Entity entity) {
        if (!entity.hasCustomName()) return false;

        if (entity instanceof EntityAleaBringerOfDawn && entity.getCustomNameTag().equals("Alea")) return false;
        if (entity instanceof EntityTyphonTheDestroyer && entity.getCustomNameTag().equals("Typhon")) return false;
        if (entity instanceof EntityVelvetMysticalHealer && entity.getCustomNameTag().equals("Velvet")) return false;
        if (entity instanceof EntityAsmidiske && entity.getCustomNameTag().equals("Asmidiske")) return false;

        return true;
    }

    @Override
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        boolean res = super.isListable(entity, player);

        if (!isMyMod(entity)) return res;

        EntityMagicalHorse horse = (EntityMagicalHorse) entity;

        if (horse.isTame() && horse.getOwnerUniqueId() == null) return false;

        return horse.isHorseSaddled() && globalTeleportCheck(entity, player);
    }

    @Override
    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        boolean res = super.isListable(entity, player);

        if (res) return true;

        if (!isMyMod(entity)) return false;

        EntityMagicalHorse horse = (EntityMagicalHorse) entity;

        return horse.isTame() && horse.getOwnerUniqueId() == null;
    }

    @Override
    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
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

    @Override
    public ITextComponent getEntityTypeName(Entity entity, EntityPlayer player) {
        // checks for isMyMod have already been made
        EntityMagicalHorse horse = (EntityMagicalHorse) entity;

        if (horse.getClass() == EntityMagicalHorse.class) {
            if (ViewableBreedNames.getSpecialBreedName(horse.getHideType()) != null) {
                return new TextComponentString(ViewableBreedNames.getSpecialBreedName(horse.getHideType()));
            }
        }

        return super.getEntityTypeName(entity, player);
    }

    @Override
    public String proxyName() {
        return "Unicorn";
    }
}
