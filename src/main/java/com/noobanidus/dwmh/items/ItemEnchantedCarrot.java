package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemEnchantedCarrot extends ItemDWMHRepairable {
    static {
        if (!DWMHConfig.carrot.effects.taming && !DWMHConfig.carrot.effects.healing && !DWMHConfig.carrot.effects.ageing && !DWMHConfig.carrot.effects.breeding) DWMHConfig.carrot.enabled = false;
    }

    public void init () {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        setRegistryName("dwmh:carrot");
        setUnlocalizedName("dwmh.carrot");
        setMaxDamage(DWMHConfig.carrot.durability.maxUses);
        setInternalRepair(DWMHConfig.carrot.durability.repairItem);
        registerPredicate("carrot_damage");

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
        ITextComponent temp;

        EntityPlayer player = event.getEntityPlayer();
        Entity entity = event.getTarget();
        ItemStack item  = event.getItemStack();

        if (item.isEmpty() || player.isSneaking() || !(item.getItem() instanceof ItemEnchantedCarrot) || !DWMH.proxy.isMyMod(entity)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);

        if (!useableItem(item) || event.getWorld().isRemote) return;

        boolean didStuff = false;

        if (DWMH.proxy.isAgeable(entity, player) && DWMHConfig.carrot.effects.ageing) {
            if (entity.getEntityData().getBoolean("quark:poison_potato_applied")) {
                temp = new TextComponentTranslation("dwmh.strings.quark_poisoned");
                temp.getStyle().setColor(TextFormatting.GREEN);
                player.sendMessage(temp);
                return;
            }

            DWMH.proxy.age(entity, player);
            didStuff = true;
        } else if (DWMH.proxy.isTameable(entity, player) && DWMHConfig.carrot.effects.taming) {
            DWMH.proxy.tame(entity, player);
            didStuff = true;
        } else if (DWMH.proxy.isHealable(entity, player) && DWMHConfig.carrot.effects.healing) {
            DWMH.proxy.heal(entity, player);
            didStuff = true;
        } else if (DWMH.proxy.isBreedable(entity, player) && DWMHConfig.carrot.effects.breeding) {
            DWMH.proxy.breed(entity, player);
            didStuff = true;
        }

        if (!player.capabilities.isCreativeMode && didStuff) {
            damageItem(item, player, DWMHConfig.carrot.durability.unbreakable);
        }

        if (didStuff) {
            event.setCanceled(true);
        }
    }


    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        if (DWMHConfig.carrot.durability.unbreakable && !useableItem(stack)) {
            return false;
        }

        return DWMHConfig.carrot.glint;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
        if(GuiScreen.isShiftKeyDown()) {
            if (DWMHConfig.carrot.durability.unbreakable && !useableItem(par1ItemStack)) {
                stacks.add(TextFormatting.DARK_RED + I18n.format("dwmh.strings.carrot.tooltip.broken"));
            }
            if (DWMHConfig.carrot.effects.taming) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.taming"));
            }
            if (DWMHConfig.carrot.effects.healing) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.healing"));
            }
            if (DWMHConfig.carrot.effects.ageing) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.ageing"));
            }
            if (DWMHConfig.carrot.effects.breeding) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.breeding"));
            }
            stacks.add(TextFormatting.AQUA + I18n.format("dwmh.strings.repair_carrot", getRepairItem().getDisplayName()));
        } else {
            stacks.add(TextFormatting.DARK_GRAY + I18n.format("dwmh.strings.hold_shift"));
        }
    }

}
