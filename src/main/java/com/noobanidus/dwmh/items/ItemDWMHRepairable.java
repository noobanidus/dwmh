package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class ItemDWMHRepairable extends Item {
    private String internalRepair;
    private ItemStack defaultRepair = ItemStack.EMPTY;

    @SuppressWarnings("deprecation")
    public static boolean useableItem(ItemStack item) {
        if (item.getItem() instanceof ItemEnchantedCarrot && DWMHConfig.EnchantedCarrot.durability.breakableCarrot) return true;

        if (item.getItemDamage() > item.getMaxDamage()) {
            item.setItemDamage(item.getMaxDamage());
            return false;
        } else if (item.getItemDamage() == item.getMaxDamage()) {
            return false;
        } else {
            return true;
        }
    }

    public static void damageItem(ItemStack item, EntityPlayer player) {
        // Most calls to this should be wrapped in this but it doesn't hurt
        if (player.capabilities.isCreativeMode) return;

        if (item.getItem() instanceof ItemDWMHRepairable) {
            if (useableItem(item)) {
                item.damageItem(1, player);
            }
        } else {
            DWMH.LOG.error(String.format("Attempted to damage a non-DWMH item! |%s|", item.getDisplayName()));
        }
    }

    public void registerPredicate(String predicate_name) {
        addPropertyOverride(new ResourceLocation("dwmh", predicate_name), new IItemPropertyGetter() {
            @Override
            public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (stack.getItem() instanceof ItemEnchantedCarrot) {
                    if (DWMHConfig.EnchantedCarrot.durability.breakableCarrot)
                        return 0;
                    if (stack.getItemDamage() == DWMHConfig.EnchantedCarrot.durability.getMaxUses())
                        return 1;
                    return 0;
                } else if (stack.getItem() instanceof ItemOcarina) {
                    if (DWMHConfig.Ocarina.functionality.getMaxUses() != 0 && stack.getItemDamage() == DWMHConfig.Ocarina.functionality.maxUses)
                        return 1;
                    return 0;
                }

                return 0;
            }
        });
    }

    public void setInternalDefault(ItemStack item) {
        defaultRepair = item;
    }

    public void updateConfig() {
    }

    public String getInternalRepair() {
        return internalRepair;
    }

    public void setInternalRepair(String item) {
        internalRepair = item;
    }

    public ItemStack getRepairItem() {
        ItemStack repairItem = parseItem(getInternalRepair());
        if (repairItem.isEmpty()) return defaultRepair;
        return repairItem;
    }

    public void checkRepairItem() {
        parseItem(getInternalRepair(), null, true);
    }

    public ItemStack parseItem(String intr) {
        return parseItem(intr, null);
    }

    public ItemStack parseItem(String intr, ItemStack def) {
        return parseItem(intr, def, false);
    }

    public ItemStack parseItem(String intr, ItemStack def, Boolean errors) {
        String[] parts = intr.split(":");

        if (def == null) def = ItemStack.EMPTY;

        String defName;

        if (def != null && !def.isEmpty()) {
            defName = def.getDisplayName();
        } else {
            defName = defaultRepair.getDisplayName();
        }

        if (parts.length == 2) {
            parts = new String[]{parts[0], parts[1], "0"};
        }

        Item repairInt = Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));
        if (repairInt == null) {
            if (errors)
                DWMH.LOG.error(String.format("Item specified in configuration does not exist: |%s|. Using default |%s| instead.", intr, defName));
            return def;
        }

        int metadata;
        try {
            metadata = Integer.parseInt(parts[2]);
        } catch (NumberFormatException nfe) {
            if (errors)
                DWMH.LOG.error(String.format("Item metadata is not a valid integer: |%s|. Using default instead of 0 instead.", intr));
            return def;
        }

        return new ItemStack(repairInt, 1, metadata);
    }

    @Override
    public boolean getIsRepairable(ItemStack item, ItemStack repairingItem) {
        ItemStack myRepair = getRepairItem();
        return repairingItem.getItem().equals(myRepair.getItem()) && repairingItem.getMetadata() == myRepair.getMetadata();

    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (stack.getItemDamage() == stack.getMaxDamage()) return false;

        if (stack.getItemDamage() == 0) return false;

        if (stack.getItem() instanceof ItemEnchantedCarrot && DWMHConfig.EnchantedCarrot.durability.breakableCarrot) return true;

        return super.showDurabilityBar(stack);
    }

    @SuppressWarnings("deprecation")
    public int getMaxDamage(ItemStack stack) {
        int max = getMaxDamage();

        if (stack.getItemDamage() > max) {
            stack.setItemDamage(max);
        }

        return max;
    }
}
