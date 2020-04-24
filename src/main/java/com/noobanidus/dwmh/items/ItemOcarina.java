package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.init.SoundRegistry;
import com.noobanidus.dwmh.util.Eligibility;
import com.noobanidus.dwmh.util.EntityTracking;
import com.noobanidus.dwmh.util.Util;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class ItemOcarina extends Item {
  public static long DELAY = 2000;
  private static Object2LongOpenHashMap<EntityPlayer> lastPlayedMap = new Object2LongOpenHashMap<>();

  static {
    lastPlayedMap.defaultReturnValue(0);
  }

  private static boolean canPlay(EntityPlayer player) {
    long last = lastPlayedMap.getLong(player);
    return (System.currentTimeMillis() - last) >= DELAY || last == 0;
  }

  private static void playSound(EntityPlayer player, boolean minor) {
    if (!canPlay(player)) return;

    lastPlayedMap.put(player, System.currentTimeMillis());
    SoundEvent sound = minor ? SoundRegistry.getRandomMinorWhistle() : SoundRegistry.getRandomWhistle();
    player.world.playSound(null, player.getPosition(), sound, SoundCategory.PLAYERS, 1f, 1f);
  }

  private static void playSound(EntityPlayer player) {
    playSound(player, false);
  }

  public ItemOcarina() {
    setMaxStackSize(1);
  }

  @Nonnull
  @Override
  @SuppressWarnings("deprecation")
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.UNCOMMON;
  }

  public void rightClickEntity(EntityPlayer playerIn, Entity target, ItemStack stack) {
    if (!playerIn.world.isRemote && Eligibility.eligibleToBeTagged(playerIn, target)) {
      EntityPlayerMP player = (EntityPlayerMP) playerIn;
      UUID owner = EntityTracking.getOwnerForEntity(target);
      if (owner == null) {
        NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
        if (tag.hasUniqueId("target")) {
          EntityTracking.unsetOwnerForEntity(tag.getUniqueId("target"));
        }
        EntityTracking.setOwnerForEntity(playerIn, target);
        tag.setUniqueId("target", target.getUniqueID());
        tag.removeTag("name");
/*        GetName packet = new GetName(target.getEntityId());
        Networking.sendTo(packet, playerIn);*/
        playSound(playerIn);
      } else {
        EntityTracking.unsetOwnerForEntity(target);
        NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
        tag.removeTag("targetMost");
        tag.removeTag("targetLeast");
        tag.removeTag("name");
        player.sendAllContents(player.openContainer, player.openContainer.getInventory());
        playSound(playerIn, true);
      }
    }
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
    if (!world.isRemote) {
      if (tag.hasUniqueId("target")) {
        UUID entityId = tag.getUniqueId("target");
        Entity entity = EntityTracking.fetchEntity(entityId);
        if (entity != null && entity.getUniqueID().equals(entityId) && !entity.isDead) {
          // Update the stack
          entity.setPosition(player.posX, player.posY, player.posZ);
          playSound(player);
        }
        EntityTracking.clearEntity(entityId);
      }
    }
    return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
  }

  @Override
  public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flags) {
    if (worldIn == null) return;

    NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
    if (tag.hasKey("name") || tag.hasUniqueId("target")) {
      tooltip.add("");
    }
    if (tag.hasKey("name")) {
      tooltip.add(I18n.format("dwmh.currently_tracking", tag.getString("name")));
    }
    if (tag.hasUniqueId("target")) {
      UUID target = tag.getUniqueId("target");
      tooltip.add(TextFormatting.GRAY + I18n.format("dwmh.uuid_target", target.toString()));
    }
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
  }
}
