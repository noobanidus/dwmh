package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemOcarina extends Item {
  private List<TextComponentTranslation> directions = new ArrayList<>();

  public static void onInteractOcarina(PlayerInteractEvent.EntityInteract event) {
    Entity entity = event.getTarget();
    EntityPlayer player = event.getEntityPlayer();
    ItemStack item = event.getItemStack();

    if (item.isEmpty() || !(item.getItem() instanceof ItemOcarina)) {
      return;
    }
  }

  public void init() {
    setMaxStackSize(1);
    setCreativeTab(DWMH.TAB);

    setRegistryName("dwmh:whistle");
    setTranslationKey("dwmh.whistle");

    for (int i = 0; i < 8; i++) {
      directions.add(new TextComponentTranslation(String.format("dwmh.strings.dir.%d", i)));
    }
  }

  @Override
  public boolean isEnchantable(ItemStack stack) {
    return true;
  }

  @Override
  public int getItemEnchantability() {
    return 20;
  }

  @Nonnull
  @Override
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.UNCOMMON;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
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
}
