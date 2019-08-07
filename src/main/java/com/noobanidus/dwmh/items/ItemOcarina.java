package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.init.SoundRegistry;
import com.noobanidus.dwmh.util.Eligibility;
import com.noobanidus.dwmh.util.EntityTracking;
import com.noobanidus.dwmh.util.Util;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
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

  private static boolean canPlay (EntityPlayer player) {
    long last = lastPlayedMap.getLong(player);
    if ((System.currentTimeMillis() - last) >= DELAY || last == 0) {
      return true;
    }
    return false;
  }

  private static void playSound (EntityPlayer player) {
    if (!canPlay(player)) return;

    lastPlayedMap.put(player, System.currentTimeMillis());
    player.world.playSound(null, player.getPosition(), SoundRegistry.getRandomWhistle(), SoundCategory.PLAYERS, 1f, 1f);
  }

  public ItemOcarina() {
    setMaxStackSize(1);
  }

  @Nonnull
  @Override
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.UNCOMMON;
  }

  public void rightClickEntity (EntityPlayer playerIn, Entity target, ItemStack stack) {
    if (Eligibility.eligibleToBeTagged(playerIn, target)) {
      EntityTracking.setOwnerForEntity(playerIn, target);
      NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
      tag.setUniqueId("target", target.getUniqueID());
      playSound(playerIn);
    }
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
    if (!world.isRemote) {
      if (tag.hasKey("targetLeast")) {
        UUID entityId = tag.getUniqueId("target");
        int id = EntityTracking.getEntityId(entityId);
        Entity entity = world.getEntityByID(id);
        if (entity != null && entity.getUniqueID().equals(entityId) && !entity.isDead) {
          // Update the stack
          entity.setPosition(player.posX, player.posY, player.posZ);
          playSound(player);
        }
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
  public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
  }
}
