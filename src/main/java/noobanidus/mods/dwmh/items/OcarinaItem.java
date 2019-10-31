package noobanidus.mods.dwmh.items;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noobanidus.mods.dwmh.DWMH;
import noobanidus.mods.dwmh.init.SoundRegistry;
import noobanidus.mods.dwmh.util.Eligibility;
import noobanidus.mods.dwmh.util.EntityTracking;
import noobanidus.mods.dwmh.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class OcarinaItem extends Item {
  public static long DELAY = 2000;
  private static Object2LongOpenHashMap<PlayerEntity> lastPlayedMap = new Object2LongOpenHashMap<>();

  static {
    lastPlayedMap.defaultReturnValue(0);
  }

  private static boolean canPlay(PlayerEntity player) {
    long last = lastPlayedMap.getLong(player);
    if ((System.currentTimeMillis() - last) >= DELAY || last == 0) {
      return true;
    }
    return false;
  }

  private static void playSound(PlayerEntity player) {
    playSound(player, false);
  }

  private static void playSound(PlayerEntity player, boolean minor) {
    if (!canPlay(player)) return;

    lastPlayedMap.put(player, System.currentTimeMillis());
    player.world.playSound(null, player.getPosition(), minor ? SoundRegistry.getRandomMinorWhistle() : SoundRegistry.getRandomWhistle(), SoundCategory.PLAYERS, 1f, 1f);
  }

  public OcarinaItem() {
    super(new Item.Properties().maxStackSize(1).rarity(Rarity.UNCOMMON).group(DWMH.ITEM_GROUP));
  }

  public void rightClickEntity(PlayerEntity playerIn, Entity target, ItemStack stack) {
    if (!playerIn.world.isRemote && Eligibility.eligibleToBeTagged(playerIn, target)) {
      UUID owner = EntityTracking.getOwnerForEntity(target);
      if (owner == null) {
        EntityTracking.setOwnerForEntity(playerIn, target);
        CompoundNBT tag = Util.getOrCreateTagCompound(stack);
        tag.putUniqueId("target", target.getUniqueID());
        playSound(playerIn);
      } else {
        EntityTracking.unsetOwnerForEntity(target);
        CompoundNBT tag = Util.getOrCreateTagCompound(stack);
        tag.remove("targetMost");
        tag.remove("targetLeast");
        playSound(playerIn, true);
      }
    }
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    CompoundNBT tag = Util.getOrCreateTagCompound(stack);
    if (!world.isRemote) {
      if (tag.hasUniqueId("target")) {
        UUID entityId = tag.getUniqueId("target");
        int id = EntityTracking.getEntityId(entityId);
        Entity entity = world.getEntityByID(id);
        if (entity != null && entity.getUniqueID().equals(entityId) && entity.isAlive()) {
          // Update the stack
          entity.setPosition(player.posX, player.posY, player.posZ);
          playSound(player);
        }
      }
    }
    return new ActionResult<>(ActionResultType.SUCCESS, stack);
  }

  @Override
  public boolean onEntitySwing(ItemStack stack, LivingEntity entityLiving) {
    return false;
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    if (worldIn == null) return;

    CompoundNBT tag = Util.getOrCreateTagCompound(stack);
    if (tag.hasUniqueId("target")) {
      UUID target = tag.getUniqueId("target");
      int id = EntityTracking.getEntityId(target);
      Entity entity = worldIn.getEntityByID(id);
      tooltip.add(new StringTextComponent(""));
      if (entity != null) {
        tooltip.add(new TranslationTextComponent("dwmh.currently_tracking", entity.getName()));
      } else {
        tooltip.add(new TranslationTextComponent("dwmh.no_target"));
      }
      tooltip.add(new TranslationTextComponent("dwmh.uuid_target", target.toString()).setStyle(new Style().setColor(TextFormatting.GRAY)));
    }
  }
}
