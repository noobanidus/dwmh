package com.noobanidus.dwmh.config;

import com.noobanidus.dwmh.items.*;
import com.noobanidus.dwmh.util.Util;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class Registrar {
    public static ItemWhistle whistle = null;
    public static ItemEnchantedCarrot carrot = null;

    public static void preInit() {
        whistle = new ItemWhistle();
        whistle.init();

        carrot = new ItemEnchantedCarrot();
        carrot.init();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(whistle);
        if (ItemEnchantedCarrot.enabled) {
            event.getRegistry().register(carrot);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(whistle, 0, new ModelResourceLocation("dwmh:whistle", "inventory"));
        if (ItemEnchantedCarrot.enabled) {
            ModelLoader.setCustomModelResourceLocation(carrot, 0, new ModelResourceLocation("dwmh:carrot", "inventory"));
        }
    }

    @SubscribeEvent
    public static void onDismount (EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && event.getEntityBeingMounted() instanceof AbstractHorse && ItemWhistle.home && !ItemWhistle.skipDismount) {
            AbstractHorse entity = (AbstractHorse) event.getEntityBeingMounted();
            entity.detachHome();
        }
    }

    @SubscribeEvent
    public static void onInteractCarrot (PlayerInteractEvent.EntityInteract event) {
        if (event.getWorld().isRemote) return;

        ITextComponent temp;

        EntityPlayer player = event.getEntityPlayer();
        ItemStack item  = event.getItemStack();

        if (item.isEmpty() || !(item.getItem() instanceof ItemEnchantedCarrot) || !(event.getTarget() instanceof AbstractHorse)) {
            return;
        }

        AbstractHorse horse = (AbstractHorse) event.getTarget();

        event.setCanceled(true);

        World world = event.getWorld();

        boolean didStuff = false;

        if (horse.isChild() && ItemEnchantedCarrot.ageing && !Util.isAnimania(horse)) {
            if (horse.getEntityData().getBoolean("quark:poison_potato_applied")) {
                temp = new TextComponentTranslation("dwmh.strings.quark_poisoned");
                temp.getStyle().setColor(TextFormatting.GREEN);
                player.sendMessage(temp);
                return;

            } else {
                horse.setGrowingAge(0);
                world.setEntityState(horse, (byte) 7);
                didStuff = true;
            }
        } else if (!horse.isTame() && ItemEnchantedCarrot.taming && !Util.isAnimania(horse)) {
            horse.setTamedBy(player);
            didStuff = true;
        } else if (horse.getHealth() < horse.getMaxHealth() && ItemEnchantedCarrot.healing) {
            horse.heal(horse.getMaxHealth() - horse.getHealth());
            world.setEntityState(horse, (byte)7);
            didStuff = true;
        }

        if (!player.capabilities.isCreativeMode && didStuff) {
            item.damageItem(1, player);
        }

        if (didStuff) {
            if (ItemEnchantedCarrot.warn) {
                if (Util.isAnimania(horse)) {
                    temp = new TextComponentTranslation("dwmh.strings.animania_heal");
                    temp.getStyle().setColor(TextFormatting.YELLOW);
                    player.sendMessage(temp);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onInteractOcarina (PlayerInteractEvent.EntityInteract event) {
        if (event.getWorld().isRemote) return;

        EntityPlayer player = event.getEntityPlayer();
        ItemStack item = event.getItemStack();

        if (item.isEmpty() || !(item.getItem() instanceof ItemWhistle) || !(event.getTarget() instanceof AbstractHorse)) {
            return;
        }

        if (!player.isSneaking()) return;

        AbstractHorse horse = (AbstractHorse) event.getTarget();

        if (!Util.isAnimania(horse)) return;

        event.setCanceled(true);

        ITextComponent temp;
        String name = String.format("%s's Steed", player.getName());
        if (horse.hasCustomName()) {
            if (ItemWhistle.unname) {
                if (horse.getCustomNameTag().equals(name)) {
                    horse.setCustomNameTag("");
                    temp = new TextComponentTranslation("dwmh.strings.unnamed");
                    temp.getStyle().setColor(TextFormatting.YELLOW);
                } else {
                    temp = new TextComponentTranslation("dwmh.strings.not_your_horse");
                    temp.getStyle().setColor(TextFormatting.RED);
                }
                player.sendMessage(temp);
            } else {
                temp = new TextComponentTranslation("dwmh.strings.already_named");
                temp.getStyle().setColor(TextFormatting.RED);
                player.sendMessage(temp);
            }
        } else {
            horse.setCustomNameTag(name);
            temp = new TextComponentTranslation("dwmh.strings.animania_named");
            temp.getStyle().setColor(TextFormatting.GOLD);
            player.sendMessage(temp);
        }
    }
}
