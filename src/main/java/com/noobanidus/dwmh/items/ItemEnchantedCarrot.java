package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemEnchantedCarrot extends Item {
  public ItemEnchantedCarrot() {
    setMaxStackSize(1);
    setCreativeTab(DWMH.TAB);
    setRegistryName("dwmh:carrot");
    setTranslationKey("dwmh.carrot");
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
  @Override
  public boolean hasEffect(ItemStack stack) {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
  }

}
