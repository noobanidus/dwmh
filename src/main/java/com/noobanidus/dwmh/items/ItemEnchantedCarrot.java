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
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
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
    public static boolean breeding = DWMH.CONFIG.get("Carrot", "Breeding", true, "Carrot can put horses into breeding mode").getBoolean(true);
    public static boolean unbreakable = DWMH.CONFIG.get("Carrot", "Unbreakable", true, "Set to false to allow the carrot to be completely destroyed when all durability is used up.").getBoolean(true);
    public static String repairItem = DWMH.CONFIG.get("Carrot", "RepairItem", "minecraft:gold_block:0", "When in \"unbreakable\" mode, this item can be used in an anvil to repair the Enchanted Carrot. Format: mod:item:metadata. Items with NBT are not supported, use 0 for no metadata.").getString();
    private static boolean glint = DWMH.CONFIG.get("Carrot", "Glint", false, "Set to true to give the carrot the enchantment glint!").getBoolean(false);

    static {
        if (!taming && !healing && !ageing && !breeding) enabled = false;
    }

    public void init () {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        setRegistryName("dwmh:carrot");
        setUnlocalizedName("dwmh.carrot");
        setMaxDamage(maxUses);
    }

    public ItemStack getRepairItem () {
        if (!unbreakable) return ItemStack.EMPTY;

        String[] parts = repairItem.split(":");
        if (parts.length != 3) {
            DWMH.LOG.error(String.format("Repair item specified in configuration invalid: |%s|", repairItem));
            return ItemStack.EMPTY;
        }

        Item repairInt = Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));
        if (repairInt == null) {
            DWMH.LOG.error(String.format("Repair item specified in configuration does not exist: |%s|", repairItem));
            return ItemStack.EMPTY;
        }

        int metadata;
        try {
            metadata = Integer.parseInt(parts[2]);
        } catch (NumberFormatException nfe) {
            DWMH.LOG.error(String.format("Repair item metadata is not a valid integer: |%s|", repairItem));
            return ItemStack.EMPTY;
        }

        ItemStack repair = new ItemStack(repairInt, 1, metadata);
        return repair;
    }

    @Override
    public boolean getIsRepairable (ItemStack carrot, ItemStack repairingItem) {
        if (!unbreakable) return false;

        if (!(carrot.getItem() instanceof ItemEnchantedCarrot)) return false;

        ItemStack myRepair = getRepairItem();
        if (repairingItem.getItem().equals(myRepair.getItem()) && repairingItem.getMetadata() == myRepair.getMetadata()) {
            return true;
        }

        return false;
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

        if (!useableCarrot(item) || event.getWorld().isRemote) return;

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
        } else if (DWMH.proxy.isBreedable(entity, player) && ItemEnchantedCarrot.breeding) {
            DWMH.proxy.breed(entity, player);
            didStuff = true;
        }

        if (!player.capabilities.isCreativeMode && didStuff) {
            damageCarrot(item, player);
        }

        if (didStuff) {
            event.setCanceled(true);
        }
    }

    public static boolean useableCarrot (ItemStack item) {
        if (unbreakable) {
            if (item.getItemDamage() == maxUses) {
                return false;
            }

            return true;
        } else {
            return true;
        }
    }

    public static void damageCarrot (ItemStack item, EntityPlayer player) {
        // Most calls to this should be wrapped in this but it doesn't hurt
        if (player.capabilities.isCreativeMode) return;

        if (item.getItem() instanceof ItemEnchantedCarrot) {
            if (useableCarrot(item)) {
                item.damageItem(1, player);
            }
        } else {
            DWMH.LOG.error(String.format("Attempted to damage a non-carrot item! |%s|", item.getDisplayName()));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        if (unbreakable && !useableCarrot(stack)) {
            return false;
        }

        return glint;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
        if(GuiScreen.isShiftKeyDown()) {
            if (unbreakable && !useableCarrot(par1ItemStack)) {
                stacks.add(TextFormatting.DARK_RED + I18n.format("dwmh.strings.carrot.tooltip.broken"));
            }
            if (taming) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.taming"));
            }
            if (healing) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.healing"));
            }
            if (ageing) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.ageing"));
            }
            if (breeding) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.carrot.tooltip.breeding"));
            }
        } else {
            stacks.add(TextFormatting.DARK_GRAY + I18n.format("dwmh.strings.hold_shift"));
        }
    }

}
