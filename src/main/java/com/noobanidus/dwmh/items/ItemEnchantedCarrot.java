package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemEnchantedCarrot extends Item {

    public static boolean enabled = DWMH.CONFIG.get("Carrot", "Enabled", true, "Set to false to disable the instant-taming carrot item. Disabling all three functionalities of the carrot has the same effect.").getBoolean(true);
    private static int maxUses = DWMH.CONFIG.get("Carrot", "MaxUses", 30, "Maximum number of uses before the enchanted carrot is destroyed.").getInt(30);
    public static boolean taming = DWMH.CONFIG.get("Carrot", "Taming", true, "Carrot can automatically tame untamed horses.").getBoolean(true);
    public static boolean healing = DWMH.CONFIG.get("Carrot", "Healing", true, "Carrot can fully heal damaged horses.").getBoolean(true);
    public static boolean ageing = DWMH.CONFIG.get("Carrot", "Ageing", true, "Carrot can age child horses into adults instantly.").getBoolean(true);
    private static boolean glint = DWMH.CONFIG.get("Carrot", "Glint", false, "Set to true to give the carrot the enchantment glint!").getBoolean(false);
    public static boolean tameAnimania = DWMH.CONFIG.get("Animania", "TameHorsesWithCarrot", false, "Set to true to enable taming of Animania horses. This allows you to summon them without having to name them, meaning you can set a custom name if you so wish.").getBoolean(false);

    static {
        if (!taming && !healing && !ageing) enabled = false;
    }

    public void init () {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        setRegistryName("dwmh:carrot");
        setUnlocalizedName("dwmh.carrot");
        setMaxDamage(maxUses);
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return false;
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    public static void onInteractCarrot (PlayerInteractEvent.EntityInteract event) {
        if (event.getWorld().isRemote) return;

        ITextComponent temp;

        EntityPlayer player = event.getEntityPlayer();
        Entity entity = event.getTarget();
        ItemStack item  = event.getItemStack();

        if (item.isEmpty() || !(item.getItem() instanceof ItemEnchantedCarrot) || !DWMH.proxy.isMyMod(entity)) {
            return;
        }

        boolean didStuff = false;

        if (DWMH.proxy.isAgeable(entity, player) && ItemEnchantedCarrot.ageing) {
            if (entity.getEntityData().getBoolean("quark:poison_potato_applied")) {
                temp = new TextComponentTranslation("dwmh.strings.quark_poisoned");
                temp.getStyle().setColor(TextFormatting.GREEN);
                player.sendMessage(temp);
                return;
            }

            DWMH.proxy.age(entity, player);
            didStuff = true;
        } else if (DWMH.proxy.isTameable(entity, player) && ItemEnchantedCarrot.taming) {
            DWMH.proxy.tame(entity, player);
            didStuff = true;
        } else if (DWMH.proxy.isHealable(entity, player) && ItemEnchantedCarrot.healing) {
            DWMH.proxy.heal(entity, player);
            didStuff = true;
        }

        if (!player.capabilities.isCreativeMode && didStuff) {
            item.damageItem(1, player);
        }

        if (didStuff) {
            event.setCanceled(true);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return glint;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
        if(GuiScreen.isShiftKeyDown()) {
            if (taming) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.taming"));
            }
            if (healing) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.healing"));
            }
            if (ageing) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.ageing"));
            }
        } else {
            stacks.add(TextFormatting.DARK_GRAY + I18n.format("dwmh.strings.hold_shift"));
        }
    }

}
