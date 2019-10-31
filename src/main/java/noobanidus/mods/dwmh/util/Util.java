package noobanidus.mods.dwmh.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class Util {
  public static CompoundNBT getOrCreateTagCompound(ItemStack stack) {
    CompoundNBT tagCompound = stack.getTag();
    if (tagCompound == null) {
      tagCompound = new CompoundNBT();
      stack.setTag(tagCompound);
    }
    return tagCompound;
  }
}
