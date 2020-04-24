package com.noobanidus.dwmh.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Util {
  public static NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
    NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      tagCompound = new NBTTagCompound();
      stack.setTagCompound(tagCompound);
    }
    return tagCompound;
  }
}
