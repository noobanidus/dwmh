package com.noobanidus.dwmh.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public class Util {
  public static Style DEFAULT_STYLE = new Style().setColor(TextFormatting.BLUE);

  public static NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
    NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      tagCompound = new NBTTagCompound();
      stack.setTagCompound(tagCompound);
    }
    return tagCompound;
  }

  @Nullable
  public static EntityPlayer resolvePlayer(UUID uuid) {
    PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
    return playerList.getPlayerByUUID(uuid);
  }

  public static ITextComponent resolveName(UUID uuid) {
    ITextComponent result;
    String name = EntityTracking.getName(uuid);
    if (name == null || name.isEmpty()) {
      result = new TextComponentTranslation("dwmh.message.unknown_entity");
    } else if (name.startsWith("entity.")) {
      result = new TextComponentTranslation(name);
    } else {
      result = new TextComponentString(name);
    }
    return result;
  }

  @SideOnly(Side.CLIENT)
  public static String resolveNameClient(String key) {
    String name = key;
    if (key.isEmpty()) {
      name = I18n.format("dwmh.message.unknown_entity");
    } else if (key.startsWith("entity.")) {
      name = I18n.format(key);
    }
    return name;
  }
}
