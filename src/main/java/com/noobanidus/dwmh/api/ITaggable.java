package com.noobanidus.dwmh.api;

import net.minecraft.entity.player.EntityPlayer;

public interface ITaggable {
  boolean canTag(EntityPlayer player);

  boolean isTaggedBy(EntityPlayer player);
}
