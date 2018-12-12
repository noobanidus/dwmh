package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemEnchantedCarrot extends Item {

    public static boolean enabled = DWMH.CONFIG.get("Carrot", "Enabled", true, "Set to false to disable the instant-taming carrot item.").getBoolean(true);
    private static int maxUses = DWMH.CONFIG.get("Carrot", "MaxUses", 30, "Maximum number of uses before the enchanted carrot is destroyed.").getInt(30);
    public static boolean taming = DWMH.CONFIG.get("Carrot", "Taming", true, "Carrot can automatically tame untamed horses.").getBoolean(true);
    public static boolean healing = DWMH.CONFIG.get("Carrot", "Healing", true, "Carrot can fully heal damaged horses.").getBoolean(true);
    public static boolean ageing = DWMH.CONFIG.get("Carrot", "Ageing", true, "Carrot can age child horses into adults instantly.").getBoolean(true);

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


    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        // If using the alternate static texture, instead return true.
        return false;
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
