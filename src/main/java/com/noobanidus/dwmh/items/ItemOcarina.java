package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.events.EventHandler;
import com.noobanidus.dwmh.init.ConfigHandler;
import com.noobanidus.dwmh.init.SoundRegistry;
import com.noobanidus.dwmh.util.Eligibility;
import com.noobanidus.dwmh.util.EntityTracking;
import com.noobanidus.dwmh.util.Util;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
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
    if (Eligibility.eligibleToBeTagged(playerIn, target)) {
      UUID owner = EntityTracking.getOwnerForEntity(target);
      NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
      if (owner == null) {
        boolean result = EntityTracking.setOwnerForEntity(playerIn, target);
        if (result) {
          tag.setUniqueId("target", target.getUniqueID());
          playerIn.sendStatusMessage(new TextComponentTranslation("dwmh.status.success_setting_owner").setStyle(new Style().setColor(TextFormatting.BLUE)), true);
          playSound(playerIn);
        } else {
          int count = EntityTracking.entityCount(playerIn);
          if (count != ConfigHandler.entityMaximum) {
            playerIn.sendStatusMessage(new TextComponentTranslation("dwmh.status.data_error").setStyle(new Style().setColor(TextFormatting.BLUE)), true);
          } else {
            playerIn.sendStatusMessage(new TextComponentTranslation("dwmh.status.maximum_owned", count, ConfigHandler.entityMaximum).setStyle(new Style().setColor(TextFormatting.BLUE)), true);
          }
          playSound(playerIn, true);
        }
      } else {
        boolean result = EntityTracking.unsetOwnerForEntity(playerIn, target);
        if (result) {
          UUID nextEntity = EntityTracking.nextEntity(playerIn, target.getUniqueID());
          if (nextEntity == null) {
            tag.removeTag("targetMost");
            tag.removeTag("targetLeast");
          } else {
            tag.setUniqueId("target", nextEntity);
          }
          playerIn.sendStatusMessage(new TextComponentTranslation("dwmh.status.success_unsetting_owner").setStyle(new Style().setColor(TextFormatting.BLUE)), true);
        } else {
          playerIn.sendStatusMessage(new TextComponentTranslation("dwmh.status.data_error").setStyle(new Style().setColor(TextFormatting.BLUE)), true);
        }
        playSound(playerIn, true);
      }
      updateOcarinaNBT(stack, playerIn);
    }
  }

  public static void updateOcarinaNBT(ItemStack stack, EntityPlayer player) {
    NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
    tag.setTag("info", EntityTracking.getTrackedDataNBT(player));
    // This is always superfluous but I always worry
    stack.setTagCompound(tag);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
    if (!worldIn.isRemote) {
      WorldServer world = (WorldServer) worldIn;
      UUID entityId = null;
      if (tag.hasKey("targetLeast")) {
        entityId = tag.getUniqueId("target");
      }
      if (player.isSneaking()) {
        UUID newId = EntityTracking.nextEntity(player, entityId);
        if (newId != null) {
          ITextComponent nameComp = Util.resolveName(newId);
          tag.setUniqueId("target", newId);
          // THIS IS OVER COMPLICATING THINGS
          stack.setTagCompound(tag);
          player.sendStatusMessage(new TextComponentTranslation("dwmh.status.now_tracking", nameComp), true);
          playSound(player);
        } else {
          player.sendStatusMessage(new TextComponentTranslation("dwmh.status.no_entities"), true);
          playSound(player, true);
        }
      } else if (entityId != null) {
        Entity entity = EntityTracking.fetchEntity(entityId, world);
        if (entity != null) {
          entity.setPosition(player.posX, player.posY, player.posZ);
          EventHandler.isSpawning = true;
          player.world.spawnEntity(entity);
          EventHandler.isSpawning = false;
          playSound(player);
        }
      } else {
        player.sendStatusMessage(new TextComponentTranslation("dwmh.status.no_entities"), true);
      }
    }
    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
  }

  @Override
  public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flags) {
    NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
    UUID tracked = null;
    if (tag.hasUniqueId("target")) {
      tracked = tag.getUniqueId("target");
    }

    tooltip.add("");
    NBTTagList info = null;

    if (tag.hasKey("info")) {
      info = tag.getTagList("info", Constants.NBT.TAG_COMPOUND);
    }

    if (info != null && !info.isEmpty()) {
      tooltip.add(I18n.format("dwmh.tooltip.tracking_count", info.tagCount()));
      for (int i = 0; i < info.tagCount(); i++) {
        NBTTagCompound entry = info.getCompoundTagAt(i);
        String name = Util.resolveNameClient(entry.getString("name"));
        boolean isTracked = tracked != null && Objects.equals(entry.getUniqueId("entity"), tracked);
        tooltip.add(I18n.format("dwmh.tooltip.tracking_entry", i, name, isTracked ? I18n.format("dwmh.tooltip.tracking_selected") : ""));
      }
    } else {
      tooltip.add(I18n.format("dwmh.tooltip.tracking_empty"));
    }
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
  }
}
